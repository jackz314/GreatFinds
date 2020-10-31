import javax.persistence.*;
import java.util.Date;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long commentID;
    private Long postID;
    private Long userID;
    private String text;

    //automatic timestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, insertable = false)
    private Date timestamp;

    //map to post
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public Long getCommentID() {
        return commentID;
    }

    public void setCommentID(Long commentID) {
        this.commentID = commentID;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }


}
