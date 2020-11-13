package com.greatfinds.cs201.tests.entry;

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
        entityManager.getTransaction().begin();
        entityManager.persist(newEntry);
        entityManager.getTransaction().commit();
        beanManager.fireEvent(newEntry);
    }

    public void post(Entry entry){
        entityManager.getTransaction().begin();
        entityManager.persist(entry);
        entityManager.getTransaction().commit();
        beanManager.fireEvent(entry);
    }

    public List<Entry> list() {
//        List<ui_tests.entry_test.Entry> l =  new LinkedList<>();
//        l.add(new ui_tests.entry_test.Entry("Hello"));
//        l.add(new ui_tests.entry_test.Entry("Test"));
//        return l;
        return entityManager
                .createNamedQuery("getMsg", Entry.class)
                .getResultList();
    }

}