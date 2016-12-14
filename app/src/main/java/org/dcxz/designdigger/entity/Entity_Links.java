package org.dcxz.designdigger.entity;

import java.io.Serializable;

/**
 * <br/>
 * Created by DC on 2016/12/13.<br/>
 */
public class Entity_Links implements Serializable {
    public static final long serialVersionUID = 0L;

    /**
     * 个人主页
     */
    private String web;
    /**
     * 推特地址
     */
    private String twitter;

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public Entity_Links() {
    }

    @Override
    public String toString() {
        return "Entity_Links{" +
                "web='" + web + '\'' +
                ", twitter='" + twitter + '\'' +
                '}';
    }
}
