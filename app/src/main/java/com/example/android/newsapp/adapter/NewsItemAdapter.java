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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NewsItem newsItem = getItem(position);

        holder.titleTextView.setText(newsItem.getTitle());
        holder.sectionTextView.setText(newsItem.getSection());

        String date = formatDate(newsItem.getPublicationDate());
        holder.publicationDateTextView.setText(date);

        if (!TextUtils.isEmpty(newsItem.getAuthor())) {
            holder.authorTextView.setText(newsItem.getAuthor());
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

    private class ViewHolder {
        private TextView titleTextView;
        private TextView authorTextView;
        private TextView sectionTextView;
        private TextView publicationDateTextView;

        ViewHolder(View view) {
            titleTextView = view.findViewById(R.id.title);
            authorTextView = view.findViewById(R.id.author);
            sectionTextView = view.findViewById(R.id.section);
            publicationDateTextView = view.findViewById(R.id.publication_date);
        }
    }
}
