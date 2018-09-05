package com.ftn.uns.scraper.site.loader;

import com.ftn.uns.scraper.model.filter.Filter;
import com.ftn.uns.scraper.model.query.*;
import com.ftn.uns.scraper.model.query.SearchMarker;
import com.ftn.uns.scraper.service.filter.FilterMatcher;
import com.ftn.uns.scraper.site.SiteFactory;
import com.ftn.uns.scraper.site.SiteLoader;
import com.ftn.uns.scraper.site.SiteType;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class HotelsSiteLoaderImpl implements SiteLoader {

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
        int roomIdx = 0;
        for(Room room: rooms){
            retVal.append(String.format("%s%d-adults=%d", ADULTS_PARAM, roomIdx, room.getAdultsInRoom()));
            if(roomIdx < rooms.size()-1){
                retVal.append("&");
            }
            roomIdx++;
        }

        return retVal.toString();
    }

    @Override
    public String createChildrenParameter(List<Room> rooms) {
        String CHILDREN_PARAM = "q-room-";
        StringBuilder retVal = new StringBuilder();
        int roomIdx = 0;
        for(Room room: rooms){
            retVal.append(String.format("%s%d-children=%d&", CHILDREN_PARAM, roomIdx, room.getChildrenInRoom().size()));

            int childIdx = 0;
            for(Integer age: room.getChildrenInRoom()){
                retVal.append(String.format("%s%d-child-%d-age=%d", CHILDREN_PARAM, roomIdx, childIdx, age));
                if(childIdx < room.getChildrenInRoom().size()-1) {
                    retVal.append("&");
                }

                childIdx++;
            }

            if(roomIdx < rooms.size()-1){
                retVal.append("&");
            }
            roomIdx++;
        }

        return retVal.toString();
    }

    @Override
    public String createFilterParameter(String[] filterNames) {
        FilterMatcher matcher = new FilterMatcher();
        List<Filter> filters = matcher.getFiltersByName(filterNames, SiteType.HOTELS);

        StringBuilder retVal = new StringBuilder();
        for(Filter filter: filters){
            if(retVal.toString().contains(filter.getParameter())){
                retVal.append(",");
            }else{
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
        return String.format(
                "https://www.hotels.com/search.do?%s&%s&%s&%s&%s&%s",
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
            HtmlPage page = SiteFactory.getClient().getPage(url+ createFilterParameter(filters));

            Thread.sleep(5000);

            return page;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO
    @Override
    public HtmlPage turnPage(SearchQuery query, SearchMarker position) {
        return getPage(createSearchQueryURL(query), query.getFilters());
    }
}
