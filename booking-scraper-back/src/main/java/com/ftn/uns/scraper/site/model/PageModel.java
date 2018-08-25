package com.ftn.uns.scraper.site.model;

import com.ftn.uns.scraper.site.SiteType;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageModel implements Serializable {

    private SiteType type;
    private int linkIndex;
    private int pageNumber;
}
