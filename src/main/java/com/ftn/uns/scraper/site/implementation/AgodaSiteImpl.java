package com.ftn.uns.scraper.site.implementation;

import com.ftn.uns.scraper.query.Result;
import com.ftn.uns.scraper.query.model.Dates;
import com.ftn.uns.scraper.query.model.Location;
import com.ftn.uns.scraper.query.model.Room;
import com.ftn.uns.scraper.site.Site;
import com.ftn.uns.scraper.site.SiteFactory;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Cleanup;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

//we'll see
//ne radi ni ovako
public class AgodaSiteImpl implements Site {

    @Override
    public String createLocationParameter(Location location) {
        String cityId = null;

        try {
            String requestUrl =
                    "https://www.agoda.com/Search/Search/GetUnifiedSuggestResult/3/1/1/0/en-us/?searchText=" + URLEncoder.encode(location.getCity(), "UTF-8");
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            connection.disconnect();

            Gson gson = new Gson();
            JsonObject object = gson.fromJson(jsonString.toString(), JsonObject.class);
            JsonArray viewModelList = object.getAsJsonArray("ViewModelList");

            for(int i=0; i<viewModelList.size(); i++){
                if(viewModelList.get(i).isJsonObject() && viewModelList.get(i).getAsJsonObject().get("CityId") != null){
                    JsonObject obj = viewModelList.get(i).getAsJsonObject();
                    if(obj.get("CityId").getAsInt() > 0){
                        cityId = obj.get("CityId").getAsString();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


        String LOCATION_PARAM = "city";
        return String.format("%s=%s",LOCATION_PARAM, cityId);
    }

    @Override
    public String createCheckInParameter(Dates dates) {
        String CHECKIN_PARAM = "startDate";
        return String.format("%s=%d-%02d-%02d", CHECKIN_PARAM, dates.getCheckInDate().getYear(),
                dates.getCheckInDate().getMonthValue(), dates.getCheckInDate().getDayOfMonth());
    }

    @Override
    public String createCheckOutParameter(Dates dates) {
        String CHECKOUT_PARAM = "endDate";
        return String.format("%s=%d-%02d-%02d", CHECKOUT_PARAM, dates.getCheckOutDate().getYear(),
                dates.getCheckOutDate().getMonthValue(), dates.getCheckOutDate().getDayOfMonth());
    }

    @Override
    public String createRoomsParameter(List<Room> rooms) {
        String ROOMS_PARAM = "rooms";
        return String.format("%s=%d", ROOMS_PARAM, rooms.size());
    }

    @Override
    public String createAdultsParameter(List<Room> rooms) {
        int adultsCount = 0;
        for(Room room: rooms) adultsCount += room.getAdultsInRoom();

        String ADULTS_PARAM = "adults";
        return String.format("%s=%d", ADULTS_PARAM, adultsCount);
    }

    @Override
    public String createChildrenParameter(List<Room> rooms) {
        StringBuilder ages = new StringBuilder();
        int childrenCount = 0;
        for (Room room : rooms) {
            childrenCount += room.getChildrenInRoom().size();
            for (Integer age : room.getChildrenInRoom()) {
                ages.append(age);
                if (room.getChildrenInRoom().indexOf(age) < room.getChildrenInRoom().size() - 1
                        || rooms.indexOf(room) < rooms.size() - 1) {
                    ages.append(",");
                }
            }
        }

        String CHILDREN_PARAM = "children";
        String CHILDRENAGE_PARAM = "childages";
        try {
            return String.format("%s=%d&%s=%s", CHILDREN_PARAM, childrenCount, CHILDRENAGE_PARAM, URLEncoder.encode(ages.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String createFilterParameter(List<String> filters) {
        return "";
    }

    @Override
    public Iterable<Result> scrapePage(HtmlPage page) {
        String rootUrl = page.getUrl().toString().substring(0, page.getUrl().toString().indexOf("?"));

        List<HtmlAnchor> links = page.getByXPath("//a[@data-selenium='popular-hotel-item']");
        List<Result> results = new ArrayList<>();

        for(HtmlAnchor anchor: links){
            Result result = new Result();
            result.setResultLink("https://www.agoda.com" + anchor.getHrefAttribute() + page.getUrl().toString().substring(page.getUrl().toString().indexOf('?')));

            try {
                HtmlPage hotelPage = SiteFactory.getClient().getPage(result.getResultLink());
                try{
                    Thread.sleep(4000);
                }catch (Exception e){

                }

                @Cleanup BufferedWriter writer = new BufferedWriter(new FileWriter(new File("agoda1.html")));
                writer.write(hotelPage.asXml());
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return results;
    }

    @Override
    public HtmlPage getResultPage(String searchUrl, List<String> filters){
        try {
            SiteFactory.getClient().getPage("https://www.agoda.com");   //coookie shit
            HtmlPage page = SiteFactory.getClient().getPage(searchUrl);

            List<HtmlMeta> cityURL = page.getByXPath("//meta[@property='og:url']");
            HtmlPage newPage = SiteFactory.getClient().getPage(cityURL.get(0).getContentAttribute() + "?" + extractParamsFromURL(searchUrl));


            return newPage;
        }catch (IOException io){
            io.printStackTrace();
            return null;
        }
    }

    private String extractParamsFromURL(String url){
        String[] parts = url.split("\\?");
        String[] params = parts[1].split("&");
        StringBuilder retVal = new StringBuilder();
        for(int i = 1; i < params.length; i++){
            retVal.append(params[i]);
            if(i < params.length-1 ){
                retVal.append("&");
            }
        }

        return retVal.toString();
    }

    @Override
    public String createSearchQueryURL(String location, String checkIn, String checkOut, String rooms, String adults,
                                       String children) {
        String QUERY_ADDRESS = "https://www.agoda.com/en-gb/pages/agoda/default/DestinationSearchResult.aspx";
        return String.format("%s?%s&%s&%s&%s&%s&%s",
                QUERY_ADDRESS, location, checkIn, checkOut, rooms, adults, children);
    }

    @Override
    public Double extractPrice(String price) {
        return null;
    }
}