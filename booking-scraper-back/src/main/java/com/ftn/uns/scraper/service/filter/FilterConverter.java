package com.ftn.uns.scraper.service.filter;

import com.ftn.uns.scraper.model.filter.FilterDTO;
import com.ftn.uns.scraper.model.filter.Filters;
import com.ftn.uns.scraper.model.filter.FiltersDTO;

import java.util.ArrayList;

public class FilterConverter {

    public FiltersDTO convertToDTO(Filters filters) {
        FiltersDTO filtersDTO = new FiltersDTO(new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        filters.getAmenityFilters().forEach(filter ->
                filtersDTO.getAmenityFilters().add(new FilterDTO(filter.getName(), filter.getLocalName())));

        filters.getPaymentFilters().forEach(filter ->
                filtersDTO.getPaymentFilters().add(new FilterDTO(filter.getName(), filter.getLocalName())));

        filters.getPriceFilters().forEach(filter ->
                filtersDTO.getPriceFilters().add(new FilterDTO(filter.getName(), filter.getLocalName())));

        filters.getRatingFilters().forEach(filter ->
                filtersDTO.getRatingFilters().add(new FilterDTO(filter.getName(), filter.getLocalName())));

        filters.getStarFilters().forEach(filter ->
                filtersDTO.getStarFilters().add(new FilterDTO(filter.getName(), filter.getLocalName())));

        filters.getTypeFilters().forEach(filter ->
                filtersDTO.getTypeFilters().add(new FilterDTO(filter.getName(), filter.getLocalName())));

        return filtersDTO;
    }
}
