
import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class UserNotificationService {

    @Inject
    private EntityManager entityManager;

    @Inject
    private BeanManager beanManager;

    public void create(String displayName, String email, String pwd) {
        User newUser = new User();
        newUser.setDisplayName(displayName);
        newUser.setEmail(email);
        newUser.setPwd(pwd);
        entityManager.persist(newUser);
        beanManager.fireEvent(newUser);
    }

    public void post(User user){
        entityManager.persist(user);
        beanManager.fireEvent(user);
    }

    public List<User> list() {
        List<User> l =  new LinkedList<>();
        l.add(new User("Hellouser", "myemail", "mypwd"));
        l.add(new User("Testuser", "thisemail", "thispwd"));
        return l;
        /*return entityManager
                .createNamedQuery("userinfo", User.class)
                .getResultList();*/
    }

}