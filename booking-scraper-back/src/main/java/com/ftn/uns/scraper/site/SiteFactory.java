package com.ftn.uns.scraper.site;

public class SiteFactory {

    public static SiteScraper getSite(Site type) {
        String className = extractClassName(type);
        Class clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }

        try {
            return (SiteScraper) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractClassName(Site type) {
        String lowerCaseName = type.name().substring(0, 1) + type.name().substring(1).toLowerCase();
        return String.format("com.ftn.uns.scraper.site.scraper.%sSiteScraperImpl", lowerCaseName);
    }
}
