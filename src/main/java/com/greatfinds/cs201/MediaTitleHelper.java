package com.greatfinds.cs201;

import com.greatfinds.cs201.db.MediaTitle;

import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@Stateless
public class MediaTitleHelper {

    @Inject
    private EntityManager entityManager;

    @Inject
    private BeanManager beanManager;

    public void addNewMediaTitle(MediaTitle mediaTitle) {
        beginSession();
        entityManager.persist(mediaTitle);
        commit();
        beanManager.fireEvent(mediaTitle);
    }

    public List<MediaTitle> getAllMediaTitles() {
        System.out.println("Getting media titles");
        return entityManager
                .createNamedQuery("getAllMediaTitles", MediaTitle.class)
                .getResultList();
    }

    // return a list of matched media titles based on the filter string
    // uses %FILTER% match in MySQL
    public List<MediaTitle> getMatchedMediaTitles(String filter) {
        return entityManager.createNamedQuery("getMatchedMediaTitles", MediaTitle.class)
                .setParameter("filter", filter)
                .getResultList();
    }

    private void commit() {
        entityManager.getTransaction().commit();
    }

    private void beginSession() {
        entityManager.getTransaction().begin();
    }
}