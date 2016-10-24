package com.codepath.applicationnewyorktimessearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.applicationnewyorktimessearch.enums.ArticleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aditikakadebansal on 10/18/16.
 */
public class Article implements Parcelable {
    private String mWebUrl;
    private String mHeadline;
    private String mThumbnail;
    private String mSection;
    private String mPublishedDate;

    public String getHeadline() {
        return mHeadline;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public String getSection() { return mSection; }

    public String getPublishedDate() { return mPublishedDate; }

    public Article(JSONObject jsonObject, ArticleType articleType) {
        try {
            JSONArray multimedia;
            switch (articleType) {
                case SEARCH:
                    this.mPublishedDate = jsonObject.getString("pub_date");
                    this.mSection = jsonObject.getString("section_name");
                    this.mWebUrl = jsonObject.getString("web_url");
                    this.mHeadline = jsonObject.getJSONObject("headline").getString("main");
                    this.mThumbnail = "";
                    multimedia = jsonObject.getJSONArray("multimedia");
                    if (multimedia.length() == 0) return;
                    for (int i=0; i<multimedia.length(); i++) {
                        JSONObject multimediaJson = multimedia.getJSONObject(i);
                        if (multimediaJson.getString("subtype").equals("thumbnail")) {
                            this.mThumbnail = String.format(
                                    "https://www.nytimes.com/%s",
                                    multimediaJson.getString("url")
                            );
                            break;
                        }
                        this.mThumbnail = String.format(
                                "https://www.nytimes.com/%s",
                                multimediaJson.getString("url")
                        );
                    }
                    break;
                case TRENDING:
                    this.mPublishedDate = jsonObject.getString("published_date");
                    this.mSection = jsonObject.getString("section");
                    this.mWebUrl = jsonObject.getString("url");
                    this.mHeadline = jsonObject.getString("title");
                    this.mThumbnail = "";
                    multimedia = jsonObject.getJSONArray("media").getJSONObject(0).getJSONArray("media-metadata");
                    if (multimedia.length() == 0) return;
                    for (int i=0; i<multimedia.length(); i++) {
                        JSONObject multimediaJson = multimedia.getJSONObject(i);
                        if (multimediaJson.getString("format").equals("Standard Thumbnail")) {
                            this.mThumbnail = multimediaJson.getString("url");
                            break;
                        }
                        this.mThumbnail = multimediaJson.getString("url");
                    }
                    break;
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray, ArticleType articleType) {
        ArrayList<Article> results = new ArrayList<>();
        if(jsonArray != null) {
            for (int x = 0; x < jsonArray.length(); x++) {
                try {
                    results.add(new Article(jsonArray.getJSONObject(x), articleType));
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        }
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mWebUrl);
        dest.writeString(this.mHeadline);
        dest.writeString(this.mThumbnail);
        dest.writeString(this.mSection);
        dest.writeString(this.mPublishedDate);
    }

    protected Article(Parcel in) {
        this.mWebUrl = in.readString();
        this.mHeadline = in.readString();
        this.mThumbnail = in.readString();
        this.mSection = in.readString();
        this.mPublishedDate = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
