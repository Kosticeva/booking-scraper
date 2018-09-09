package com.ftn.uns.scraper.site;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

public class ClientFactory {

    private static WebClient client;

    public static WebClient getClient() {
        if (client == null) {
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
}
