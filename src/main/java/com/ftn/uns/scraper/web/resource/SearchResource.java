package com.ftn.uns.scraper.web.resource;

import com.ftn.uns.scraper.query.model.SearchQuery;
import com.ftn.uns.scraper.service.SearchService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.ftn.uns.scraper.query.Result;

import java.util.List;

@RestController
public class SearchResource {

    private SearchService searchService;

    public SearchResource(final SearchService searchService){
        this.searchService = searchService;
    }

    @PostMapping(value = "/api/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Result> getHotels(@RequestBody SearchQuery query){
        return searchService.getResults(query);
    }
}
