package com.greatfinds.cs201.db;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "getAllUsers", query = "SELECT u from User u"),
        @NamedQuery(name = "getUser", query = "SELECT u from User u WHERE u.email = :email"),
        @NamedQuery(name = "userExists", query = "SELECT count(u) from User u WHERE u.email = :email"),
        @NamedQuery(name = "userMatches", query = "SELECT count(u) from User u WHERE u.email = :email AND u.pwd = :pwd"),
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    private String displayName, email, pwd, bio;

    @ElementCollection
    private Set<String> followedTags;

    public User(String displayName, String email, String pwd) {
        this.displayName = displayName;
        this.email = email;
        this.pwd = pwd;
    }

    public User() {

    }

    public Set<String> getFollowedTags() {
        return followedTags;
    }

    public void setFollowedTags(Set<String> followedTags) {
        this.followedTags = followedTags;
    }

    public String getDisplayName() { return displayName == null ? "" : displayName; }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Long getUserID() {
        return userID;
    }

    public void followTag(String tag) {
        followedTags.add(tag);
    }

    public void unfollowTag(String tag) {
        followedTags.remove(tag);
    }

    @Override
    public String toString() {
        return "User{" +
                "name=" + displayName +
                ", email='" + email +
                ", pwd='" + pwd +
                ", bio='" + bio +
                ", followedTags='" + followedTags + '\'' +
                '}';
    }
}
