package com.ftn.uns.scraper.service;

import com.ftn.uns.scraper.model.result.Offer;
import com.ftn.uns.scraper.model.result.Result;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PriceCompareService {

    public List<Result> scrapeHotels(List<Result> results, List<Result> finalResults) {
        if (results.size() > 0) {
            List<Result> sameResults = findSameResults(results, results.get(0));
            finalResults.add(findCheapestPrice(sameResults));
            return scrapeHotels(clearCheckedHotels(results, sameResults), finalResults);
        } else {
            return finalResults;
        }
    }

    private Result findCheapestPrice(List<Result> sameResults) {
        Result cheapest = sameResults
                .stream()
                .min(Comparator.comparing(Result::getPrice)).orElseThrow(NoSuchElementException::new);

        return collectPrices(cheapest, sameResults);
    }

    private Result collectPrices(Result cheapest, List<Result> sameResults) {
        sameResults.forEach(result -> {
            Offer offer = new Offer();
            offer.setLink(result.getLink());
            offer.setPrice(result.getPrice());
            offer.setSite(result.getSite());
            cheapest.getOffers().add(offer);
        });

        return cheapest;
    }

    private List<Result> findSameResults(List<Result> allHotels, Result original) {
        return allHotels.stream()
                .filter(result -> original.getTitle().equals(result.getTitle()))
                .collect(Collectors.toList());
    }

    private List<Result> clearCheckedHotels(List<Result> allResults, List<Result> sameResults) {
        allResults.removeAll(sameResults);
        return allResults;
    }
}
