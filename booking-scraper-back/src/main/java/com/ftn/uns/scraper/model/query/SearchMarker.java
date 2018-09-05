package com.ftn.uns.scraper.model.query;

import com.ftn.uns.scraper.site.SiteType;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchMarker implements Serializable {

    private SiteType type;
    private int linkIndex;
    private int pageNumber;
}
