import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long postID;

    //TODO waiting for user and media title to map to them directly
    private Long userID;
    private Long mediaTitleID;

    private String caption;
    private Integer rating, numLikes = 0;

    //automatic timestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, insertable = false)
    private Date timestamp;

    @ElementCollection
    private List<String> tags;

    //mapped in comment class
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;


    public Post(Long userID, Long mediaTitleID, String caption, Integer rating, List<String> tags) {
        this.userID = userID;
        this.mediaTitleID = mediaTitleID;
        this.caption = caption;
        this.rating = rating;
        this.tags = tags;
    }

    public Post() {

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

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getMediaTitleID() {
        return mediaTitleID;
    }

    public void setMediaTitleID(Long mediaTitleID) {
        this.mediaTitleID = mediaTitleID;
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

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
}
