package com.greatfinds.cs201.db;

import javax.persistence.*;

@Entity
@Table(name = "mediaTitles")
@NamedQueries({
        @NamedQuery(name = "getAllMediaTitles", query = "SELECT m from MediaTitle m"),
        @NamedQuery(name = "getMatchedMediaTitles", query = "SELECT m from MediaTitle m WHERE m.title like '%:filter%'"),
        @NamedQuery(name = "getExactMatchMediaTitles", query = "SELECT m from MediaTitle m WHERE m.title = :filter"),
})
public class MediaTitle {
    //mediaTitleID, title, genre

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaTitleID;

    @JoinColumn(name = "genre")
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
}
