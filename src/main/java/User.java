import javax.persistence.*;
import java.util.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userID;

    private String displayName, email, pwd, bio;

    @ElementCollection
    private Set<String> followedTags;

    public User(Long userID, String displayName, String email, String pwd, String bio) {
        this.userID = userID;
        this.displayName = displayName;
        this.email = email;
        this.pwd = pwd;
        this.bio = bio;
        followedTags = new HashSet<String>();
    }

    public User() {
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDisplayName(){
        return displayName;
    }
    public String getEmail(){
        return email;
    }
    public String getPwd(){
        return pwd;
    }
    public String getBio(){
        return bio;
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
}
