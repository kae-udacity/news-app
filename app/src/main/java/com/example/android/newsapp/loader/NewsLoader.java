package com.example.android.newsapp.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.newsapp.data.NewsItem;
import com.example.android.newsapp.util.RequestUtils;

import java.util.List;

/**
 * Starts a {@link android.content.Loader} to fetch the news items in the background.
 */

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    private String url;

    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        return RequestUtils.fetchNewsItems(getContext(), url);
    }
}
