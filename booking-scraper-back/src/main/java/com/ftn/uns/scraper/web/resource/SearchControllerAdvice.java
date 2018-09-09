package com.ftn.uns.scraper.web.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.MalformedParametersException;
import java.net.URISyntaxException;
import java.time.DateTimeException;

@ControllerAdvice
public class SearchControllerAdvice {

    @ExceptionHandler({NumberFormatException.class, MalformedParametersException.class, DateTimeException.class})
    public ResponseEntity<String> handleInvalidParameters(Exception e) {
        return new ResponseEntity<>("Please check your search parameters", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleServerException(RuntimeException e) {
        return new ResponseEntity<>("An error occured while collecting data. Please try again in a few moments.", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<String> handleURIException(URISyntaxException e) {
        return new ResponseEntity<>("Location search service is currently unavailable.", HttpStatus.BAD_REQUEST);
    }
}
