package com.greatfinds.cs201;


// Java packages
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

// our packages
import com.greatfinds.cs201.db.Post;

@Named
@SessionScoped
public class SearchBean implements Serializable {
    @Inject
    @SuppressWarnings("CdiUnproxyableBeanTypesInspection")
    transient private PostHelper postHelper;
    private List<Post> posts;

    private List<String> categories;
    private List<String> genres;


    @PostConstruct
    public void load() {
        posts = postHelper.getAllPosts();
    }

    public String search() {
        // Run SQL command
        posts = postHelper.getPostsWith(categories, genres);
        return "index";
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

    public List<Post> getPosts() {
        return posts;
    }
}
