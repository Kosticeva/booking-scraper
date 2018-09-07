package com.ftn.uns.scraper.site;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

public class SiteFactory {

    private static WebClient client;

    public static WebClient getClient(){
        if(client == null) {
            client = new WebClient(BrowserVersion.CHROME);
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(true);
            client.getOptions().setThrowExceptionOnScriptError(false);
            client.getOptions().setThrowExceptionOnFailingStatusCode(false);
            client.getOptions().setPrintContentOnFailingStatusCode(false);
            client.getOptions().setPopupBlockerEnabled(true);
            client.getCookieManager().setCookiesEnabled(true);
        }

        return client;
    }

    public static SiteScraper getSite(SiteType type){
        String className = extractClassName(type);
        Class clazz;
        try{
            clazz = Class.forName(className);
        }catch (ClassNotFoundException e) {
            return null;
        }

        try {
            return (SiteScraper) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractClassName(SiteType type) {
        String lowerCaseName = type.name().substring(0, 1) + type.name().substring(1).toLowerCase();
        return String.format("com.ftn.uns.scraper.site.scraper.%sSiteScraperImpl", lowerCaseName);
    }
}
