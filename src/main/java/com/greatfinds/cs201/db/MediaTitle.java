package com.greatfinds.cs201.db;

import javax.persistence.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private String genre;
    private String title;

    public MediaTitle(String title, String genre) {
        this.title = title;
        this.genre = genre;
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

    public Long getMediaTitleID() {
        return mediaTitleID;
    }

    public void setMediaTitleID(Long ID) {
        this.mediaTitleID = ID;
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
        return title + " / " + genre;
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
