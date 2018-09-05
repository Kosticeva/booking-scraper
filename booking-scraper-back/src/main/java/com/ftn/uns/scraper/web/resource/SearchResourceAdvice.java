package com.ftn.uns.scraper.web.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.MalformedParametersException;
import java.net.URISyntaxException;
import java.time.DateTimeException;

@ControllerAdvice
public class SearchResourceAdvice {

    @ExceptionHandler({NumberFormatException.class, MalformedParametersException.class, DateTimeException.class})
    public ResponseEntity<String> handleInvalidParameters(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleServerException(RuntimeException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<String> handleURIException(URISyntaxException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
