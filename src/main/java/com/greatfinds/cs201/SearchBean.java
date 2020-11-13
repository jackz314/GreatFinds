package com.greatfinds.cs201;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named
@SessionScoped
public class SearchBean implements Serializable {
    private List<String> categories;
    private List<String> genres;
    private String sqlSearch;


//    @PostConstruct
//    public void load() {
//        categories = Arrays.asList("hello", "brother", "sgh");
//        genres = new ArrayList<>();
//    }

    public String search() {
        // Run SQL command
        sqlSearch = "Categories: " + categories + "\nGenres: " + genres;
        return "feed";
    }


    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(List<String> cat) {
        categories = cat;
    }

    public List<String> getGenres() {
        return genres;
    }
    public void setGenres(List<String> gen) {
        genres = gen;
    }

    public String getSqlSearch() {
        return sqlSearch;
    }
}
