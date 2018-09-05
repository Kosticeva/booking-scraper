package com.ftn.uns.scraper.model.query;

import lombok.Data;

import java.util.List;

@Data
public class SearchQuery {

    private Location location;
    private Dates dates;
    private List<Room> rooms;
    private String[] filters;
    private List<SearchMarker> markers;
}
