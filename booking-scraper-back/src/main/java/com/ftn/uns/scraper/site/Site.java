package com.ftn.uns.scraper.site;

import com.ftn.uns.scraper.model.query.Dates;
import com.ftn.uns.scraper.model.query.Location;
import com.ftn.uns.scraper.model.query.Room;
import com.ftn.uns.scraper.model.result.Result;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.List;

public interface Site {

    String createLocationParameter(Location location);
    String createCheckInParameter(Dates dates);
    String createCheckOutParameter(Dates dates);
    String createRoomsParameter(List<Room> rooms);
    String createAdultsParameter(List<Room> rooms);
    String createChildrenParameter(List<Room> rooms);
    String createFilterParameter(String[] filters);
    Iterable<Result> scrapePage(HtmlPage page);
    HtmlPage getResultPage(String searchUrl, String[] filters);
    String createSearchQueryURL(String location, String checkIn, String checkOut, String rooms, String adults, String children);
    Double extractPrice(String price);
}
