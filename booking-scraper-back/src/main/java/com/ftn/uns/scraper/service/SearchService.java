package com.ftn.uns.scraper.service;

import com.ftn.uns.scraper.model.query.SearchQuery;
import com.ftn.uns.scraper.model.result.Results;
import com.ftn.uns.scraper.site.SiteFactory;
import com.ftn.uns.scraper.site.SiteScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
public class SearchService {

    @Autowired
    PriceCompareService comparator;

    public Results getResults(SearchQuery query){

        File dir = new File("src/main/resources/sites/");
        File[] sites = dir.listFiles();
        Results finalResults = new Results();
        finalResults.setHotels(new ArrayList<>());
        finalResults.setMarkers(new ArrayList<>());

        for(File file: sites) {
            SiteScraper scraper = SiteFactory.getSite(file);
            Results results = scraper.scrapePage(query);
            finalResults.getHotels().addAll(results.getHotels());
            finalResults.getMarkers().add(results.getMarkers().get(0));
            /*Site site = new AgodaSiteImpl();
            String searchQuery = site.createSearchQueryURL(site.createLocationParameter(query.getLocation()),
                    site.createCheckInParameter(query.getDates()), site.createCheckOutParameter(query.getDates()),
                    site.createRoomsParameter(query.getRooms()), site.createAdultsParameter(query.getRooms()),
                    site.createChildrenParameter(query.getRooms()));
            HtmlPage result = site.getResultPage(searchQuery, query.getFilters());
            finalResults.getHotels().addAll((Collection<? extends Result>)site.scrapePage(result));*/
        }

        finalResults.setHotels(comparator.scrapeHotels(finalResults.getHotels(), new ArrayList<>()));
        return finalResults;
    }
}
