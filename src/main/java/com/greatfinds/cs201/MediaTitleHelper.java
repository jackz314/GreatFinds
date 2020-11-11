package com.greatfinds.cs201;

import com.greatfinds.cs201.db.MediaTitle;

import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.print.attribute.standard.Media;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
public class MediaTitleHelper {

    @Inject
    private EntityManager entityManager;

    @Inject
    private BeanManager beanManager;

    public void registerMediaTitle(MediaTitle mediaTitle) {
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

    private void commit() {
        entityManager.getTransaction().commit();
    }

    private void beginSession() {
        entityManager.getTransaction().begin();
    }
}