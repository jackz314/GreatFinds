package com.greatfinds.cs201;

import com.greatfinds.cs201.db.MediaTitle;
import com.greatfinds.cs201.db.Post;
import com.greatfinds.cs201.db.User;
import info.movito.themoviedbapi.model.Multi;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

//user session scoped, one instance per user, including anonymous/guest users

@Named
@WindowScoped
public class UserBean implements Serializable {

    private UUID uuid;

    private User registerUser;
    private User loginUser;
    private boolean isUserLoggedIn = false;

    private String dropdownFilter;
    private String inputFilter;

    private String tagStr;

    private String loginOutText = "LOG IN/REGISTER";

    @Inject
    transient private GlobalBean globalBean;

    @Inject
    transient private PostHelper postHelper;
    private Post inputPost;
    private boolean customPost = false;
    private List<Post> originalPosts;
    private List<Post> filteredPosts;

    private Set<String> genres;
    private Set<String> availableGenres;

    @Inject
    transient private MediaTitleHelper mediaTitleHelper;

    //same story as above
    @Inject
    transient private UserHelper userHelper;

    @PostConstruct
    public void load() {
        uuid = UUID.randomUUID();
        originalPosts = postHelper.getAllPosts();//start with guest posts (no filter)
        registerUser = new User();
        loginUser = new User();
        globalBean.registerUserBean(this);
        System.out.println("LOADED user bean " + this);
    }

    @PreDestroy
    public void unload() {
        System.out.println("Unloading " + this);
        globalBean.unregisterUserBean(this);
    }

    public User getRegisterUser() {
        return registerUser;
    }

