package com.greatfinds.cs201;

import com.greatfinds.cs201.db.Post;
import com.greatfinds.cs201.db.em.EMF;

import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
public class PostHelper {

    @Inject
    private EntityManager entityManager;

    @Inject
    private BeanManager beanManager;

    public Set<String> extractTags(String caption) {
        Matcher matcher = Pattern.compile("#(\\w+)").matcher(caption);
        return matcher.results().map(matchResult -> matchResult.group().replace("#", ""))
                .collect(Collectors.toSet());
    }

    public void post(Post post) {
        Set<String> tags = extractTags(post.getCaption());
        tags.add("all");
        post.setTags(tags);
        beginSession();
        entityManager.persist(post);
        commit();
        beanManager.fireEvent(new PostUpdate(post, PostUpdate.Type.CREATED));
    }

    public void beginSession() {
        entityManager.getTransaction().begin();
    }

    //commits all changes to DB
    public void commit() {
        entityManager.getTransaction().commit();
    }

    //todo maybe a better way to do this query?
    public List<Post> getFollowedPosts(Set<String> tags) {
        if (tags == null || tags.isEmpty()) return getAllPosts();
        StringJoiner joiner = new StringJoiner(" OR ", "SELECT p from Post p WHERE ", " ORDER BY p.timestamp DESC");
        int i = 0;
        for (; i < tags.size(); i++) {
            String s = ":var" + i + " MEMBER OF p.tags";
            joiner.add(s);
        }
        String queryStr = joiner.toString();
        System.out.println("followed posts query: " + queryStr);
        entityManager.clear();
        entityManager.clear();
        entityManager.close();
        entityManager = EMF.createEntityManager();
        TypedQuery<Post> query = entityManager.createQuery(queryStr, Post.class);
        i = 0;
        for (String tag : tags) query.setParameter("var" + i++, tag);
        return query.getResultList();
    }

    public List<Post> getAllPosts() {
        entityManager.clear();
        return entityManager.createNamedQuery("getAllPosts", Post.class).getResultList();
    }

    public void updatePost(Post post) {
        beginSession();
        entityManager.merge(post);
        commit();
        beanManager.fireEvent(new PostUpdate(post, PostUpdate.Type.MODIFIED));
    }

}