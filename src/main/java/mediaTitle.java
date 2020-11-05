import javax.persistence.*;
import javax.persistence.Id;

@Entity
public class mediaTitle {
    //mediaTitleID, title, genre

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mediaTitleID;

    private String genre;

    private String title;

    public mediaTitle(String title, String genre){
        this.title = title;
        this.genre = genre;
    }

    public mediaTitle(){}

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
