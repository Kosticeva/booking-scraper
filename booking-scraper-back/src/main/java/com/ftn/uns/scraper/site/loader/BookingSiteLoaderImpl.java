package com.ftn.uns.scraper.site.loader;

import com.ftn.uns.scraper.model.filter.Filter;
import com.ftn.uns.scraper.model.query.*;
import com.ftn.uns.scraper.model.query.SearchMarker;
import com.ftn.uns.scraper.service.filter.FilterMatcher;
import com.ftn.uns.scraper.site.SiteFactory;
import com.ftn.uns.scraper.site.SiteLoader;
import com.ftn.uns.scraper.site.SiteType;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;

public class BookingSiteLoaderImpl implements SiteLoader {

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
    public String createFilterParameter(String[] filters) {
        return "";
    }


    @Override
    public String createSearchQueryURL(SearchQuery query) {
        String QUERY_ADDRESS = "https://www.booking.com/searchresults.en-us.html";
        return String.format("%s?%s&%s&%s&%s&%s&%s",
                QUERY_ADDRESS,
                createLocationParameter(query.getLocation()),
                createCheckInParameter(query.getDates()),
                createCheckOutParameter(query.getDates()),
                createRoomsParameter(query.getRooms()),
                createAdultsParameter(query.getRooms()) ,
                createChildrenParameter(query.getRooms()));
    }

    @Override
    public HtmlPage getPage(String url, String[] filters) {
        try {
            SiteFactory.getClient().getCookieManager().clearCookies();
            SiteFactory.getClient().getPage("https://www.booking.com/index.html?" +
                    "label=gen173nr-1FCAEoggJCAlhYSDNYBGjBAYgBAZgBMcIBCndpbmRvd3MgMTDIAQzYAQHoAQH4AQKSAgF5qAID;" +
                    "sid=a20d9f4619c3d0f15a64593f9c932e97;sb_price_type=total&;selected_currency=USD;" +
                    "changed_currency=1;top_currency=1");

            return doFilters(filters, SiteFactory.getClient().getPage(url));
        }catch (IOException e) {
            return null;
        }
    }

    @SneakyThrows
    private HtmlPage doFilters(String[] filterNames, HtmlPage page) throws IOException {
        FilterMatcher matcher = new FilterMatcher();
        List<Filter> filters = matcher.getFiltersByName(filterNames, SiteType.BOOKING);
        HtmlPage retVal = page;

        for(Filter filter: filters){
            List<HtmlAnchor> filterAnchors = retVal.getByXPath(String.format("//a[@%s='%s']", filter.getParameter(), filter.getValue()));
            if(filterAnchors.size() > 0){
                retVal = filterAnchors.get(0).click();
            }
        }

        return retVal;
    }

    @Override
    public HtmlPage turnPage(SearchQuery query, SearchMarker position) {
        String searchQuery = createSearchQueryURL(query);
        searchQuery += "&offset="+position.getLinkIndex();
        return getPage(searchQuery, query.getFilters());
    }
}
