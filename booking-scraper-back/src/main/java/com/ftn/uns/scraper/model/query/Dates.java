package com.ftn.uns.scraper.model.query;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Dates {

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

}
