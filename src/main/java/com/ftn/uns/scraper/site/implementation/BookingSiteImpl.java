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

import java.io.*;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BookingSiteImpl implements Site {

    @Override
    public String createLocationParameter(Location location) {
        String LOCATION_PARAM = "ss";
        try {
            return String.format("%s=%s",LOCATION_PARAM, URLEncoder.encode(location.getCity(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return LOCATION_PARAM;
        }
    }

    @Override
    public String createCheckInParameter(Dates dates) {
        String YEAR_PARAM = "checkin_year";
        String MONTH_PARAM = "checkin_month";
        String DAY_PARAM = "checkin_monthday";
        return String.format("%s=%d&%s=%d&%s=%d", MONTH_PARAM, dates.getCheckInDate().getMonthValue(),
                                                  DAY_PARAM, dates.getCheckInDate().getDayOfMonth(),
                                                    YEAR_PARAM, dates.getCheckInDate().getYear());
    }

    @Override
    public String createCheckOutParameter(Dates dates) {
        String YEAR_PARAM = "checkout_year";
        String MONTH_PARAM = "checkout_month";
        String DAY_PARAM = "checkout_monthday";
        return String.format("%s=%d&%s=%d&%s=%d", MONTH_PARAM, dates.getCheckOutDate().getMonthValue(),
                DAY_PARAM, dates.getCheckOutDate().getDayOfMonth(),
                YEAR_PARAM, dates.getCheckOutDate().getYear());
    }

    @Override
    public String createRoomsParameter(List<Room> rooms) {
        String ROOMS_PARAM = "no_rooms";
        return String.format("%s=%d", ROOMS_PARAM, rooms.size());
    }

    @Override
    public String createAdultsParameter(List<Room> rooms) {
        int adultsCount = 0;
        for(Room room: rooms) adultsCount += room.getAdultsInRoom();

        String ADULTS_PARAM = "group_adults";
        return String.format("%s=%d", ADULTS_PARAM, adultsCount);
    }

    @Override
    public String createChildrenParameter(List<Room> rooms) {
        StringBuilder ages = new StringBuilder();
        String CHILDAGE_PARAM = "age";
        int childrenCount = 0;
        for(Room room: rooms) {
            childrenCount += room.getChildrenInRoom().size();
            for(Integer age: room.getChildrenInRoom()){
                ages.append(CHILDAGE_PARAM);
                ages.append("=");
                ages.append(age);
                if(room.getChildrenInRoom().indexOf(age) < room.getChildrenInRoom().size() - 1
                        || rooms.indexOf(room) < rooms.size() -1) {
                    ages.append("&");
                }
            }
        }

        String CHILDREN_PARAM = "group_children";
        return String.format("%s=%d&%s",CHILDREN_PARAM, childrenCount, ages);
    }

    @Override
    public String createFilterParameter(List<String> filters) {
        return "";
    }

    @Override
    public Iterable<Result> scrapePage(HtmlPage page) {
        try {
            @Cleanup BufferedWriter log = new BufferedWriter(new FileWriter(new File("src/main/resources/site-htmls/booking.html")));
            log.write(page.asXml());
        }catch (IOException io){
            //
        }

        String parentTag = "//div[@class='sr_item_content sr_item_content_slider_wrapper ']";
        String RESULTLINK_XPATH = ".//a[@class='hotel_name_link url']";
        String RESULTPRICE_XPATH = ".//div[@class[contains(., 'entire_row_clickable')]]";
        String RESULTTITLE_XPATH = ".//span[@class[contains(., 'sr-hotel__name')]]";

        List<HtmlElement> tags = page.getByXPath(parentTag);
        List<Result> results = new ArrayList<>();

        for(HtmlElement tag: tags){
            System.out.println(tag.asXml());
            List<HtmlAnchor> anchors = tag.getByXPath(RESULTLINK_XPATH);
            List<HtmlElement> prices = tag.getByXPath(RESULTPRICE_XPATH);
            List<HtmlElement> titles = tag.getByXPath(RESULTTITLE_XPATH);

            if(prices.size() > 0){
                Result result = new Result();
                result.setResultLink("https://www.booking.com/" + anchors.get(0).getHrefAttribute());
                result.setResultPrice(extractPrice(prices.get(0).asText()));
                result.setResultTitle(titles.get(0).asText());
                results.add(result);
            }
        }

        return results;
    }

    @Override
    public Double extractPrice(String price){
        String[] prices = price.split("US\\$");
        NumberFormat format = NumberFormat.getCurrencyInstance();
        try {
            return format.parse("$" + prices[prices.length-1]).doubleValue();
        }catch (ParseException e){
            return 0.0;
        }
    }

    @Override
    public HtmlPage getResultPage(String searchUrl, List<String> filters){
        try {
            SiteFactory.getClient().getCookieManager().clearCookies();
            SiteFactory.getClient().getPage("https://www.booking.com/index.html?" +
                    "label=gen173nr-1FCAEoggJCAlhYSDNYBGjBAYgBAZgBMcIBCndpbmRvd3MgMTDIAQzYAQHoAQH4AQKSAgF5qAID;" +
                    "sid=a20d9f4619c3d0f15a64593f9c932e97;sb_price_type=total&;selected_currency=USD;" +
                    "changed_currency=1;top_currency=1");
            return doFilters(filters, SiteFactory.getClient().getPage(searchUrl));
        }catch (IOException e) {
            return null;
        }
    }

    private HtmlPage doFilters(List<String> filterNames, HtmlPage page) throws IOException{
        FilterMatcher matcher = new FilterMatcher();
        List<Filter> filters = matcher.getFiltersByName(filterNames, SiteType.BOOKING);
        HtmlPage retVal = page;

        for(Filter filter: filters){
            List<HtmlAnchor> filterAnchors = retVal.getByXPath(String.format("//a[@%s='%s']", filter.getFilterParam(), filter.getFilterValue()));
            if(filterAnchors.size() > 0){
                retVal = filterAnchors.get(0).click();
            }
        }

        return retVal;
    }

    @Override
    public String createSearchQueryURL(String location, String checkIn, String checkOut, String rooms, String adults,
                                       String children) {
        String QUERY_ADDRESS = "https://www.booking.com/searchresults.en-us.html";
        return String.format("%s?%s&%s&%s&%s&%s&%s",
                QUERY_ADDRESS, location, checkIn, checkOut, rooms, adults, children);
    }
}
