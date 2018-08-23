package com.ftn.uns.scraper.site.implementation;
import com.ftn.uns.scraper.result.Result;
import com.ftn.uns.scraper.query.model.Dates;
import com.ftn.uns.scraper.query.model.Filter;
import com.ftn.uns.scraper.query.model.Location;
import com.ftn.uns.scraper.query.model.Room;
import com.ftn.uns.scraper.service.filter.FilterMatcher;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.SiteFactory;
import com.ftn.uns.scraper.site.SiteType;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Cleanup;

import java.io.*;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class ExpediaSiteImpl implements Site {

    @Override
    public String createLocationParameter(Location location) {
        String LOCATION_PARAM = "destination";
        try {
            return String.format("%s=%s",LOCATION_PARAM, URLEncoder.encode(location.getCity(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return LOCATION_PARAM;
        }
    }

    @Override
    public String createCheckInParameter(Dates dates) {
        String CHECKIN_PARAM = "startDate";
        return String.format("%s=%d-%02d-%02d", CHECKIN_PARAM, dates.getCheckInDate().getYear(),
                dates.getCheckInDate().getMonthValue(), dates.getCheckInDate().getDayOfMonth());
    }

    @Override
    public String createCheckOutParameter(Dates dates) {
        String CHECKOUT_PARAM = "endDate";
        return String.format("%s=%d-%02d-%02d", CHECKOUT_PARAM, dates.getCheckOutDate().getYear(),
                dates.getCheckOutDate().getMonthValue(), dates.getCheckOutDate().getDayOfMonth());
    }

    @Override
    public String createRoomsParameter(List<Room> rooms) {
        String ROOMS_PARAM = "rooms";
        return String.format("%s=%d", ROOMS_PARAM, rooms.size());
    }

    @Override
    public String createAdultsParameter(List<Room> rooms) {
        StringBuilder adults = new StringBuilder();
        for(Room room: rooms) {
            adults.append(room.getAdultsInRoom());
            if(rooms.indexOf(room) < rooms.size() -1){
                adults.append(",");
            }
        }

        String ADULTS_PARAM = "adults";
        return String.format("%s=%s", ADULTS_PARAM, adults.toString());
    }

    @Override
    public String createChildrenParameter(List<Room> rooms) {
        StringBuilder ages = new StringBuilder();
        for(Room room: rooms) {
            for(Integer age: room.getChildrenInRoom()){
                ages.append(rooms.indexOf(room));
                ages.append("_");
                ages.append(age);
                if(room.getChildrenInRoom().indexOf(age) < room.getChildrenInRoom().size() - 1
                        || rooms.indexOf(room) < rooms.size() -1) {
                    ages.append(",");
                }
            }
        }

        String CHILDREN_PARAM = "children";
        return String.format("%s=%s",CHILDREN_PARAM, ages.toString());
    }

    @Override
    public String createFilterParameter(List<String> filterNames) {
        FilterMatcher matcher = new FilterMatcher();
        List<Filter> filters = matcher.getFiltersByName(filterNames, SiteType.EXPEDIA);

        StringBuilder retVal = new StringBuilder();
        for(Filter filter: filters){
            if(retVal.toString().contains(filter.getFilterParam())){
                retVal.append(",");
            }else{
                retVal.append("&");
                retVal.append(filter.getFilterParam());
                retVal.append("=");
            }
            retVal.append(filter.getFilterValue());
        }

        return retVal.toString();
    }

    @Override
    public Iterable<Result> scrapePage(HtmlPage page) {
        try {
            @Cleanup BufferedWriter log = new BufferedWriter(new FileWriter(new File("src/main/resources/site-htmls/expedia.html")));
            log.write(page.asXml());
        }catch (IOException io){
            //
        }

        String parentTag = "//div[@class='flex-link-wrap']";
        String RESULTLINK_XPATH = ".//a[@class='flex-link' and starts-with(@href,'https:')]";
        String RESULTFULLPRICE_XPATH = ".//li[@class='total-price-on-card']";
        String RESULTPRICE_XPATH = ".//li[@class='price-breakdown-tooltip price ']";
        String RESULTTITLE_XPATH = ".//h4[@class='hotelName fakeLink']";
        String RESULTCAT_XPATH = ".//li[@class='starRating secondary']";
        String RESULTRATING_XPATH = ".//li[@class='reviewOverall']/span[@aria-hidden='true']";

        List<HtmlElement> tags = page.getByXPath(parentTag);
        List<Result> results = new ArrayList<>();

        for(HtmlElement tag: tags){
            List<HtmlAnchor> links = tag.getByXPath(RESULTLINK_XPATH);
            List<HtmlElement> priceContainers = tag.getByXPath(RESULTFULLPRICE_XPATH);
            List<HtmlElement> titles = tag.getByXPath(RESULTTITLE_XPATH);
            List<HtmlElement> ratings = tag.getByXPath(RESULTRATING_XPATH);
            List<HtmlElement> categories = tag.getByXPath(RESULTCAT_XPATH);

            if(links.size() > 0){
                Result result = new Result();
                result.setResultLink(links.get(0).getHrefAttribute());
                result.setResultTitle(titles.get(0).getTextContent());
                result.setOffers(new ArrayList<>());

                if(priceContainers.size() > 0){
                    result.setResultPrice(extractPrice(priceContainers.get(0).getTextContent()));
                }else{
                    priceContainers = tag.getByXPath(RESULTPRICE_XPATH);
                    if(priceContainers.size() > 0){
                        Double calculatedPrice = extractPrice(priceContainers.get(0).getTextContent());
                        result.setResultPrice(calculatePrice(page.getBaseURL().toString(), calculatedPrice));
                    }else{
                        continue;
                    }
                }

                if(categories.size() > 0){
                    result.setResultCategory(extractStars(categories.get(0).asText()));
                }else{
                    result.setResultCategory(0.0);
                }

                if(ratings.size() > 0) {
                    result.setResultRating(extractRating(ratings.get(0).asText()));
                }else{
                    result.setResultRating(0.0);
                }
                results.add(result);
            }
        }

        return results;
    }

    @Override
    public Double extractPrice(String price) {
        String[] prices = price.split("\\$");
        NumberFormat format = NumberFormat.getCurrencyInstance();
        try {
            return format.parse("$" + prices[prices.length - 1].trim()).doubleValue();
        } catch (ParseException e) {
            return 0.0;
        }
    }

    private Double extractStars(String stars){
        return Double.parseDouble(stars.substring(0, stars.indexOf(" out of")));
    }

    private Double extractRating(String rating){
        return Double.parseDouble(rating.substring(0, rating.indexOf("/")));
    }

    private Double calculatePrice(String pageUrl, Double nightlyPrice){
        LocalDate checkIn = LocalDate.parse(pageUrl.substring(pageUrl.indexOf("startDate")+10, pageUrl.indexOf("startDate")+20));
        LocalDate checkOut = LocalDate.parse(pageUrl.substring(pageUrl.indexOf("endDate")+8, pageUrl.indexOf("endDate")+18));
        String roomsParamPart = pageUrl.substring(pageUrl.indexOf("&rooms=")+7);
        Integer roomsCount = Integer.parseInt(roomsParamPart.substring(0, roomsParamPart.indexOf("&")));
        Period days = checkIn.until(checkOut);
        return days.getDays() * nightlyPrice * roomsCount;
    }

    @Override
    public HtmlPage getResultPage(String searchUrl, List<String> filters){
        try {
            SiteFactory.getClient().getCookieManager().clearCookies();
            HtmlPage page = SiteFactory.getClient().getPage(searchUrl + createFilterParameter(filters));
            Thread.sleep(20000);

            return page;
        }catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String createSearchQueryURL(String location, String checkIn, String checkOut, String rooms, String adults,
                                       String children) {
        String QUERY_ADDRESS = "https://www.expedia.com/Hotel-Search";
        return String.format("%s?%s&%s&%s&%s&%s&%s",
                QUERY_ADDRESS, location, checkIn, checkOut, rooms, adults, children);
    }
}
