package com.ftn.uns.scraper.service;

import com.ftn.uns.scraper.query.model.Filter;
import com.ftn.uns.scraper.query.model.Filters;
import com.ftn.uns.scraper.site.SiteType;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FilterMatcher {

    public List<Filter> getFiltersByName(List<String> filterNames, SiteType type){
        List<Filter> siteFilters = getFiltersFromSite(type);
        List<Filter> retVal = new ArrayList<>();

        for(String filterName: filterNames){
            Filter filter = new Filter();
            filter.setFilterName(filterName);
            filter.setFilterSite(type);
            if(siteFilters.contains(filter)){
                retVal.add(siteFilters.get(siteFilters.indexOf(filter)));
            }
        }

        return retVal;
    }

    private List<Filter> getFiltersFromSite(SiteType site){
        try{
            FileReader reader = new FileReader(new File("src/main/resources/filters."+site.name().toLowerCase()+".yaml"));
            Yaml yaml = new Yaml();
            return collectFilters(yaml.loadAs(reader, Filters.class));
        }catch (FileNotFoundException | YAMLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
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
