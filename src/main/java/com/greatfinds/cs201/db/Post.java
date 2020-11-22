package com.greatfinds.cs201.db;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "posts")
@NamedQueries({
        @NamedQuery(name = "getAllPosts", query = "SELECT p from Post p ORDER BY p.timestamp DESC"),
//    @NamedQuery(name = "getFollowedPosts", query = "SELECT p from Post p WHERE :tag MEMBER OF p.tags"),
})
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mediaTitleID")
    private MediaTitle mediaTitle;

    private String caption;
    private Integer rating, numLikes = 0;

    //automatic timestamp
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ElementCollection
    private Set<String> tags;

    @ElementCollection
    private Set<Long> likedUsers;

    /**
     * Create new instance of Post
     *
     * @param user       user who created the post
     * @param mediaTitle media title of the post
     * @param caption    text caption from user
     * @param rating     G-ratings/G-score
     * @param tags       a set of tags associated with this post
     */
    public Post(User user, MediaTitle mediaTitle, String caption, Integer rating, Set<String> tags) {
        this.user = user;
        this.mediaTitle = mediaTitle;
        this.caption = caption;
        this.rating = rating;
        this.tags = tags;
    }

    public Post() {
        mediaTitle = new MediaTitle();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Post)) return false;
        return this.postID.equals(((Post) other).postID);
    }

    public void likeOrUnlike(User user) {
        if (userLiked(user)) {
            removeLike(user);
        } else {
            addLike(user);
        }
    }

    public void addLike(User user) {
        if (likedUsers == null) likedUsers = new HashSet<>();
        boolean res = likedUsers.add(user.getUserID());
        if (res) {
            ++numLikes;
            System.out.println("added like to post from " + user);
        } else {
            System.out.println("add like to post failed from " + user);
        }
    }

    public void removeLike(User user) {
        if (likedUsers == null) return;
        boolean res = likedUsers.remove(user.getUserID());
        if (res) {
            --numLikes;
            System.out.println("added like to post from " + user);
        } else {
            System.out.println("add like to post failed from " + user);
        }
    }

    public boolean userLiked(User user) {
        if (likedUsers == null || user == null) return false;
        return likedUsers.contains(user.getUserID());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MediaTitle getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(MediaTitle mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getRating() {
        return rating;
    }

    public String getGscore() {
        int r = this.rating;
        if(this.rating==1) return "Garbage";
        else if(this.rating==2) return "Good";
        else return "Great";
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(Integer numLikes) {
        this.numLikes = numLikes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<Long> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(Set<Long> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public String getTimestampStr() {
        return new SimpleDateFormat("dd MMM YYYY, HH:mm:ss").format(timestamp);
    }

    @Override
    public String toString() {
        return "Post{" +
                "postID=" + postID +
                ", user=" + user +
                ", mediaTitle=" + mediaTitle +
                ", caption='" + caption + '\'' +
                ", rating=" + rating +
                ", numLikes=" + numLikes +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                '}';
    }
}
