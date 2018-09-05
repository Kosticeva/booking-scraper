package com.ftn.uns.scraper.model.filter;

import lombok.Value;

import java.util.List;

@Value
public class FiltersDTO {

    List<FilterDTO> amenityFilters;
    List<FilterDTO> paymentFilters;
    List<FilterDTO> typeFilters;
    List<FilterDTO> starFilters;
    List<FilterDTO> ratingFilters;
    List<FilterDTO> priceFilters;
}
