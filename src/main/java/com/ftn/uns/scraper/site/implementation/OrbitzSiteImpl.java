package com.ftn.uns.scraper.site.implementation;

import com.ftn.uns.scraper.query.Result;
import com.ftn.uns.scraper.query.model.Dates;
import com.ftn.uns.scraper.query.model.Location;
import com.ftn.uns.scraper.query.model.Room;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.SiteFactory;
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

public class OrbitzSiteImpl implements Site{

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
    public String createFilterParameter(List<String> filters) {
        return "";
    }

    @Override
    public Iterable<Result> scrapePage(HtmlPage page) {
        try {
            @Cleanup BufferedWriter log = new BufferedWriter(new FileWriter(new File("src/main/resources/site-htmls/orbitz.html")));
            log.write(page.asXml());
        }catch (IOException io){
            //
        }

        String RESULTLINK_XPATH = "//a[@class='flex-link' and starts-with(@href,'https:')]";
        String RESULTFULLPRICE_XPATH = "//li[@class='total-price-on-card']";
        String RESULTPRICE_XPATH = "//li[@class='price-breakdown-tooltip price ']";
        String RESULTTITLE_XPATH = "//h4[@class='hotelName fakeLink']";

        List<HtmlAnchor> links = page.getByXPath(RESULTLINK_XPATH);
        List<HtmlElement> priceContainers = page.getByXPath(RESULTFULLPRICE_XPATH);
        List<HtmlElement> titles = page.getByXPath(RESULTTITLE_XPATH);
        List<Result> results = new ArrayList<>();

        if(links.size() == priceContainers.size()) {
            for (HtmlAnchor anchor : links) {
                Result result = new Result();
                result.setResultLink(anchor.getHrefAttribute());
                result.setResultPrice(extractPrice(priceContainers.get(links.indexOf(anchor)).getTextContent()));
                result.setResultTitle(titles.get(links.indexOf(anchor)).getTextContent());
                results.add(result);
            }
        }else{
            priceContainers = page.getByXPath(RESULTPRICE_XPATH);

            if(links.size() == priceContainers.size()){
                for (HtmlAnchor anchor : links) {
                    Result result = new Result();
                    result.setResultLink(anchor.getHrefAttribute());
                    Double calculatedPrice = extractPrice(priceContainers.get(links.indexOf(anchor)).getTextContent());
                    result.setResultPrice(calculatePrice(page.getBaseURL().toString(), calculatedPrice));
                    result.setResultTitle(titles.get(links.indexOf(anchor)).getTextContent());
                    results.add(result);
                }
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
            HtmlPage page = SiteFactory.getClient().getPage(searchUrl);

            try{
                Thread.sleep(20000);
            }catch (Exception e){

            }
            return page;

        }catch (IOException io){
            //
        }

        return null;
    }

    @Override
    public String createSearchQueryURL(String location, String checkIn, String checkOut, String rooms, String adults,
                                       String children) {
        String QUERY_ADDRESS = "https://www.orbitz.com/Hotel-Search";
        return String.format("%s?%s&%s&%s&%s&%s&%s",
                QUERY_ADDRESS, location, checkIn, checkOut, rooms, adults, children);
    }
}
