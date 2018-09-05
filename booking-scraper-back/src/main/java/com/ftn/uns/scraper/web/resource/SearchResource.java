package com.ftn.uns.scraper.web.resource;

import com.ftn.uns.scraper.model.filter.FiltersDTO;
import com.ftn.uns.scraper.model.query.*;
import com.ftn.uns.scraper.model.result.Results;
import com.ftn.uns.scraper.model.query.SearchMarker;
import com.ftn.uns.scraper.service.filter.FilterFactory;
import com.ftn.uns.scraper.service.SearchService;
import com.ftn.uns.scraper.site.SiteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.lang.reflect.MalformedParametersException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class SearchResource {

    @Autowired
    private SearchService searchService;

    @PostMapping(value="/search",
                params = {"destination", "checkIn", "checkOut", "rooms", "adults", "children", "filters"},
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Results> getHotels(@RequestParam String destination, @RequestParam String checkIn,
                                             @RequestParam String checkOut, @RequestParam Integer rooms,
                                             @RequestParam String[] adults, @RequestParam String children,
                                             @RequestParam String[] filters, @RequestBody List<SearchMarker> marker)
           throws DateTimeException, MalformedParametersException, NumberFormatException {
            Location location = new Location();
            location.setCity(destination);

            Dates dates = new Dates();
            dates.setCheckInDate(LocalDate.parse(checkIn));
            dates.setCheckOutDate(LocalDate.parse(checkOut));

            List<Room> allRooms = createRoomsFromParameter(rooms, adults, children);

            SearchQuery query = new SearchQuery();
            query.setLocation(location);
            query.setDates(dates);
            query.setRooms(allRooms);
            query.setFilters(filters);
            query.setMarkers(marker);

            Results results = searchService.getResults(query);
            return ResponseEntity.ok(results);
    }

    private List<Room> createRoomsFromParameter(Integer roomCount, String[] adults, String childrenParam)
            throws MalformedParametersException {
        if(!roomCount.equals(adults.length)){
            throw new MalformedParametersException("Each room must have at least one adult staying in it.");
        }

        String[] children = childrenParam.split(";");
        if(!roomCount.equals(children.length)){
            throw new MalformedParametersException("Number of children not defined for each room.");
        }

        List<Room> rooms = new ArrayList<>();
        int idx = 0;

        for(String adult: adults){
            Room room = new Room();
            room.setChildrenInRoom(new ArrayList<>());
            String[] childrenAges = children[idx].split(",");
            for(String child: childrenAges){
                room.getChildrenInRoom().add(Integer.parseInt(child));
            }
            room.setAdultsInRoom(Integer.parseInt(adult));
            rooms.add(room);
        }

        return rooms;
    }

    @GetMapping(value="/filters", produces = MediaType.APPLICATION_JSON_VALUE)
    public FiltersDTO getFilters(){
        try{
            return FilterFactory.convertToDTO(FilterFactory.getFilters(SiteType.BOOKING));
        }catch (FileNotFoundException e){
            FiltersDTO filtersDTO = new FiltersDTO(new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            return filtersDTO;
        }
    }

    @GetMapping(value="/locations", produces = MediaType.APPLICATION_JSON_VALUE, params = {"query"})
    public Object getLocations(@RequestParam String query) throws URISyntaxException {
        RestTemplate template = new RestTemplate();
        return template.getForEntity(new URI("https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
            + query + "&key=AIzaSyAF046BxSGUdMMYpbzKLcaS_AuBNRzboxQ"), Object.class);
    }
}
