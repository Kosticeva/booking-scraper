package com.ftn.uns.scraper.service;

import com.ftn.uns.scraper.model.query.SearchQuery;
import com.ftn.uns.scraper.model.result.Results;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.SiteFactory;
import com.ftn.uns.scraper.site.SiteScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SearchService {

    @Autowired
    PriceCompareService comparator;

    public Results getResults(SearchQuery query) {
        Results finalResults = new Results();
        finalResults.setHotels(new ArrayList<>());
        finalResults.setMarkers(new ArrayList<>());

        Site[] sites = Site.values();

        for (Site site : sites) {
            SiteScraper scraper = SiteFactory.getSite(site);
            if (scraper != null) {
                Results results = scraper.scrapePage(query);
                finalResults.getHotels().addAll(results.getHotels());
                finalResults.getMarkers().add(results.getMarkers().get(0));
            }
        }

        finalResults.setHotels(comparator.scrapeHotels(finalResults.getHotels(), new ArrayList<>()));
        return finalResults;
    }
}
