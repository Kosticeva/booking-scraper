package com.ftn.uns.scraper.query.model;

import lombok.Data;

import java.util.List;

@Data
public class Room {

    private Integer adultsInRoom;
    private List<Integer> childrenInRoom;
}
