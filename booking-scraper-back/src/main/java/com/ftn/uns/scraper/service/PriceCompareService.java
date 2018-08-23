package com.ftn.uns.scraper.service;

import com.ftn.uns.scraper.result.Offer;
import com.ftn.uns.scraper.result.Result;
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
            if(hotel.getResultPrice() < cheapest.getResultPrice()){
                cheapest = hotel;
            }
        }

        //sameResults.remove(cheapest);
        return collectPrices(cheapest, sameResults);
    }

    private Result collectPrices(Result cheapest, List<Result> sameResults){
        for(Result result: sameResults){
            Offer offer = new Offer();
            offer.setLink(result.getResultLink());
            offer.setPrice(result.getResultPrice());
            offer.setSite(result.getResultLink().substring(12, result.getResultLink().indexOf(".com/")));
            cheapest.getOffers().add(offer);
        }

        return cheapest;
    }

    private List<Result> findSameResults(List<Result> allHotels, Result original){
        List<Result> sameResults = new ArrayList<>();

        for(Result result: allHotels){
            if(original.getResultTitle().equals(result.getResultTitle())){
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
