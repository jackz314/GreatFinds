package com.greatfinds.cs201;

import com.greatfinds.cs201.db.MediaTitle;
import com.greatfinds.cs201.db.Post;
import com.greatfinds.cs201.db.User;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

// Global bean that's shared across all users and sessions. Only one instance on the server
// This is NOT thread-safe, use synchronization if needed

@Named
@ApplicationScoped
public class GlobalBean {

    @Inject
    private PostHelper postHelper;

    @Inject
    private UserHelper userHelper;

    @Inject
    private MediaTitleHelper titleHelper;

    @Inject
    @Push
    private PushContext pushCh;

    private final CopyOnWriteArraySet<UserBean> userBeans = new CopyOnWriteArraySet<>();

    private List<Post> globalPosts;

    private List<User> users;

    private List<MediaTitle> titles;

    //like constructor, called after bean is constructed
    @PostConstruct
    public void load() {
        globalPosts = postHelper.getAllPosts();
        users = userHelper.getAllUsers();
        titles = titleHelper.getAllMediaTitles();
    }

    //called when a new post arrives, thread safe
    public void onPostsUpdate(@Observes PostUpdate postUpdate) {
        System.out.println("Global POST UPDATE: " + postUpdate);
        Post post = postUpdate.getPost();
        try {
            switch (postUpdate.getType()) {
                case CREATED -> globalPosts.add(0, post);
                case DELETED -> globalPosts.remove(post);
                case MODIFIED -> globalPosts.set(globalPosts.indexOf(post), post);
            }
            userBeans.forEach(userBean -> userBean.onPostsUpdate(postUpdate));
            pushCh.send("updatePosts");
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
        return globalPosts;
    }

    public List<MediaTitle> getAllMediaTitles() {
        return titles;
    }

    public void registerUserBean(UserBean userBean) {
        userBeans.add(userBean);
        System.out.println("Registered new user bean: " + userBeans.size() + " " + userBeans);
    }

    public void unregisterUserBean(UserBean userBean) {
        userBeans.remove(userBean);
        System.out.println("Unregistered new user bean: " + userBeans.size() + " " + userBeans);
    }
}