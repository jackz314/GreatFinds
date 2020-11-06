import javax.persistence.*;

@Entity
@Table(name = "mediaTitles")
public class MediaTitle {
    //mediaTitleID, title, genre

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaTitleID;

    private String genre;
    private String title;

    public MediaTitle(String title, String genre){
        this.title = title;
        this.genre = genre;
    }

    public MediaTitle(){}

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }
    public Long getMediaTitleID() {
        return mediaTitleID;
    }

    public void setMediaTitleID(Long ID) {
        this.mediaTitleID = ID;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPostID(String title) {
        this.title = title;
    }
}
