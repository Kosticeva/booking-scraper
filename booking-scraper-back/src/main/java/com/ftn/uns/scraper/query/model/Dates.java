package com.ftn.uns.scraper.query.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Dates {

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

}
