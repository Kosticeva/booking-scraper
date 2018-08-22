package com.ftn.uns.scraper.query;

import lombok.Data;

@Data
public class Result {

    private String resultLink;
    private Double resultPrice;
    private String resultTitle;

    @Override
    public String toString(){
        return String.format("<result>\n\t<title>%s</title>\n\t<link>%s</link>\n\t<price>$%.2f</price>\n</result>", resultTitle, resultLink, resultPrice);
    }
}
