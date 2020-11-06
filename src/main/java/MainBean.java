import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.annotation.FacesConfig;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

// enforce JSF 2.3
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
@Named
@ApplicationScoped
public class MainBean {

    @Inject
    private DBHelper dbHelper;

    private Post inputPost;

    private List<Post> posts;

    @Inject @Push
    private PushContext pushCh;

    //like constructor, called after bean is constructed
    @PostConstruct
    public void load() {
        posts = dbHelper.getAllPosts();
        inputPost = new Post();
    }


    public Post getInputPost(){
        return inputPost;
    }

    public void submitPost(){
        dbHelper.post(inputPost);
        inputPost = new Post();
    }

    public void onNewPost(@Observes Post post) {
        System.out.println("NEW POST: " + post);
        posts.add(0, post);
        pushCh.send("updateEntries");
//        comm.PushEP.sendAll("updateEntries");
    }

    public List<Post> getPosts() {
        return posts;
    }

}