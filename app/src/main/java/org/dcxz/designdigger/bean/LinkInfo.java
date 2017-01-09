package org.dcxz.designdigger.bean;

import java.io.Serializable;

/**
 * <br/>
 * Created by DC on 2016/12/13.<br/>
 */
@SuppressWarnings("WeakerAccess")
public class LinkInfo implements Serializable {
    public static final long serialVersionUID = 0L;

    /**
     * 个人主页
     */
    private String web;
    /**
     * 推特地址
     */
    private String twitter;

    public LinkInfo() {
    }

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

    @Override
    public String toString() {
        return "LinkInfo{" +
                "web='" + web + '\'' +
                ", twitter='" + twitter + '\'' +
                '}';
    }
}
