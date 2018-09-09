package com.ftn.uns.scraper.model.result;

import com.ftn.uns.scraper.site.Site;
import lombok.Data;

@Data
public class Offer {

    private String link;
    private Site site;
    private Double price;
}
