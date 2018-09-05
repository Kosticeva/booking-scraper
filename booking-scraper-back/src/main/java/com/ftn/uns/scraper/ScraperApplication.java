package com.ftn.uns.scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScraperApplication {

	public static void main(String[] args) {
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "error");
		SpringApplication.run(ScraperApplication.class, args);
	}
}
