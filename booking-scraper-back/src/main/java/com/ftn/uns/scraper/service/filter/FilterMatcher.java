package com.ftn.uns.scraper.service.filter;

import com.ftn.uns.scraper.model.filter.Filter;
import com.ftn.uns.scraper.model.filter.Filters;
import com.ftn.uns.scraper.site.SiteType;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FilterMatcher {

    public List<Filter> getFiltersByName(String[] filterNames, SiteType type){
        List<Filter> siteFilters = new ArrayList<>();
        try{
            siteFilters = collectFilters(FilterFactory.getFilters(type));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        List<Filter> retVal = new ArrayList<>();

        for(String filterName: filterNames){
            Filter filter = new Filter();
            filter.setName(filterName);
            filter.setSite(type);
            if(siteFilters.contains(filter)){
                retVal.add(siteFilters.get(siteFilters.indexOf(filter)));
            }
        }

        return retVal;
    }

    private List<Filter> collectFilters(Filters filters){
        List<Filter> retVal = new ArrayList<>();

        retVal.addAll(filters.getAmenityFilters());
        retVal.addAll(filters.getPaymentFilters());
        retVal.addAll(filters.getPriceFilters());
        retVal.addAll(filters.getRatingFilters());
        retVal.addAll(filters.getStarFilters());
        retVal.addAll(filters.getTypeFilters());

        return retVal;
    }
}
