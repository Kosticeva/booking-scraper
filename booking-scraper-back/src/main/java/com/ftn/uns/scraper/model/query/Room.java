package com.ftn.uns.scraper.model.query;

import lombok.Data;

import java.util.List;

@Data
public class Room {

    private Integer adultsInRoom;
    private List<Integer> childrenInRoom;
}
