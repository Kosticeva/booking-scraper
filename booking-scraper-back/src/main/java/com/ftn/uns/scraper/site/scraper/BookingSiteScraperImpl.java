package com.ftn.uns.scraper.site.scraper;

import com.ftn.uns.scraper.model.query.SearchQuery;
import com.ftn.uns.scraper.model.result.Result;
import com.ftn.uns.scraper.model.result.Results;
import com.ftn.uns.scraper.model.query.SearchMarker;
import com.ftn.uns.scraper.site.SiteScraper;
import com.ftn.uns.scraper.site.SiteType;
import com.ftn.uns.scraper.site.loader.BookingSiteLoaderImpl;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BookingSiteScraperImpl implements SiteScraper {
    
    @Override
    @SneakyThrows
    public Results scrapePage(SearchQuery query) {
        Results results = new Results();
        results.setHotels(new ArrayList<>());
        results.setMarkers(new ArrayList<>());

        SearchMarker bookingMarker = null;
        for(SearchMarker marker: query.getMarkers()){
            if(marker.getType() == SiteType.BOOKING){
                bookingMarker = marker;
                break;
            }
        }

        if(bookingMarker == null) {
            bookingMarker = new SearchMarker();
            bookingMarker.setLinkIndex(0);
            bookingMarker.setPageNumber(0);
            bookingMarker.setType(SiteType.BOOKING);
        }

        BookingSiteLoaderImpl loader = new BookingSiteLoaderImpl();
        HtmlPage page = null;

        while(results.getHotels().size() < 10){
            page = loader.turnPage(query, bookingMarker);

            @Cleanup BufferedWriter wr = new BufferedWriter(new FileWriter(new File("src/main/resources/book.html")));
            wr.write(page.asXml());

            List<HtmlElement> hotels = extractHotels(page);
            if(hotels.size() == 0){
                break;
            }

            int idx =  0;
            for(HtmlElement hotel: hotels){
                if(idx == 10){
                    break;
                }

                Result result = new Result();
                result.setTitle(extractHotelName(hotel));
                result.setLink(extractHotelLink(hotel));
                result.setCategory(extractCategory(hotel));
                result.setPrice(extractPrice(hotel));
                result.setRating(extractRating(hotel));
                result.setType(SiteType.BOOKING);
                result.setOffers(new ArrayList<>());
                results.getHotels().add(result);
                idx++;
            }

            bookingMarker.setLinkIndex(bookingMarker.getLinkIndex()+idx);
        }

        results.getMarkers().add(bookingMarker);
        return results;
    }

    @Override
    public List<HtmlElement> extractHotels(HtmlPage page) {
        String parentTag = "//div[@class='sr_item_content sr_item_content_slider_wrapper ']";
        return page.getByXPath(parentTag);
    }

    @Override
    public String extractHotelName(HtmlElement tag) {
        String RESULTTITLE_XPATH = ".//span[@class[contains(., 'sr-hotel__name')]]";
        List<HtmlElement> titles = tag.getByXPath(RESULTTITLE_XPATH);

        if(titles.size() > 0){
            return titles.get(0).asText();
        }

        return null;
    }

    @Override
    public String extractHotelLink(HtmlElement tag) {
        String RESULTLINK_XPATH = ".//a[@class='hotel_name_link url']";
        List<HtmlAnchor> anchors = tag.getByXPath(RESULTLINK_XPATH);

        if(anchors.size() > 0){
            return "https://www.booking.com" + anchors.get(0).getHrefAttribute();
        }

        return null;
    }

    @Override
    public Double extractPrice(HtmlElement tag) {
        String RESULTPRICE_XPATH = ".//div[@class[contains(., 'js_rackrate_')]]";
        List<HtmlElement> prices = tag.getByXPath(RESULTPRICE_XPATH);
        
        if(prices.size() > 0) {
            String[] priceParts = prices.get(0).asText().split("US\\$");
            NumberFormat format = NumberFormat.getCurrencyInstance();
            try {
                return format.parse("$" + priceParts[priceParts.length - 1]).doubleValue();
            } catch (ParseException e) {
                return 0.0;
            }
        }
        
        return 0.0;
    }

    @Override
    public Double extractCategory(HtmlElement tag) {
        String RESULTCAT_XPATH = ".//i[@class='\nbk-icon-wrapper\nbk-icon-stars\nstar_track\n']/span";
        List<HtmlElement> categories = tag.getByXPath(RESULTCAT_XPATH);

        if(categories.size() > 0) {
            return Double.parseDouble(categories.get(0).asText().substring(0, 1));
        }

        return 0.0;
    }

    @Override
    public Double extractRating(HtmlElement tag) {
        String RESULTRATING_XPATH = ".//span[@class='review-score-badge']";
        List<HtmlElement> ratings = tag.getByXPath(RESULTRATING_XPATH);

        if(ratings.size() > 0){
            return Double.parseDouble(ratings.get(0).asText());
        }

        return 0.0;
    }
}
