package com.greatfinds.cs201;

import com.greatfinds.cs201.db.MediaTitle;
import com.greatfinds.cs201.db.Post;
import com.greatfinds.cs201.db.User;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.annotation.FacesConfig;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

// Global bean that's shared across all users and sessions. Only one instance on the server
// This is NOT thread-safe, use synchronization if needed

// enforce JSF 2.3
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
@Named
@ApplicationScoped
public class GlobalBean {

    @Inject
    private PostHelper postHelper;

    @Inject
    private UserHelper userHelper;

    @Inject
    private MediaTitleHelper titleHelper;

    private List<Post> posts;

    private List<User> users;

    private List<MediaTitle> titles;

    @Inject
    @Push
    private PushContext pushCh;

    //like constructor, called after bean is constructed
    @PostConstruct
    public void load() {
        posts = postHelper.getAllPosts();
        users = userHelper.getAllUsers();
        titles = titleHelper.getAllMediaTitles();
    }

    //called when a new post arrives, thread safe
    public void onPostsUpdate(@Observes PostUpdate postUpdate) {
        System.out.println("Global POST UPDATE: " + postUpdate);
        Post post = postUpdate.getPost();
        try {
            switch (postUpdate.getType()) {
                case CREATED -> posts.add(0, post);
                case DELETED -> posts.remove(post);
                case MODIFIED -> posts.set(posts.indexOf(post), post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //called when a new user is created, thread safe
    public void onNewUser(@Observes User newUser) {
        System.out.println("NEW USER: " + newUser);
        users.add(0, newUser);
//        pushCh.send("updateUsers");
//       PushEP.sendAll("updateUsers");
    }

    public void onNewTitle(@Observes MediaTitle title) {
        System.out.println("NEW MEDIA TITLE: " + title);
        titles.add(0, title);
//        pushCh.send("updateTitles");
    }

    public List<User> getAllUsers() {
        return users;
    }

    public List<Post> getAllPosts() {
        return posts;
    }

    public List<MediaTitle> getAllMediaTitles() {
        return titles;
    }
}