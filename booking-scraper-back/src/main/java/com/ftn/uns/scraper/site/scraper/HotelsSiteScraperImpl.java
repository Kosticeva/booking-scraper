package com.ftn.uns.scraper.site.scraper;

import com.ftn.uns.scraper.model.query.SearchQuery;
import com.ftn.uns.scraper.model.result.Result;
import com.ftn.uns.scraper.model.result.Results;
import com.ftn.uns.scraper.model.query.SearchMarker;
import com.ftn.uns.scraper.site.SiteFactory;
import com.ftn.uns.scraper.site.SiteScraper;
import com.ftn.uns.scraper.site.SiteType;
import com.ftn.uns.scraper.site.loader.HotelsSiteLoaderImpl;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class HotelsSiteScraperImpl implements SiteScraper {
    
    @Override
    @SneakyThrows
    public Results scrapePage(SearchQuery query) {
        Results results = new Results();
        results.setHotels(new ArrayList<>());
        results.setMarkers(new ArrayList<>());

        SearchMarker hotelsMarker = null;
        for(SearchMarker marker: query.getMarkers()){
            if(marker.getType() == SiteType.HOTELS){
                hotelsMarker = marker;
                break;
            }
        }

        if(hotelsMarker == null) {
            hotelsMarker = new SearchMarker();
            hotelsMarker.setLinkIndex(0);
            hotelsMarker.setPageNumber(0);
            hotelsMarker.setType(SiteType.HOTELS);
        }

        if(hotelsMarker.getPageNumber() > 0){
            results.getMarkers().add(hotelsMarker);
            return results;
        }

        HotelsSiteLoaderImpl loader = new HotelsSiteLoaderImpl();
        HtmlPage page = loader.turnPage(query, hotelsMarker);

        @Cleanup BufferedWriter wr = new BufferedWriter(new FileWriter(new File("src/main/resources/hotels.html")));
        wr.write(page.asXml());

        List<HtmlElement> hotels = extractHotels(page);

        for(HtmlElement hotel: hotels) {

            Result result = new Result();
            result.setTitle(extractHotelName(hotel));
            result.setLink(extractHotelLink(hotel));
            result.setRating(extractRating(hotel));
            result.setType(SiteType.HOTELS);
            result.setOffers(new ArrayList<>());

            try {
                HtmlPage hotelPage = SiteFactory.getClient().getPage(result.getLink());
                result.setCategory(extractCategory(hotelPage.getDocumentElement()));
                result.setPrice(extractPrice(hotelPage.getDocumentElement()));
            } catch (IOException e) {
                e.printStackTrace();
                result.setCategory(0.0);
                result.setPrice(0.0);
            }

            results.getHotels().add(result);
        }

        hotelsMarker.setPageNumber(1);

        results.getMarkers().add(hotelsMarker);
        return results;
    }

    @Override
    public List<HtmlElement> extractHotels(HtmlPage page) {
        String parentTag = "//li[@class='hotel' or @class='hotel sponsored' or @class='hotel deal-of-the-day']";
        return page.getByXPath(parentTag);
    }

    @Override
    public String extractHotelName(HtmlElement element) {
        String RESULTTITLE_XPATH = ".//h3[@class='p-name']";
        List<HtmlElement> titles = element.getByXPath(RESULTTITLE_XPATH);

        if(titles.size() > 0){
            return titles.get(0).asText();
        }

        return null;
    }

    @Override
    public String extractHotelLink(HtmlElement element) {
        String RESULTLINK_XPATH = ".//div[@class='price']/a";
        List<HtmlAnchor> anchors = element.getByXPath(RESULTLINK_XPATH);

        if(anchors.size() > 0){
            return "https://www.hotels.com" + anchors.get(0).getHrefAttribute();
        }

        return null;
    }

    @Override
    public Double extractPrice(HtmlElement element) {
        List<HtmlElement> priceTables = element.getByXPath("//div[@class='widget-tooltip-bd']");

        if(priceTables.size() > 0) {
            List<HtmlElement> prices = priceTables.get(0).getByXPath(".//td");

            if (prices.size() > 0) {
                String[] priceParts = prices.get(prices.size() - 1).asText().split("\\$");
                NumberFormat format = NumberFormat.getCurrencyInstance();
                try {
                    return format.parse("$" + priceParts[priceParts.length - 1]).doubleValue();
                } catch (ParseException parse) {
                    return 0.0;
                }
            }
        }else{
            priceTables = element.getByXPath("//div[@class='price']");
            if(priceTables.size() > 0) {
                String[] priceParts = priceTables.get(0).asText().split("\\$");
                NumberFormat format = NumberFormat.getCurrencyInstance();
                try {
                    return format.parse("$" + priceParts[priceParts.length - 1]).doubleValue();
                } catch (ParseException parse) {
                    return 0.0;
                }
            }
        }

        return 0.0;
    }

    @Override
    public Double extractCategory(HtmlElement element) {
        String RESULTCAT_XPATH = "//span[starts-with(@class,'star-rating-text')]";
        List<HtmlElement> categories = element.getByXPath(RESULTCAT_XPATH);

        if(categories.size() > 0) {
            return Double.parseDouble(categories.get(0).asText().substring(0, categories.get(0).asText().indexOf("-")));
        }

        return 0.0;
    }

    @Override
    public Double extractRating(HtmlElement element) {
        String RESULTRATING_XPATH = ".//span[@class='guest-rating-value']";
        List<HtmlElement> ratings = element.getByXPath(RESULTRATING_XPATH);

        if(ratings.size() > 0){
            return Double.parseDouble(ratings.get(0).asText());
        }

        return 0.0;
    }
}
