package com.example.android.newsapp.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String topic = sharedPrefs.getString(
                getString(R.string.settings_select_topic_default_key),
                getString(R.string.settings_select_topic_default_value)
        );
        Uri baseUri = Uri.parse(getString(R.string.base_request_url));

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendPath(topic);
        uriBuilder.appendQueryParameter(getString(R.string.show_tags), getString(R.string.contributor));
        uriBuilder.appendQueryParameter(getString(R.string.api_key), getString(R.string.api_key_value));
        return new NewsLoader(this, uriBuilder.toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
