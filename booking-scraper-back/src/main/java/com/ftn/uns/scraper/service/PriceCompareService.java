package com.ftn.uns.scraper.service;

import com.ftn.uns.scraper.model.result.Offer;
import com.ftn.uns.scraper.model.result.Result;
import com.ftn.uns.scraper.site.SiteType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PriceCompareService {

    public List<Result> scrapeHotels(List<Result> results, List<Result> finalResults){
        if(results.size() > 0){
            List<Result> sameResults = findSameResults(results, results.get(0));
            finalResults.add(findCheapestPrice(sameResults));
            return scrapeHotels(clearCheckedHotels(results, sameResults), finalResults);
        }else{
            return finalResults;
        }
    }

    private Result findCheapestPrice(List<Result> sameResults){
        Result cheapest = sameResults.get(0);
        for(Result hotel: sameResults){
            if(hotel.getPrice() < cheapest.getPrice()){
                cheapest = hotel;
            }
        }

        return collectPrices(cheapest, sameResults);
    }

    private Result collectPrices(Result cheapest, List<Result> sameResults){
        for(Result result: sameResults){
            Offer offer = new Offer();
            offer.setLink(result.getLink());
            offer.setPrice(result.getPrice());
            offer.setSite(result.getType());
            cheapest.getOffers().add(offer);
        }

        return cheapest;
    }

    private List<Result> findSameResults(List<Result> allHotels, Result original){
        List<Result> sameResults = new ArrayList<>();

        for(Result result: allHotels){
            if(original.getTitle().equals(result.getTitle())){
                sameResults.add(result);
            }
        }

        return sameResults;
    }

    private List<Result> clearCheckedHotels(List<Result> allResults, List<Result> sameResults){
        allResults.removeAll(sameResults);
        return allResults;
    }
}
