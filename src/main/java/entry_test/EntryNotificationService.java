package entry_test;

import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@Stateless
public class EntryNotificationService {

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
//        List<entry_test.Entry> l =  new LinkedList<>();
//        l.add(new entry_test.Entry("Hello"));
//        l.add(new entry_test.Entry("Test"));
//        return l;
        return entityManager
            .createNamedQuery("msg", Entry.class)
            .getResultList();
    }

}