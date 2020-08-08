package com.sehat.tracker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("userTier")
    @Expose
    private String userTier;

    @SerializedName("total")
    @Expose
    private int total;

    @SerializedName("startIndex")
    @Expose
    private int startIndex;

    @SerializedName("pageSize")
    @Expose
    private int pageSize;

    @SerializedName("currentPage")
    @Expose
    private int currentPage;

    @SerializedName("pages")
    @Expose
    private int pages;

    @SerializedName("orderBy")
    @Expose
    private String orderBy;

    @SerializedName("results")
    @Expose
    private List<Article> article;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserTier() {
        return userTier;
    }

    public void setUserTier(String userTier) {
        this.userTier = userTier;
    }

    public List<Article> getArticle() {
        return article;
    }

    public void setArticle(List<Article> article) {
        this.article = article;
    }
}