    public User getLoginUser() { return loginUser; }

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
        if ("abc".equals(email) || "123".equals(email)) return;//skip test account
        if (!email.matches("^.+@.+\\..+$")) {
            throw new ValidatorException(new FacesMessage("Invalid email."));
        }/*else if (!service.userExists(email)) {
            throw new ValidatorException(new FacesMessage("Email doesn't match any account."));
        }*/
    }

    public void validateRating(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        int rating = (Integer) value;
        System.out.println("Validating rating " + rating);
        if (rating > 3 || rating < 1) {
            throw new ValidatorException(new FacesMessage("Invalid rating."));
        }
    }

    public String getLoginOutText() {
        return loginOutText;
    }

    public void setLoginOutText(String loginOutText) {
        this.loginOutText = loginOutText;
    }

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

    public String registerRedirect() {
        register();
        loginUser = registerUser;
        registerUser = new User();
        login();
        return "index.xhtml?faces-redirect=true";
    }

    public void login() {
//        isUserLoggedIn = userHelper.userMatch(loginUser.getEmail(), loginUser.getPwd());
        isUserLoggedIn = true;
        loginOutText = "LOG OUT";
        populateUserInfo();
        inputPost = new Post();
        Set<String> followedTags = loginUser.getFollowedTags();
        if (followedTags == null || followedTags.isEmpty()) return;//keep default if there's no followed tags
        originalPosts = postHelper.getFollowedPosts(followedTags);
        tagStr = "#" + String.join(" #", loginUser.getFollowedTags());
        updateFilterStuff();
        System.out.println("User logged in: " + loginUser);
    }

    public String loginRedirect() {
        login();
        return "index.xhtml?faces-redirect=true";
    }

    public String loginOutRedirect() {
        if (isUserLoggedIn) {
            logOut();
            return "index.xhtml?faces-redirect=true";
        } else {
            return "login.xhtml?faces-redirect=true";
        }
    }

    public void logOut() {
        isUserLoggedIn = false;
        loginOutText = "LOG IN/REGISTER";
        loginUser = new User();
        originalPosts = postHelper.getAllPosts();
        updateFilterStuff();
    }

    private void updateFilterStuff() {
        filterPosts();
        updateAvailableGenres();
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public String getLoginStatus() {
        return isUserLoggedIn ? "logged in!\n" + loginUser : "not logged in.";
    }

    public void onPostsUpdate(PostUpdate postUpdate) {
        System.out.println("User POST UPDATE: " + postUpdate);
        Post post = postUpdate.getPost();
        Set<String> followedTags = loginUser.getFollowedTags();
        //skip if none of the tags in this post matches any of the tags the user follows
        if (isUserLoggedIn && post.getTags().stream().noneMatch(followedTags::contains)) return;
        try {
            switch (postUpdate.getType()) {
                case CREATED -> originalPosts.add(0, post);
                case DELETED -> originalPosts.remove(post);
                case MODIFIED -> originalPosts.set(originalPosts.indexOf(post), post);
            }
            updateFilterStuff();
//            pushCh.send("updatePosts", getUuid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTagStr() {
        return tagStr;
    }

    public void setTagStr(String tagStr) {
        this.tagStr = tagStr;
        System.out.println("new tagStr: " + tagStr);
        Set<String> tags = postHelper.extractTags(tagStr);
        loginUser.setFollowedTags(tags);
        userHelper.updateUser(loginUser);
        originalPosts = postHelper.getFollowedPosts(tags);
        filterPosts();
    }

    public void populateUserInfo() {
        loginUser = userHelper.getCompleteUser(loginUser);
    }

    public List<Post> getPosts() {
        if (filteredPosts == null) return originalPosts;
        System.out.println("getting posts " + filteredPosts.size());
        return filteredPosts;
    }

    public Post getInputPost() {
        return inputPost;
    }

    public boolean isCustomPost() {
        return customPost;
    }

    public void setCustomPost(boolean customPost) {
        this.customPost = customPost;
    }

    public String boldMatchedText(String title) {
        return title.replaceAll("(?i)(^|)(" + dropdownFilter + ")(|$)", "$1<b>$2</b>$3");
    }

    public void dropdownItemSelected(SelectEvent event) {
        MediaTitle selectedTitle = (MediaTitle) event.getObject();
        inputPost.setMediaTitle(selectedTitle);
    }

    public List<MediaTitle> mediaTitleDropdown(String filter) {
        this.dropdownFilter = filter.toLowerCase();
        List<MediaTitle> titles = mediaTitleHelper.getMatchedMediaTitles(filter);
        if (customPost) return titles;
        List<Multi> results = TmdbHelper.getTmdbSearch().searchMulti(filter, "en", 1).getResults();
        int num = Math.min(results.size(), Math.min(5, 10 - titles.size()));
        for (int i = 0; i < num; i++) {
            Multi result = results.get(i);
            MediaTitle title = TmdbHelper.getMediaTitleFromMulti(result);
            if (title != null) {
                if (titles.contains(title)) {
                    num = Math.min(results.size(), num + 1);
                    continue;
                }
                title.setSuggested(true);
                titles.add(title);
            }
        }
        return titles;
    }

    public void likeOrUnlikePost(Post post) {
        System.out.println("Liking post " + post);
        if (!isUserLoggedIn) return;
        post.likeOrUnlike(loginUser);
        postHelper.updatePost(post);
    }

    public void submitPost() {
        inputPost.setUser(loginUser);
        if (customPost) inputPost.getMediaTitle().setCustom(true);
        mediaTitleHelper.processPostMediaTitle(inputPost);
        postHelper.post(inputPost);
        inputPost = new Post();
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void updateAvailableGenres() {
        availableGenres = new HashSet<>();
        for (Post post : originalPosts) {
            String[] postGenres = post.getMediaTitle().getGenre().split(", ");
            availableGenres.addAll(Arrays.asList(postGenres));
        }
    }

    public String getInputFilter() {
        return inputFilter;
    }

    public void setInputFilter(String inputFilter) {
        this.inputFilter = inputFilter;
    }

    public Set<String> getAvailableGenres() {
        if (availableGenres == null) updateAvailableGenres();
        return availableGenres;
    }

    public void setAvailableGenres(Set<String> availableGenres) {
        this.availableGenres = availableGenres;
    }

    public void filterPosts() {
//        System.out.println("Filtering posts");
        if ((genres == null || genres.isEmpty()) && (inputFilter == null || inputFilter.isEmpty())) {
            filteredPosts = new ArrayList<>(originalPosts);
            return;
        }
        filteredPosts = new LinkedList<>();
        for (Post post : originalPosts) {
            String[] postGenres = post.getMediaTitle().getGenre().split(", ");
            if (genres != null && !Collections.disjoint(genres, Arrays.asList(postGenres))) {
                filteredPosts.add(post);
            } else if (inputFilter != null && !inputFilter.isEmpty()) {
                if (post.getMediaTitle().getTitle().toLowerCase().contains(inputFilter.toLowerCase()) ||
                        post.getCaption().toLowerCase().contains(inputFilter.toLowerCase()) ||
                        post.getMediaTitle().getGenre().toLowerCase().contains(inputFilter.toLowerCase())) {
                    filteredPosts.add(post);
                }
            }
        }
        System.out.println(filteredPosts);
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    public String getUuid() {
        return uuid.toString();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "uuid=" + getUuid() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserBean)) return false;

        UserBean bean = (UserBean) o;

        return Objects.equals(uuid, bean.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}