package com.ftn.uns.scraper.result;

import lombok.Data;

import java.util.List;

@Data
public class Result {

    private String resultLink;
    private Double resultPrice;
    private String resultTitle;
    private Double resultCategory;
    private Double resultRating;
    private List<Offer> offers;

    @Override
    public String toString(){
        return String.format("<result>\n\t<title>%s</title>\n\t<link>%s</link>\n\t<price>$%.2f</price>" +
                "\n\t<rating>%.1f</rating>\n\t<category>%.1f</category>\n\t<offers>%d</offers>\n</result>",
                resultTitle, resultLink, resultPrice, resultRating, resultCategory, offers.size());
    }
}
