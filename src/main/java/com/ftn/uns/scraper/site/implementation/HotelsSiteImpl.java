package com.ftn.uns.scraper.site.implementation;

import com.ftn.uns.scraper.query.Result;
import com.ftn.uns.scraper.query.model.Dates;
import com.ftn.uns.scraper.query.model.Filter;
import com.ftn.uns.scraper.query.model.Location;
import com.ftn.uns.scraper.query.model.Room;
import com.ftn.uns.scraper.service.FilterMatcher;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.SiteFactory;
import com.ftn.uns.scraper.site.SiteType;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Cleanup;
import okio.Buffer;

import java.io.*;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class HotelsSiteImpl implements Site {

    @Override
    public String createLocationParameter(Location location) {
        String LOCATION_PARAM = "q-destination";
        try{
            return String.format("%s=%s", LOCATION_PARAM, URLEncoder.encode(location.getCity(), "UTF-8"));
        }catch (UnsupportedEncodingException e){
            return null;
        }
    }

    @Override
    public String createCheckInParameter(Dates dates) {
        String CHECKIN_PARAM = "q-check-in";
        return String.format("%s=%d-%02d-%02d", CHECKIN_PARAM, dates.getCheckInDate().getYear(),
                dates.getCheckInDate().getMonthValue(), dates.getCheckInDate().getDayOfMonth());
    }

    @Override
    public String createCheckOutParameter(Dates dates) {
        String CHECKOUT_PARAM = "q-check-out";
        return String.format("%s=%d-%02d-%02d", CHECKOUT_PARAM, dates.getCheckOutDate().getYear(),
                dates.getCheckOutDate().getMonthValue(), dates.getCheckOutDate().getDayOfMonth());
    }

    @Override
    public String createRoomsParameter(List<Room> rooms) {
        String ROOMS_PARAM = "q-rooms";
        return String.format("%s=%d", ROOMS_PARAM, rooms.size());
    }

    @Override
    public String createAdultsParameter(List<Room> rooms) {
        String ADULTS_PARAM = "q-room-";
        StringBuilder retVal = new StringBuilder();
        for(Room room: rooms){
            retVal.append(String.format("%s%d-adults=%d", ADULTS_PARAM, rooms.indexOf(room), room.getAdultsInRoom()));
            if(rooms.indexOf(room) < rooms.size()-1){
                retVal.append("&");
            }
        }

        return retVal.toString();
    }

    @Override
    public String createChildrenParameter(List<Room> rooms) {
        String CHILDREN_PARAM = "q-room-";
        StringBuilder retVal = new StringBuilder();
        for(Room room: rooms){
            retVal.append(String.format("%s%d-children=%d&", CHILDREN_PARAM, rooms.indexOf(room), room.getChildrenInRoom().size()));

            for(Integer age: room.getChildrenInRoom()){
                retVal.append(String.format("%s%d-child-%d-age=%d", CHILDREN_PARAM, rooms.indexOf(room), room.getChildrenInRoom().indexOf(age), age));
                if(room.getChildrenInRoom().indexOf(age) < room.getChildrenInRoom().size()-1) {
                    retVal.append("&");
                }
            }

            if(rooms.indexOf(room) < rooms.size()-1){
                retVal.append("&");
            }
        }

        return retVal.toString();
    }

    @Override
    public String createFilterParameter(List<String> filterNames) {
        FilterMatcher matcher = new FilterMatcher();
        List<Filter> filters = matcher.getFiltersByName(filterNames, SiteType.HOTELS);

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
            @Cleanup BufferedWriter log = new BufferedWriter(new FileWriter(new File("src/main/resources/site-htmls/hotels.html")));
            log.write(page.asXml());
        }catch (IOException io){
            //
        }

        String RESULTLINK_XPATH = "//li[@class='hotel' or @class='hotel sponsored']";
        String RESULTPRICE_XPATH = ".//div[@class='price']/a";
        List<Result> results = new ArrayList<>();

        List<HtmlElement> tags = page.getByXPath(RESULTLINK_XPATH);
        for(HtmlElement tag: tags){
            List<HtmlAnchor> anchors = tag.getByXPath(RESULTPRICE_XPATH);
            Result result = new Result();
            result.setResultLink("https://www.hotels.com" + anchors.get(0).getHrefAttribute());

            List<HtmlElement> titles = tag.getByXPath(".//h3[@class='p-name']");
            result.setResultTitle(titles.get(0).asText());

            try {
                HtmlPage hotelPage = SiteFactory.getClient().getPage("https://www.hotels.com"+anchors.get(0).getHrefAttribute());
                List<HtmlElement> priceTables = hotelPage.getByXPath("//div[@class='widget-tooltip-bd']");
                List<HtmlElement> price = priceTables.get(0).getByXPath(".//td");
                result.setResultPrice(extractPrice(price.get(price.size() - 1).asText()));

                results.add(result);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return results;
    }

    @Override
    public Double extractPrice(String price){
        String[] prices = price.split("\\$");
        NumberFormat format = NumberFormat.getCurrencyInstance();
        try {
            return format.parse("$" + prices[prices.length-1]).doubleValue();
        }catch (ParseException parse){
            return 0.0;
        }
    }

    @Override
    public HtmlPage getResultPage(String searchUrl, List<String> filters) {
        try {
            SiteFactory.getClient().getCookieManager().clearCookies();
            return SiteFactory.getClient().getPage(searchUrl + createFilterParameter(filters));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String createSearchQueryURL(String location, String checkIn, String checkOut, String rooms, String adults, String children) {
        return String.format(
                "https://www.hotels.com/search.do?%s&%s&%s&%s&%s&%s", location, checkIn, checkOut, rooms, adults, children);
    }
}
