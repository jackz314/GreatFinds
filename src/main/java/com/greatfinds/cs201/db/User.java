package com.greatfinds.cs201.db;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "getAllUsers", query = "SELECT u from User u"),
        @NamedQuery(name = "getUser", query = "SELECT u from User u WHERE u.email = :email"),
        @NamedQuery(name = "userExists", query = "SELECT count(u) from User u WHERE u.email = :email"),
        @NamedQuery(name = "userMatches", query = "SELECT count(u) from User u WHERE u.email = :email AND u.pwd = :pwd"),
})
public class User implements Serializable {

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

    public User(User user) {
        this(user.displayName, user.email, user.pwd);
    }

    public Set<String> getFollowedTags() {
        return followedTags;
    }

    public void setFollowedTags(Set<String> followedTags) {
        this.followedTags = followedTags;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

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
        if (bio == null || bio.isEmpty()) {
            try {
                URL bioGenUrl = new URL("https://www.twitterbiogenerator.com/generate");
                HttpURLConnection conn = (HttpURLConnection) bioGenUrl.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String bio = br.readLine();
                br.close();
                return bio;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return Objects.equals(userID, user.userID);
    }

    @Override
    public int hashCode() {
        return userID != null ? userID.hashCode() : 0;
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
