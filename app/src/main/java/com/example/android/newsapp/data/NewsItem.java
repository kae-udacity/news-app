package com.example.android.newsapp.data;

/**
 * Stores the data of the news item.
 */

public class NewsItem {

    private String title;
    private String section;
    private String url;
    private String author;
    private String publicationDate;

    public NewsItem(String title, String section, String url, String publicationDate) {
        this.title = title;
        this.section = section;
        this.url = url;
        this.publicationDate = publicationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublicationDate() {
        return publicationDate;
    }
}
