package com.ftn.uns.scraper.site;

import com.ftn.uns.scraper.model.query.Dates;
import com.ftn.uns.scraper.model.query.Location;
import com.ftn.uns.scraper.model.query.Room;
import com.ftn.uns.scraper.model.query.SearchQuery;
import com.ftn.uns.scraper.model.query.SearchMarker;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.List;

public interface SiteLoader {

    String createLocationParameter(Location location);
    String createCheckInParameter(Dates dates);
    String createCheckOutParameter(Dates dates);
    String createRoomsParameter(List<Room> rooms);
    String createAdultsParameter(List<Room> rooms);
    String createChildrenParameter(List<Room> rooms);
    String createFilterParameter(String[] filters);
    String createSearchQueryURL(SearchQuery query);
    HtmlPage getPage(String url, String[] filters);
    HtmlPage turnPage(SearchQuery query, SearchMarker position);
}
