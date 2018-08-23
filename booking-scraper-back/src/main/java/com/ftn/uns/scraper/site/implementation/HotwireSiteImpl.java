package com.ftn.uns.scraper.site.implementation;

import com.ftn.uns.scraper.result.Result;
import com.ftn.uns.scraper.query.model.Dates;
import com.ftn.uns.scraper.query.model.Location;
import com.ftn.uns.scraper.query.model.Room;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.SiteFactory;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Cleanup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


//javascript isnt executed when
//page is called
public class HotwireSiteImpl implements Site {

    @Override
    public String createLocationParameter(Location location) {
        try {
            return URLEncoder.encode(location.getCity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String createCheckInParameter(Dates dates) {
        return String.format("%02d-%02d-%d", dates.getCheckInDate().getMonthValue(),
                dates.getCheckInDate().getDayOfMonth(), dates.getCheckInDate().getYear());
    }

    @Override
    public String createCheckOutParameter(Dates dates) {
        return String.format("%02d-%02d-%d", dates.getCheckOutDate().getMonthValue(),
                dates.getCheckOutDate().getDayOfMonth(), dates.getCheckOutDate().getYear());
    }

    @Override
    public String createRoomsParameter(List<Room> rooms) {
        return "" + rooms.size();
    }

    @Override
    public String createAdultsParameter(List<Room> rooms) {
        int adultsCount = 0;
        for (Room room : rooms) {
            adultsCount += room.getAdultsInRoom();
        }

        return "" + adultsCount;
    }

    @Override
    public String createChildrenParameter(List<Room> rooms) {
        int childCount = 0;
        for (Room room : rooms) {
            childCount += room.getChildrenInRoom().size();
        }

        return "" + childCount;
    }

    @Override
    public String createFilterParameter(List<String> filters) {
        return null;
    }

    @Override
    public Iterable<Result> scrapePage(HtmlPage page) {
        try {
            Thread.sleep(30000);

            @Cleanup BufferedWriter writer = new BufferedWriter(new FileWriter(new File("src/main/resources/site-htmls/hotwire.html")));
            writer.write(page.asXml());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public HtmlPage getResultPage(String searchUrl, List<String> filters) {
        try {
            return SiteFactory.getClient().getPage(searchUrl);
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    @Override
    public String createSearchQueryURL(String location, String checkIn, String checkOut, String rooms, String adults, String children) {
        return String.format(
                "https://www.hotwire.com/hotels/results/%s/%s/%s/%s/%s/%s", location,
                checkIn, checkOut, rooms, adults, children);
    }

    @Override
    public Double extractPrice(String price) {
        return null;
    }
}

// https://www.booking.com/searchresults.html?label=gen173nr-1FCAEoggJCAlhYSDNYBGjBAYgBAZgBMcIBCndpbmRvd3MgMTDIAQzYAQHoAQH4AQKSAgF5qAID;sid=94d6979a931f5a8e1ca3f8e1823604fa;checkin_month=9&checkin_monthday=22&checkin_year=2018&checkout_month=9&checkout_monthday=23&checkout_year=2018&class_interval=1&dest_id=20088325&dest_type=city&dtdisc=0&from_sf=1&group_adults=2&group_children=0&iata=NYC&inac=0&index_postcard=0&label_click=undef&no_rooms=1&offset=0&postcard=0&raw_dest_type=city&room1=A%2CA&sb_price_type=total&search_selected=1&src=index&src_elem=sb&ss=New%20York%2C%20New%20York%20State%2C%20USA&ss_all=0&ss_raw=New%20Y&ssb=empty&sshis=0&

//checked 1star filter (data-id = class-1)

// https://www.booking.com/searchresults.html?label=gen173nr-1FCAEoggJCAlhYSDNYBGjBAYgBAZgBMcIBCndpbmRvd3MgMTDIAQzYAQHoAQH4AQKSAgF5qAID&sid=94d6979a931f5a8e1ca3f8e1823604fa&checkin_month=9&checkin_monthday=22&checkin_year=2018&checkout_month=9&checkout_monthday=23&checkout_year=2018&class_interval=1&dest_id=20088325&dest_type=city&from_sf=1&group_adults=2&group_children=0&iata=NYC&label_click=undef&no_rooms=1&raw_dest_type=city&room1=A%2CA&sb_price_type=total&search_selected=1&src=index&ss=New%20York%2C%20New%20York%20State%2C%20USA&ss_raw=New%20Y&ssb=empty&
// nflt=class%3D1%3B&    -> nflt=class=1;
// rsf=class-1&     ->   rsf=class-1
// lsf=class%7C1%7C3    ->    class|1|3


//checked 1star filter and 2star filter (data-id = class-1, data-id= class-3

// https://www.booking.com/searchresults.html?aid=304142&label=gen173nr-1FCAEoggJCAlhYSDNYBGjBAYgBAZgBMcIBCndpbmRvd3MgMTDIAQzYAQHoAQH4AQKSAgF5qAID&sid=94d6979a931f5a8e1ca3f8e1823604fa&checkin_month=9&checkin_monthday=22&checkin_year=2018&checkout_month=9&checkout_monthday=23&checkout_year=2018&class_interval=1&dest_id=20088325&dest_type=city&dtdisc=0&from_sf=1&group_adults=2&group_children=0&iata=NYC&inac=0&index_postcard=0&label_click=undef&no_rooms=1&postcard=0&raw_dest_type=city&room1=A%2CA&sb_price_type=total&search_selected=1&src=index&ss=New%20York%2C%20New%20York%20State%2C%20USA&ss_all=0&ss_raw=New%20Y&ssb=empty&sshis=0&
// nflt=class%3D1%3Bclass%3D3%3B&    ->   nflt=class=1;class=3;
// rsf=class-3&          ->
// lsf=class%7C3%7C237    ->    lsf=class|3|237

//unchecked 1star filter, only 3star filter data-id=class-3

// https://www.booking.com/searchresults.html?aid=304142&label=gen173nr-1FCAEoggJCAlhYSDNYBGjBAYgBAZgBMcIBCndpbmRvd3MgMTDIAQzYAQHoAQH4AQKSAgF5qAID&sid=94d6979a931f5a8e1ca3f8e1823604fa&checkin_month=9&checkin_monthday=22&checkin_year=2018&checkout_month=9&checkout_monthday=23&checkout_year=2018&class_interval=1&dest_id=20088325&dest_type=city&from_sf=1&group_adults=2&group_children=0&iata=NYC&label_click=undef&no_rooms=1&raw_dest_type=city&room1=A%2CA&sb_price_type=total&search_selected=1&src=index&ss=New%20York%2C%20New%20York%20State%2C%20USA&ss_raw=New%20Y&ssb=empty&
// nflt=class%3D3%3B&    ->    nflt=class=3;
// rdf=class-1