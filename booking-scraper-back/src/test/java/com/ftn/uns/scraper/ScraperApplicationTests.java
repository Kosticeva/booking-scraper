package com.ftn.uns.scraper;

import com.ftn.uns.scraper.query.Result;
import com.ftn.uns.scraper.query.model.Dates;
import com.ftn.uns.scraper.query.model.Location;
import com.ftn.uns.scraper.query.model.Room;
import com.ftn.uns.scraper.query.model.SearchQuery;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.SiteFactory;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Cleanup;
import okio.Buffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ScraperApplicationTests {

	@Test
	public void testSiteFactoryAndScrapingForResults() throws IOException {
        SearchQuery query = new SearchQuery();

        Location loc = new Location();
        loc.setCity("Dublin, Ireland");
        query.setLocation(loc);

        Dates dates = new Dates();
        LocalDate in = LocalDate.of(2018,9,15);
        LocalDate out = LocalDate.of(2018, 9, 17);
	    dates.setCheckInDate(in);
	    dates.setCheckOutDate(out);
	    query.setDates(dates);

        Room r1 = new Room();
        r1.setAdultsInRoom(1);
        List<Integer> r1c = new ArrayList<>();
        r1c.add(5);
        r1.setChildrenInRoom(r1c);

        Room r2 = new Room();
        r2.setAdultsInRoom(1);
        List<Integer> r2c = new ArrayList<>();
        r2c.add(10);
        r2c.add(7);
        r2.setChildrenInRoom(r2c);

        List<Room> rooms = new ArrayList<>();
        rooms.add(r1);
        rooms.add(r2);
        query.setRooms(rooms);

        query.setFilters(new ArrayList<>());
        query.getFilters().add("Kitchen");

        File dir = new File("src/main/resources/booking-site-sources");
        File[] sites = dir.listFiles();
        List<Result> results = new ArrayList<>();

        for(File file: sites) {
            Site site = SiteFactory.getSite(file);

            String searchQuery = site.createSearchQueryURL(site.createLocationParameter(query.getLocation()),
                    site.createCheckInParameter(query.getDates()), site.createCheckOutParameter(query.getDates()),
                    site.createRoomsParameter(query.getRooms()), site.createAdultsParameter(query.getRooms()),
                    site.createChildrenParameter(query.getRooms()));
            HtmlPage result = site.getResultPage(searchQuery, query.getFilters());
            results.addAll((Collection<? extends Result>)site.scrapePage(result));
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("src/main/resources/result.txt")));
        for(Result result: results){
            writer.write(result.toString());
            writer.newLine();
        }

        writer.close();
    }

}
