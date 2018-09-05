package com.ftn.uns.scraper.model.result;

import com.ftn.uns.scraper.model.query.SearchMarker;
import lombok.Data;

import java.util.List;

@Data
public class Results {

    private List<Result> hotels;
    private List<SearchMarker> markers;
}
