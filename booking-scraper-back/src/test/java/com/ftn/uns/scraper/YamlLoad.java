package com.ftn.uns.scraper;

import com.ftn.uns.scraper.query.model.Filters;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class YamlLoad {

    public Filters loadBooking() throws IOException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileReader(new File("src/main/resources/filters.booking.yaml")), Filters.class);
    }
    

    public Filters loadExpedia() throws IOException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileReader(new File("src/main/resources/filters.expedia.yaml")), Filters.class);
    }
    

    public Filters loadHotels() throws IOException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileReader(new File("src/main/resources/filters.hotels.yaml")), Filters.class);
    }
    
    @Test
    public void loadAll() throws IOException {
        Filters booking =  loadBooking();
        Filters expedia = loadExpedia();
        Filters hotels = loadHotels();
        Filters allFilters = new Filters();
        
        allFilters.setAmenityFilters(new ArrayList<>());
        allFilters.getAmenityFilters().addAll(booking.getAmenityFilters());
        allFilters.getAmenityFilters().addAll(expedia.getAmenityFilters());
        allFilters.getAmenityFilters().addAll(hotels.getAmenityFilters());
        
        allFilters.setPaymentFilters(new ArrayList<>());
        allFilters.getPaymentFilters().addAll(booking.getPaymentFilters());
        allFilters.getPaymentFilters().addAll(expedia.getPaymentFilters());
        allFilters.getPaymentFilters().addAll(hotels.getPaymentFilters());

        allFilters.setStarFilters(new ArrayList<>());
        allFilters.getStarFilters().addAll(booking.getStarFilters());
        allFilters.getStarFilters().addAll(expedia.getStarFilters());
        allFilters.getStarFilters().addAll(hotels.getStarFilters());

        allFilters.setRatingFilters(new ArrayList<>());
        allFilters.getRatingFilters().addAll(booking.getRatingFilters());
        allFilters.getRatingFilters().addAll(expedia.getRatingFilters());
        allFilters.getRatingFilters().addAll(hotels.getRatingFilters());

        allFilters.setPriceFilters(new ArrayList<>());
        allFilters.getPriceFilters().addAll(booking.getPriceFilters());
        allFilters.getPriceFilters().addAll(expedia.getPriceFilters());
        allFilters.getPriceFilters().addAll(hotels.getPriceFilters());

        allFilters.setTypeFilters(new ArrayList<>());
        allFilters.getTypeFilters().addAll(booking.getTypeFilters());
        allFilters.getTypeFilters().addAll(expedia.getTypeFilters());
        allFilters.getTypeFilters().addAll(hotels.getTypeFilters());

    }
}
