package com.sehat.tracker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Article {

    @SerializedName("webTitle")
    @Expose
    private String title;

    @SerializedName("webUrl")
    @Expose
    private String url;

    @SerializedName("webPublicationDate")
    @Expose
    private String publishedAt;

    @SerializedName("fields")
    @Expose
    private Fields fields;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }
}

