package com.ftn.uns.scraper.site.scraper;

import com.ftn.uns.scraper.model.query.SearchQuery;
import com.ftn.uns.scraper.model.result.Result;
import com.ftn.uns.scraper.model.result.Results;
import com.ftn.uns.scraper.model.query.SearchMarker;
import com.ftn.uns.scraper.site.SiteScraper;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.loader.TravelocitySiteLoaderImpl;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class TravelocitySiteScraperImpl implements SiteScraper {

    @Override
    public Results scrapePage(SearchQuery query) {
        Results results = new Results();
        results.setHotels(new ArrayList<>());
        results.setMarkers(new ArrayList<>());

        SearchMarker travelocityMarker = null;
        for (SearchMarker marker : query.getMarkers()) {
            if (marker.getSite() == Site.TRAVELOCITY) {
                travelocityMarker = marker;
                break;
            }
        }

        if (travelocityMarker == null) {
            travelocityMarker = new SearchMarker();
            travelocityMarker.setLinkIndex(0);
            travelocityMarker.setPageNumber(0);
            travelocityMarker.setSite(Site.TRAVELOCITY);
        }

        TravelocitySiteLoaderImpl loader = new TravelocitySiteLoaderImpl();
        HtmlPage page = null;

        while (results.getHotels().size() < 10) {
            page = loader.turnPage(query, travelocityMarker);

            List<HtmlElement> hotels = extractHotels(page);
            if (hotels.size() == 0) {
                break;
            }

            for (HtmlElement hotel : hotels) {
                if (hotels.indexOf(hotel) < travelocityMarker.getLinkIndex()) {
                    continue;
                }

                Result result = new Result();
                result.setTitle(extractHotelName(hotel));
                result.setLink(extractHotelLink(hotel));
                result.setCategory(extractCategory(hotel));
                result.setPrice(extractPrice(hotel));
                result.setRating(extractRating(hotel) * 2);
                result.setSite(Site.TRAVELOCITY);
                result.setOffers(new ArrayList<>());
                results.getHotels().add(result);

                if (hotels.indexOf(hotel) == hotels.size() - 1) {
                    travelocityMarker.setPageNumber(travelocityMarker.getPageNumber() + 1);
                    travelocityMarker.setLinkIndex(0);
                }

                if (results.getHotels().size() == 10) {
                    travelocityMarker.setLinkIndex((hotels.indexOf(hotel)) % hotels.size());
                    break;
                }
            }
        }

        results.getMarkers().add(travelocityMarker);
        return results;
    }

    @Override
    public List<HtmlElement> extractHotels(HtmlPage page) {
        String parentTag = "//div[@class='flex-link-wrap']";
        return page.getByXPath(parentTag);
    }

    @Override
    public String extractHotelName(HtmlElement element) {
        String RESULTTITLE_XPATH = ".//h4[@class='hotelName fakeLink']";
        List<HtmlElement> titles = element.getByXPath(RESULTTITLE_XPATH);

        if (titles.size() > 0) {
            return titles.get(0).getTextContent();
        }

        return null;
    }

    @Override
    public String extractHotelLink(HtmlElement element) {
        String RESULTLINK_XPATH = ".//a[@class='flex-link' and starts-with(@href,'https:')]";

        List<HtmlAnchor> links = element.getByXPath(RESULTLINK_XPATH);
        if (links.size() > 0) {
            return links.get(0).getHrefAttribute();
        }

        return null;
    }

    @Override
    public Double extractPrice(HtmlElement element) {
        String RESULTFULLPRICE_XPATH = ".//li[@class='total-price-on-card']";
        String RESULTPRICE_XPATH = ".//li[@class='price-breakdown-tooltip price ']";

        List<HtmlElement> priceContainers = element.getByXPath(RESULTFULLPRICE_XPATH);
        if (priceContainers.size() == 0) {
            priceContainers = element.getByXPath(RESULTPRICE_XPATH);

            if (priceContainers.size() > 0) {
                return calculatePrice(element.getBaseURI(), parsePrice(priceContainers.get(0).asText()));
            }

            return parsePrice(priceContainers.get(0).asText());
        }

        return 0.0;
    }

    private Double parsePrice(String price) {
        String[] prices = price.split("\\$");
        NumberFormat format = NumberFormat.getCurrencyInstance();
        try {
            return format.parse("$" + prices[prices.length - 1].trim()).doubleValue();
        } catch (ParseException e) {
            return 0.0;
        }
    }

    private Double calculatePrice(String pageUrl, Double nightlyPrice) {
        LocalDate checkIn = LocalDate.parse(pageUrl.substring(pageUrl.indexOf("startDate") + 10, pageUrl.indexOf("startDate") + 20));
        LocalDate checkOut = LocalDate.parse(pageUrl.substring(pageUrl.indexOf("endDate") + 8, pageUrl.indexOf("endDate") + 18));
        String roomsParamPart = pageUrl.substring(pageUrl.indexOf("&rooms=") + 7);
        Integer roomsCount = Integer.parseInt(roomsParamPart.substring(0, roomsParamPart.indexOf("&")));
        Period days = checkIn.until(checkOut);
        return days.getDays() * nightlyPrice * roomsCount;
    }

    @Override
    public Double extractCategory(HtmlElement element) {
        String RESULTCAT_XPATH = ".//li[@class='starRating secondary']";
        List<HtmlElement> categories = element.getByXPath(RESULTCAT_XPATH);

        if (categories.size() > 0) {
            return Double.parseDouble(categories.get(0).asText().substring(0, categories.get(0).asText().indexOf(" out of")));
        }

        return 0.0;
    }

    @Override
    public Double extractRating(HtmlElement element) {
        String RESULTRATING_XPATH = ".//li[@class='reviewOverall']/span[@aria-hidden='true']";
        List<HtmlElement> ratings = element.getByXPath(RESULTRATING_XPATH);

        if (ratings.size() > 0) {
            return Double.parseDouble(ratings.get(0).asText().substring(0, ratings.get(0).asText().indexOf("/")));
        }

        return 0.0;
    }
}
