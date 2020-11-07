import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
        entityManager.getTransaction().begin();
        newUser.setEmail(email);
        newUser.setPwd(pwd);
        entityManager.persist(newUser);
        entityManager.getTransaction().commit();
        beanManager.fireEvent(newUser);
    }

    public void post(User user){
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        beanManager.fireEvent(user);
    }

//    return true if user with email & password match record in database
    public boolean userMatch(String email, String pwd){
        TypedQuery<Long> query = entityManager.createNamedQuery("userMatch", Long.class);
        query.setParameter("email", email);
        query.setParameter("pwd", pwd);
        return query.getSingleResult() == 1;
    }

    public List<User> list() {
//        List<user_test.User> l =  new LinkedList<>();
//        l.add(new user_test.User("Hello"));
//        l.add(new user_test.User("Test"));
//        return l;
        return entityManager
                .createNamedQuery("userInfo", User.class)
                .getResultList();
    }

}