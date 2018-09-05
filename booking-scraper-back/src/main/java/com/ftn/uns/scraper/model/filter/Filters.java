package com.ftn.uns.scraper.model.filter;

import lombok.Data;

import java.util.List;

@Data
public class Filters {

    List<Filter> amenityFilters;
    List<Filter> paymentFilters;
    List<Filter> typeFilters;
    List<Filter> starFilters;
    List<Filter> ratingFilters;
    List<Filter> priceFilters;
}
