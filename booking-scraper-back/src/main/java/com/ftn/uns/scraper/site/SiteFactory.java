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
            client.getOptions().setJavaScriptEnabled(true);             ///needs to be true because expedia needs javascript to be enabled
            client.getOptions().setThrowExceptionOnScriptError(false);
            client.getOptions().setThrowExceptionOnFailingStatusCode(false);
            client.getOptions().setPrintContentOnFailingStatusCode(false);
            client.getCookieManager().setCookiesEnabled(true);
        }

        return client;
    }

    public static Site getSite(File file){
        String className = extractClassName(file.getName());
        Class clazz = null;
        try{
            clazz = Class.forName("com.ftn.uns.scraper.site.implementation."+className+"SiteImpl");
        }catch (ClassNotFoundException e) {
            return null;
        }

        try {
            return (Site)clazz.newInstance();
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
