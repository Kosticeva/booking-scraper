package com.ftn.uns.scraper.query.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchQuery {

    private Location location;
    private Dates dates;
    private List<Room> rooms;
    private List<String> filters;
}
