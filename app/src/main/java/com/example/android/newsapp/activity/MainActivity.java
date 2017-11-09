package com.example.android.newsapp.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.android.newsapp.R;
import com.example.android.newsapp.adapter.NewsItemAdapter;
import com.example.android.newsapp.data.NewsItem;
import com.example.android.newsapp.databinding.ActivityMainBinding;
import com.example.android.newsapp.loader.NewsLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private static final String REQUEST_URL = "https://content.guardianapis.com/technology?show-tags=contributor&api-key=0b7a363a-fabc-4d25-b5bd-9c56e9dd3176";

    private ActivityMainBinding binding;
    private NewsItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        adapter = new NewsItemAdapter(this, new ArrayList<NewsItem>());
        binding.list.setAdapter(adapter);
        binding.list.setEmptyView(binding.emptyView);

        if (isOnline()) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.emptyView.setText(R.string.no_internet_connection);
        }

        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsItem newsItem = adapter.getItem(i);
                String url = newsItem.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    @Override
    public NewsLoader onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        binding.progressBar.setVisibility(View.GONE);
        adapter.clear();

        if (newsItems != null && !newsItems.isEmpty()) {
            adapter.addAll(newsItems);
        }

        binding.emptyView.setText(R.string.no_news_items_found);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        adapter.clear();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}
