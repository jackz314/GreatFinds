package com.greatfinds.cs201.db;

import com.greatfinds.cs201.TmdbHelper;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;

import javax.persistence.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "mediaTitles")
@NamedQueries({
        @NamedQuery(name = "getAllMediaTitles", query = "SELECT m from MediaTitle m"),
        @NamedQuery(name = "getMatchedMediaTitles", query = "SELECT m from MediaTitle m WHERE m.title like CONCAT('%', :filter, '%')"),
        @NamedQuery(name = "getExactMatchMediaTitles", query = "SELECT m from MediaTitle m WHERE m.title = :title AND m.genre = :genre"),
})
public class MediaTitle {
    //mediaTitleID, title, genre

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaTitleID;

    private boolean isCustom;

    @JoinColumn(name = "genre")
    private String genre;
    private String title;
    private String imgUrl = null;

    @Transient
    private boolean suggested = false;

    public MediaTitle(String title, String genre, String imgUrl) {
        this.title = title;
        this.genre = genre;
        this.imgUrl = imgUrl;
    }

    public MediaTitle(String title, String genre) {
        this.title = title;
        this.genre = genre;
        populateImgUrl();
    }

    public MediaTitle() {
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        if (imgUrl == null || imgUrl.isEmpty()) {
            populateImgUrl();
        }
        return imgUrl;
    }

    public boolean isSuggested() {
        return suggested;
    }

    public void setSuggested(boolean suggested) {
        this.suggested = suggested;
    }

    private void populateImgUrl() {
        System.out.println("populating " + this);
        if (isCustom) {
            imgUrl = TmdbHelper.imageNotFoundUrl;
            return;
        }
        TmdbSearch search = TmdbHelper.getTmdbSearch();
        List<Multi> results = search.searchMulti(getTitle(), "en", 1).getResults();
        if (results.isEmpty()) {
            imgUrl = TmdbHelper.imageNotFoundUrl;
            return;
        }
        Multi result = results.get(0);
        switch (result.getMediaType()) {
            case MOVIE -> imgUrl = ((MovieDb) result).getPosterPath();
            case TV_SERIES -> imgUrl = ((TvSeries) result).getPosterPath();
            case PERSON -> imgUrl = ((Person) result).getProfilePath();
        }
        if (imgUrl == null) imgUrl = TmdbHelper.imageNotFoundUrl;
        else imgUrl = TmdbHelper.imageBaseUrl + imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaTitle)) return false;

        MediaTitle that = (MediaTitle) o;

        if (!Objects.equals(title, that.title)) return false;
        return Objects.equals(genre, that.genre);
    }

    @Override
    public int hashCode() {
        int result = genre != null ? genre.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MediaTitle{" +
                "mediaTitleID=" + mediaTitleID +
                ", isCustom=" + isCustom +
                ", genre='" + genre + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", suggested=" + suggested +
                '}';
    }

    //get an url escaped String that represents the media title, includes title and genre
    public String escapedSimpleRep() {
        if (title == null || genre == null) return null;
        return URLEncoder.encode(title, StandardCharsets.UTF_8) + "," + URLEncoder.encode(genre, StandardCharsets.UTF_8);
    }

    //populate object info (title & genre) with an escaped representation
    public void populateEscapedRep(String rep) {
        if (rep == null) return;
        int splitIdx = rep.indexOf(',');
        if (splitIdx == -1) {
            // object didn't exist before in the database
            // this is a new one and only contains a title, no need to decode anything
            title = rep;
            return;
        }
        title = URLDecoder.decode(rep.substring(0, splitIdx), StandardCharsets.UTF_8);
        genre = URLDecoder.decode(rep.substring(splitIdx + 1), StandardCharsets.UTF_8);
    }
}
