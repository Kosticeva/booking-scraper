package com.ftn.uns.scraper.query.model;

import com.ftn.uns.scraper.site.SiteType;
import lombok.Data;

@Data
public class Filter {
    private String filterName;
    private SiteType filterSite;
    private String filterParam;
    private String filterValue;

    @Override
    public boolean equals(Object o){
        if(o instanceof Filter){
            if(((Filter) o).getFilterName().equals(filterName) && ((Filter) o).getFilterSite().equals(filterSite)){
                return true;
            }
        }

        return false;
    }
}
