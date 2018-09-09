package com.ftn.uns.scraper.model.query;

import com.ftn.uns.scraper.site.Site;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchMarker implements Serializable {

    private Site site;
    private int linkIndex;
    private int pageNumber;
}
