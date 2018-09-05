package com.ftn.uns.scraper.model.result;

import lombok.Data;

import java.util.List;

@Data
public class Result {

    private String link;
    private Double price;
    private String title;
    private Double category;
    private Double rating;
    private List<Offer> offers;
}
