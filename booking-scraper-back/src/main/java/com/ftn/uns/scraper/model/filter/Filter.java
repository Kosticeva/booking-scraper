package com.ftn.uns.scraper.model.filter;

import com.ftn.uns.scraper.site.SiteType;
import lombok.Data;

@Data
public class Filter {

    private String name;
    private String localName;
    private SiteType site;
    private String parameter;
    private String value;
}
