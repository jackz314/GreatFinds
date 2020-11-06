import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@NamedQuery(name = "getAllPosts", query = "SELECT p from Post p")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postID;

    @OneToOne
    @JoinColumn(name = "userID")
    private User user;

    @OneToOne
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
    private List<String> tags;

    //mapped in comment class
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public Post(User user, MediaTitle mediaTitle, String caption, Integer rating, List<String> tags) {
        this.user = user;
        this.mediaTitle = mediaTitle;
        this.caption = caption;
        this.rating = rating;
        this.tags = tags;
    }

    public Post() {
        rating = -1;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    public void addLike(){
        ++numLikes;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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
                ", comments=" + comments +
                '}';
    }
}
