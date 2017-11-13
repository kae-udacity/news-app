package com.example.android.newsapp.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.newsapp.R;
import com.example.android.newsapp.data.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a HTTP request to fetch the news items. When the data is retrieved it extracts the
 * relevant information and stores it in a {@link List} of {@link NewsItem} objects.
 */

public final class RequestUtils {

    private static final String TAG = RequestUtils.class.getSimpleName();

    private RequestUtils() {
    }

    public static List<NewsItem> fetchNewsItems(Context context, String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }

        URL url = createUrl(context, requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = fetchJsonResponse(context, url);
        } catch (IOException e) {
            Log.e(TAG, context.getString(R.string.error_closing_input_stream), e);
        }
        return extractNewsItems(context, jsonResponse);
    }

    private static String fetchJsonResponse(Context context, URL url) throws IOException {
        String jsonResponse = null;

        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(context.getString(R.string.get));
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);

            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFrom(inputStream);
            } else {
                Log.e(TAG, context.getString(R.string.error_retrieving_data) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, context.getString(R.string.error_retrieving_news_item_data), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFrom(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream == null) {
            return null;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }

    private static List<NewsItem> extractNewsItems(Context context, String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<NewsItem> newsItems = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONObject response = baseJsonResponse.getJSONObject(context.getString(R.string.response));
            JSONArray results = response.getJSONArray(context.getString(R.string.results));

            for (int i = 0; i < results.length(); i++) {
                JSONObject newsItemJson = results.getJSONObject(i);
                NewsItem newsItem = getNewsItem(context, newsItemJson);
                newsItems.add(newsItem);
            }
        } catch (JSONException e) {
            Log.e(TAG, context.getString(R.string.error_parsing_json_response), e);
        }

        return newsItems;
    }

    @NonNull
    private static NewsItem getNewsItem(Context context, JSONObject newsItemJson) throws JSONException {
        String title = newsItemJson.getString(context.getString(R.string.web_title));
        String section = newsItemJson.getString(context.getString(R.string.section_name));
        String url = newsItemJson.getString(context.getString(R.string.web_url));
        String publicationDate = newsItemJson.getString(context.getString(R.string.web_publication_date));

        NewsItem newsItem = new NewsItem(title, section, url, publicationDate);
        setAuthor(context, newsItemJson, newsItem);
        return newsItem;
    }

    private static void setAuthor(Context context, JSONObject newsItemJson, NewsItem newsItem) throws JSONException {
        JSONArray tags = newsItemJson.getJSONArray(context.getString(R.string.tags));
        if (tags.length() > 0) {
            JSONObject tag = tags.getJSONObject(0);
            String author = tag.getString(context.getString(R.string.web_title));
            newsItem.setAuthor(author);
        }
    }

    private static URL createUrl(Context context, String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, context.getString(R.string.error_creating_url), e);
        }
        return url;
    }
}
