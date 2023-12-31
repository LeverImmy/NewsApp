package com.java.xiongzeen.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.java.xiongzeen.MyApplication;
import com.java.xiongzeen.data.News;
import com.java.xiongzeen.data.Page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public final class FetchFromAPIManager {
    private final static FetchFromAPIManager instance = new FetchFromAPIManager();
    private static String startDate = "";
    private static String endDate = "";
    private static String keyWords = "";
    private static List<String> categoriesOfNews = new ArrayList<>();
    private static List<String> categoriesOfSearch = new ArrayList<>();

    static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static void reset() {
        startDate = "";
        endDate = getCurrentDate();
        keyWords = "";
        categoriesOfNews.clear();
    }

    static String getNewsUrl(String startDate, String endDate, String words, List<String> categories, int page) {
        if (endDate == null || endDate.length() == 0)
            endDate = getCurrentDate();
        StringBuilder allCategories = new StringBuilder();
        for (String category : categories)
            if (!category.equals("综合"))
                allCategories.append(category).append(",");
        if (allCategories.length() != 0)
            allCategories = new StringBuilder(allCategories.substring(0, allCategories.length() - 1));

        return "https://api2.newsminer.net/svc/news/queryNewsList" +
                "?size=15" +
                "&startDate=" + startDate +
                "&endDate=" + endDate +
                "&words=" + words +
                "&categories=" + allCategories +
                "&page=" + page;
    }

    static JSONObject getUrlResponse(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return new JSONObject(builder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public List<News> getNews(int offset, int pageSize) {

        String url = getNewsUrl(startDate, endDate, keyWords,
                MyApplication.page == Page.NEWS ? categoriesOfNews : categoriesOfSearch,
                offset / pageSize + 1);
        Log.d("FetchFromAPIManager", "Trying to get from " + url + " .");

        try {

            JSONArray data = getUrlResponse(url).getJSONArray("data");
            List<News> newsFeed = new ArrayList<>();

            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);

                newsFeed.add(new News(jsonObject));
            }

            return newsFeed;

        } catch (JSONException e) {

            e.printStackTrace();
            return null;
        }
    }

    public void setCategory(String category) {
        reset();
        if (!category.equals("综合"))
            categoriesOfNews.add(category);
        Log.d("FetchFromAPIManager", "categories:" + categoriesOfNews);
    }

    public void handleSearch(List<String> categories, String startDate, String endDate, String keyWords) {
        FetchFromAPIManager.categoriesOfSearch = categories;
        FetchFromAPIManager.keyWords = keyWords;
        FetchFromAPIManager.startDate = startDate;
        FetchFromAPIManager.endDate = endDate;
        Log.d("FetchFromAPIManager", "categories: " + categoriesOfSearch + " key = " + keyWords +
                " startDate = " + startDate + " endDate = " + endDate);
    }

    public static FetchFromAPIManager getInstance() {
        return instance;
    }
}
