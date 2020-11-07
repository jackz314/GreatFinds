package com.greatfinds.cs201;

import com.greatfinds.cs201.db.Post;

public class PostUpdate {
    private Post post;
    private Type type;

    public PostUpdate(Post post, Type type) {
        this.post = post;
        this.type = type;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PostUpdate{" +
                "post=" + post +
                ", type=" + type +
                '}';
    }

    public enum Type {
        CREATED, MODIFIED, DELETED
    }
}
