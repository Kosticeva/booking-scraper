package com.ftn.uns.scraper.service;

import com.ftn.uns.scraper.query.Result;
import com.ftn.uns.scraper.query.model.SearchQuery;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.SiteFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SearchService {

    public List<Result> getResults(SearchQuery query){

        File dir = new File("src/main/resources/booking-site-sources");
        File[] sites = dir.listFiles();
        List<Result> results = new ArrayList<>();

        for(File file: sites) {
            Site site = SiteFactory.getSite(file);

            results.addAll((Collection<? extends Result>)site.scrapePage(site.getResultPage(site.createSearchQueryURL(site.createLocationParameter(query.getLocation()),
                    site.createCheckInParameter(query.getDates()), site.createCheckOutParameter(query.getDates()),
                    site.createRoomsParameter(query.getRooms()), site.createAdultsParameter(query.getRooms()),
                    site.createChildrenParameter(query.getRooms())), query.getFilters())));
        }

        return results;
    }
}
