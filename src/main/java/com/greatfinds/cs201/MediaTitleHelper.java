package com.greatfinds.cs201;

import com.greatfinds.cs201.db.MediaTitle;
import com.greatfinds.cs201.db.Post;

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

    public void processPostMediaTitle(Post post) {
        MediaTitle title = post.getMediaTitle();
        MediaTitle match = getExactMatchMediaTitle(title.getTitle(), title.getGenre());
        if (match != null) {//found in database, update post
            post.setMediaTitle(match);
        } else {//not found, create new in db
            addNewMediaTitle(title);
        }
    }

    public void addNewMediaTitle(MediaTitle mediaTitle) {
        beginSession();
        entityManager.persist(mediaTitle);
        commit();
        beanManager.fireEvent(mediaTitle);
    }

    public List<MediaTitle> getAllMediaTitles() {
        System.out.println("Getting media titles");
        entityManager.clear();
        return entityManager.createNamedQuery("getAllMediaTitles", MediaTitle.class)
                .getResultList();
    }

    // return a list of matched media titles based on the filter string
    // uses %FILTER% match in MySQL
    public List<MediaTitle> getMatchedMediaTitles(String filter) {
        //        System.out.println("MEDIA TITLE RESULT: " + resultList);
        entityManager.clear();
        return entityManager.createNamedQuery("getMatchedMediaTitles", MediaTitle.class)
                .setMaxResults(10)
                .setParameter("filter", filter)
                .getResultList();
    }

    public MediaTitle getExactMatchMediaTitle(String title, String genre) {
        entityManager.clear();
        return entityManager.createNamedQuery("getExactMatchMediaTitles", MediaTitle.class)
                .setParameter("title", title)
                .setParameter("genre", genre)
                .getResultList().stream().findFirst().orElse(null);
    }

    private void commit() {
        entityManager.getTransaction().commit();
    }

    private void beginSession() {
        entityManager.getTransaction().begin();
    }
}