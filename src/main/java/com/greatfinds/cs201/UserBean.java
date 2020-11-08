package com.greatfinds.cs201;

import com.greatfinds.cs201.db.Post;
import com.greatfinds.cs201.db.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

//user session scoped, one instance per user, including anonymous/guest users

@Named
@SessionScoped
public class UserBean implements Serializable {

    private User registerUser;
    private User loginUser;
    private boolean isUserLoggedIn = false;

    private String tagStr;

    //we're injecting EJB here, not regular CDI, so we don't need to implement serializable and it'll still work
    @SuppressWarnings("CdiUnproxyableBeanTypesInspection")
    @Inject
    transient private PostHelper postHelper;

    private Post inputPost;

    private List<Post> posts;

    //same story as above
    @SuppressWarnings("CdiUnproxyableBeanTypesInspection")
    @Inject
    transient private UserHelper userHelper;

    @PostConstruct
    public void load() {
        System.out.println("LOADED user bean");
        posts = postHelper.getAllPosts();//start with guest posts (no filter)
        registerUser = new User();
        loginUser = new User();
        inputPost = new Post();
    }

    public User getRegisterUser() {
        return registerUser;
    }

    public User getLoginUser() {
        return loginUser;
    }

    @SuppressWarnings("unused")
    public void validateRegisterEmail(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String email = (String) value;
        if (!email.matches("^.+@.+\\..+$")) {
            FacesMessage msg = new FacesMessage("Invalid email.");
            throw new ValidatorException(msg);
        } else if (userHelper.userExists(email)) {
            throw new ValidatorException(new FacesMessage("Email already exists."));
        }
    }

    @SuppressWarnings("unused")
    public void validateLoginEmail(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        System.out.println("VALIDATING EMAIL");
        String email = (String) value;
        if ("abc".equals(email)) return;//skip test account
        if (!email.matches("^.+@.+\\..+$")) {
            throw new ValidatorException(new FacesMessage("Invalid email."));
        }/*else if (!service.userExists(email)) {
            throw new ValidatorException(new FacesMessage("Email doesn't match any account."));
        }*/
    }

    /*@SuppressWarnings("unused")
    public void validateLogin(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        System.out.println("VALIDATING LOGIN");
        UIInput loginEmailInput = (UIInput) context.getViewRoot().findComponent("emailLogin");
        String email = (String) loginEmailInput.getSubmittedValue();
        String pwd = (String) value;
        if (!userHelper.userMatch(email, pwd)) {
            throw new ValidatorException(new FacesMessage("Incorrect email or password."));
        }
    }*/

    @SuppressWarnings("unused")
    public boolean validateLogin(FacesContext context, List<UIInput> components, List<Object> values) throws ValidatorException {
        String email = (String) values.get(0);
        String pwd = (String) values.get(1);
        System.out.println("VALIDATING LOGIN: " + email + " : " + pwd);
        return userHelper.userMatch(email, pwd);
    }

    public void register() {
        userHelper.registerUser(registerUser);
    }

    public void login() {
//        isUserLoggedIn = userHelper.userMatch(loginUser.getEmail(), loginUser.getPwd());
        isUserLoggedIn = true;
        populateUserInfo();
        Set<String> followedTags = loginUser.getFollowedTags();
        if (followedTags == null || followedTags.isEmpty()) return;//keep default if there's no followed tags
        posts = postHelper.getFollowedPosts(followedTags);
        tagStr = "#" + String.join(" #", loginUser.getFollowedTags());
        System.out.println("User logged in: " + loginUser);
    }

    public void logOut() {
        isUserLoggedIn = false;
        loginUser = new User();
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public String getLoginStatus() {
        return isUserLoggedIn ? "logged in!\n" + loginUser : "not logged in.";
    }

    public void onPostsUpdate(@Observes PostUpdate postUpdate) {
        System.out.println("User POST UPDATE: " + postUpdate);
        Post post = postUpdate.getPost();
        Set<String> followedTags = loginUser.getFollowedTags();
        //skip if none of the tags in this post matches any of the tags the user follows
        if (isUserLoggedIn && post.getTags().stream().noneMatch(followedTags::contains)) return;
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

    public String getTagStr() {
        return tagStr;
    }

    public void setTagStr(String tagStr) {
        this.tagStr = tagStr;
        Set<String> tags = postHelper.extractTags(tagStr);
        loginUser.setFollowedTags(tags);
        posts = postHelper.getFollowedPosts(tags);
    }

    public void populateUserInfo() {
        loginUser = userHelper.getCompleteUser(loginUser);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public Post getInputPost() {
        return inputPost;
    }

    public void submitPost() {
        inputPost.setUser(loginUser);
        postHelper.post(inputPost);
        inputPost = new Post();
    }

}