package org.dcxz.designdigger.bean;

import java.io.Serializable;

/**
 * <br/>
 * Created by DC on 2017/1/14.<br/>
 */

public class CommentInfo implements Serializable {
    public static final long serialVersionUID = 1L;
    private int id;
    private String body;
    private int likes_count;
    private String likes_url;
    private String created_at;
    private String updated_at;
    private UserInfo user;

    public CommentInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public String getLikes_url() {
        return likes_url;
    }

    public void setLikes_url(String likes_url) {
        this.likes_url = likes_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
