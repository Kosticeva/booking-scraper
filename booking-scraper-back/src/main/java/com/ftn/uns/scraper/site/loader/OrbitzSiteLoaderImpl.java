package com.ftn.uns.scraper.site.loader;

import com.ftn.uns.scraper.model.filter.Filter;
import com.ftn.uns.scraper.model.query.*;
import com.ftn.uns.scraper.model.query.SearchMarker;
import com.ftn.uns.scraper.service.filter.FilterMatcher;
import com.ftn.uns.scraper.site.ClientFactory;
import com.ftn.uns.scraper.site.SiteLoader;
import com.ftn.uns.scraper.site.Site;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class OrbitzSiteLoaderImpl implements SiteLoader {

    @Override
    public String createLocationParameter(Location location) {
        String LOCATION_PARAM = "destination";
        try {
            return String.format("%s=%s", LOCATION_PARAM, URLEncoder.encode(location.getCity(), "UTF-8"));
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
        for (Room room : rooms) {
            adults.append(room.getAdultsInRoom());
            if (rooms.indexOf(room) < rooms.size() - 1) {
                adults.append(",");
            }
        }

        String ADULTS_PARAM = "adults";
        return String.format("%s=%s", ADULTS_PARAM, adults.toString());
    }

    @Override
    public String createChildrenParameter(List<Room> rooms) {
        StringBuilder ages = new StringBuilder();
        for (Room room : rooms) {
            for (Integer age : room.getChildrenInRoom()) {
                ages.append(rooms.indexOf(room));
                ages.append("_");
                ages.append(age);
                if (room.getChildrenInRoom().indexOf(age) < room.getChildrenInRoom().size() - 1
                        || rooms.indexOf(room) < rooms.size() - 1) {
                    ages.append(",");
                }
            }
        }

        String CHILDREN_PARAM = "children";
        return String.format("%s=%s", CHILDREN_PARAM, ages.toString());
    }

    @Override
    public String createFilterParameter(String[] filterNames) {
        FilterMatcher matcher = new FilterMatcher();
        List<Filter> filters = matcher.getFiltersByName(filterNames, Site.EXPEDIA);

        StringBuilder retVal = new StringBuilder();
        for (Filter filter : filters) {
            if (retVal.toString().contains(filter.getParameter())) {
                retVal.append(",");
            } else {
                retVal.append("&");
                retVal.append(filter.getParameter());
                retVal.append("=");
            }
            retVal.append(filter.getValue());
        }

        return retVal.toString();
    }

    @Override
    public String createSearchQueryURL(SearchQuery query) {
        String QUERY_ADDRESS = "https://www.orbitz.com/Hotel-Search";
        return String.format("%s?%s&%s&%s&%s&%s&%s",
                QUERY_ADDRESS,
                createLocationParameter(query.getLocation()),
                createCheckInParameter(query.getDates()),
                createCheckOutParameter(query.getDates()),
                createRoomsParameter(query.getRooms()),
                createAdultsParameter(query.getRooms()),
                createChildrenParameter(query.getRooms()));
    }

    @Override
    public HtmlPage getPage(String url, String[] filters) {
        try {
            ClientFactory.getClient().getCookieManager().clearCookies();
            HtmlPage page = ClientFactory.getClient().getPage(url + createFilterParameter(filters));
            Thread.sleep(20000);

            return page;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public HtmlPage turnPage(SearchQuery query, SearchMarker position) {
        String queryURL = createSearchQueryURL(query);
        queryURL += "&pages=" + position.getPageNumber();
        return getPage(queryURL, query.getFilters());
    }
}
