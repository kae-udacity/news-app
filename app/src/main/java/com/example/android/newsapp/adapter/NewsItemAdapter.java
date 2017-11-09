package com.example.android.newsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.newsapp.R;
import com.example.android.newsapp.data.NewsItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Provides views for the list of news items.
 */

public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    private static final String TAG = NewsItemAdapter.class.getSimpleName();

    public NewsItemAdapter(Context context, List<NewsItem> newsItems) {
        super(context, 0, newsItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_item, parent, false);
        }
        NewsItem newsItem = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView sectionTextView = convertView.findViewById(R.id.section);

        titleTextView.setText(newsItem.getTitle());
        sectionTextView.setText(newsItem.getSection());

        String date = formatDate(newsItem.getPublicationDate());
        TextView publicationDateTextView = convertView.findViewById(R.id.publication_date);
        publicationDateTextView.setText(date);

        if (!TextUtils.isEmpty(newsItem.getAuthor())) {
            TextView authorTextView = convertView.findViewById(R.id.author);
            authorTextView.setText(newsItem.getAuthor());
        }

        return convertView;
    }

    private String formatDate(String unformattedDate) {
        int dividerIndex = unformattedDate.indexOf(getContext().getString(R.string.t));
        unformattedDate = unformattedDate.substring(0, dividerIndex);

        String formattedDate = null;
        try {
            Date date = new SimpleDateFormat(getContext().getString(R.string.yyyy_mm_dd), Locale.getDefault()).parse(unformattedDate);
            formattedDate = new SimpleDateFormat(getContext().getString(R.string.lll_dd_yyyy), Locale.getDefault()).format(date);
        } catch (ParseException e) {
            Log.e(TAG, getContext().getString(R.string.error_parsing_date), e);
        }

        return formattedDate;
    }
}
