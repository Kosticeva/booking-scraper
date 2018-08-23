package com.ftn.uns.scraper.web.resource;

import com.ftn.uns.scraper.query.model.Filters;
import com.ftn.uns.scraper.query.model.SearchQuery;
import com.ftn.uns.scraper.service.filter.FilterFactory;
import com.ftn.uns.scraper.service.SearchService;
import com.ftn.uns.scraper.site.SiteType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.ftn.uns.scraper.result.Result;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "localhost:4200")
public class SearchResource {

    private SearchService searchService;

    public SearchResource(final SearchService searchService){
        this.searchService = searchService;
    }

    @PostMapping(value="/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Result> getHotels(@RequestBody SearchQuery query){
        return searchService.getResults(query);
    }

    @GetMapping(value="/filters", produces = MediaType.APPLICATION_JSON_VALUE)
    public Filters getFilters(){
        try{
            return FilterFactory.getFilters(SiteType.BOOKING);
        }catch (FileNotFoundException e){
            Filters filters = new Filters();
            filters.setPaymentFilters(new ArrayList<>());
            filters.setAmenityFilters(new ArrayList<>());
            filters.setPriceFilters(new ArrayList<>());
            filters.setRatingFilters(new ArrayList<>());
            filters.setStarFilters(new ArrayList<>());
            filters.setTypeFilters(new ArrayList<>());

            return filters;
        }
    }
}
