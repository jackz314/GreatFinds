import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@Stateless
public class NotificationService {

    @Inject
    private EntityManager entityManager;

    @Inject
    private BeanManager beanManager;

    public void create(String message) {
        Entry newEntry = new Entry();
        newEntry.setMsg(message);
        entityManager.persist(newEntry);
        beanManager.fireEvent(newEntry);
    }

    public void post(Entry entry){
        entityManager.persist(entry);
        beanManager.fireEvent(entry);
    }

    public List<Entry> list() {
//        List<Entry> l =  new LinkedList<>();
//        l.add(new Entry("Hello"));
//        l.add(new Entry("Test"));
//        return l;
        return entityManager
            .createNamedQuery("msg", Entry.class)
            .getResultList();
    }

}