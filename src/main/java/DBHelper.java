import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@Stateless
public class DBHelper {

    @Inject
    private EntityManager entityManager;

    @Inject
    private BeanManager beanManager;

    public void post(Post post){
        beginSession();
        entityManager.persist(post);
        commit();
        beanManager.fireEvent(post);
    }

    public void beginSession(){
        entityManager.getTransaction().begin();
    }

    //commits all changes to DB
    public void commit(){
        entityManager.getTransaction().commit();
    }

    public List<Post> getAllPosts() {
        return entityManager.createNamedQuery("getAllPosts", Post.class).getResultList();
    }

}