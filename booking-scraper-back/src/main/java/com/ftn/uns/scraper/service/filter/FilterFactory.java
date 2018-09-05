package com.ftn.uns.scraper.service.filter;

import com.ftn.uns.scraper.model.filter.Filter;
import com.ftn.uns.scraper.model.filter.FilterDTO;
import com.ftn.uns.scraper.model.filter.Filters;
import com.ftn.uns.scraper.model.filter.FiltersDTO;
import com.ftn.uns.scraper.site.SiteType;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class FilterFactory {

    public static Filters getFilters(SiteType site) throws FileNotFoundException {
        try {
            FileReader reader = new FileReader(new File("src/main/resources/filters/filters." + site.name().toLowerCase() + ".yaml"));
            Yaml yaml = new Yaml();
            return yaml.loadAs(reader, Filters.class);
        }catch (YAMLException e){
            throw new FileNotFoundException();
        }
    }
    
    public static FiltersDTO convertToDTO(Filters filters){
        FiltersDTO filtersDTO = new FiltersDTO(new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        
        for(Filter filter: filters.getAmenityFilters()){
            filtersDTO.getAmenityFilters().add(new FilterDTO(filter.getName(), filter.getLocalName()));
        }

        for(Filter filter: filters.getPaymentFilters()){
            filtersDTO.getPaymentFilters().add(new FilterDTO(filter.getName(), filter.getLocalName()));
        }

        for(Filter filter: filters.getPriceFilters()){
            filtersDTO.getPriceFilters().add(new FilterDTO(filter.getName(), filter.getLocalName()));
        }

        for(Filter filter: filters.getRatingFilters()){
            filtersDTO.getRatingFilters().add(new FilterDTO(filter.getName(), filter.getLocalName()));
        }

        for(Filter filter: filters.getStarFilters()){
            filtersDTO.getStarFilters().add(new FilterDTO(filter.getName(), filter.getLocalName()));
        }

        for(Filter filter: filters.getTypeFilters()){
            filtersDTO.getTypeFilters().add(new FilterDTO(filter.getName(), filter.getLocalName()));
        }

        return filtersDTO;
    }
            
}
