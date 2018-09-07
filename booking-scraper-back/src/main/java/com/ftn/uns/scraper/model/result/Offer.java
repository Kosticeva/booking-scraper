package com.ftn.uns.scraper.model.result;

import com.ftn.uns.scraper.site.SiteType;
import lombok.Data;

@Data
public class Offer {

    private String link;
    private SiteType site;
    private Double price;
}
