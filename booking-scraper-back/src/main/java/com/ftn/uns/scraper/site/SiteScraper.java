package com.ftn.uns.scraper.site;

import com.ftn.uns.scraper.model.query.SearchQuery;
import com.ftn.uns.scraper.model.result.Results;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.List;

public interface SiteScraper {

    Results scrapePage(SearchQuery query);

    List<HtmlElement> extractHotels(HtmlPage page);

    String extractHotelName(HtmlElement element);

    String extractHotelLink(HtmlElement element);

    Double extractPrice(HtmlElement element);

    Double extractCategory(HtmlElement element);

    Double extractRating(HtmlElement element);
}
