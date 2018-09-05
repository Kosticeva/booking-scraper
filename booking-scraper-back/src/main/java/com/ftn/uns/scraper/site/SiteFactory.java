package com.ftn.uns.scraper.site;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

import java.io.File;

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

    public static SiteScraper getSite(File file){
        String className = extractClassName(file.getName());
        Class clazz;
        try{
            clazz = Class.forName("com.ftn.uns.scraper.site.scraper."+className+"SiteScraperImpl");
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

    private static String extractClassName(String fileName) {
        String name = fileName.substring(0, fileName.length()-9);

        String firstLetter = name.substring(0,1).toUpperCase();
        String rest = name.substring(1);

        return firstLetter + rest;
    }
}
