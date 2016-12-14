package org.dcxz.designdigger.entity;

import java.io.Serializable;

/**
 * <br/>
 * Created by DC on 2016/12/13.<br/>
 */
public class Entity_User implements Serializable {
    public static final long serialVersionUID = 0L;
    /**
     * id
     */
    private int id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户在Dribbble上的主页
     */
    private String html_url;
    /**
     * 头像地址
     */
    private String avatar_url;
    /**
     * bio,something like this:
     * <p>
     * Co-founder &amp; designer of
     * <a href=\"https://dribbble.com/dribbble\">@Dribbble</a>.
     * Principal of SimpleBits. Aspiring clawhammer banjoist.
     */
    private String bio;
    /**
     * 住址
     */
    private String location;
    /**
     * 链接地址
     */
    private Entity_Links links;
    /**
     * 总球桶数
     */
    private int buckets_count;
    /**
     * 总评论数
     */
    private int comments_received_count;
    /**
     * 总粉丝数
     */
    private int followers_count;
    /**
     * 总关注数
     */
    private int followings_count;
    private int likes_count;
    private int likes_received_count;
    /**
     * 总项目数
     */
    private int projects_count;
    /**
     * "打篮板"
     */
    private int rebounds_received_count;
    /**
     * 总作品数
     */
    private int shots_count;
    /**
     * 总团队数
     */
    private int teams_count;
    /**
     * 是否可上传作品
     */
    private boolean can_upload_shot;
    /**
     * 用户类型
     */
    private String type;
    /**
     * 是不是职业用户
     */
    private boolean pro;
    private String buckets_url;
    private String followers_url;
    private String likes_url;
    private String shots_url;
    private String teams_url;
    private String created_at;
    private String updated_at;

    public Entity_User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Entity_Links getLinks() {
        return links;
    }

    public void setLinks(Entity_Links links) {
        this.links = links;
    }

    public int getBuckets_count() {
        return buckets_count;
    }

    public void setBuckets_count(int buckets_count) {
        this.buckets_count = buckets_count;
    }

    public int getComments_received_count() {
        return comments_received_count;
    }

    public void setComments_received_count(int comments_received_count) {
        this.comments_received_count = comments_received_count;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFollowings_count() {
        return followings_count;
    }

    public void setFollowings_count(int followings_count) {
        this.followings_count = followings_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getLikes_received_count() {
        return likes_received_count;
    }

    public void setLikes_received_count(int likes_received_count) {
        this.likes_received_count = likes_received_count;
    }

    public int getProjects_count() {
        return projects_count;
    }

    public void setProjects_count(int projects_count) {
        this.projects_count = projects_count;
    }

    public int getRebounds_received_count() {
        return rebounds_received_count;
    }

    public void setRebounds_received_count(int rebounds_received_count) {
        this.rebounds_received_count = rebounds_received_count;
    }

    public int getShots_count() {
        return shots_count;
    }

    public void setShots_count(int shots_count) {
        this.shots_count = shots_count;
    }

    public int getTeams_count() {
        return teams_count;
    }

    public void setTeams_count(int teams_count) {
        this.teams_count = teams_count;
    }

    public boolean isCan_upload_shot() {
        return can_upload_shot;
    }

    public void setCan_upload_shot(boolean can_upload_shot) {
        this.can_upload_shot = can_upload_shot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPro() {
        return pro;
    }

    public void setPro(boolean pro) {
        this.pro = pro;
    }

    public String getBuckets_url() {
        return buckets_url;
    }

    public void setBuckets_url(String buckets_url) {
        this.buckets_url = buckets_url;
    }

    public String getFollowers_url() {
        return followers_url;
    }

    public void setFollowers_url(String followers_url) {
        this.followers_url = followers_url;
    }

    public String getLikes_url() {
        return likes_url;
    }

    public void setLikes_url(String likes_url) {
        this.likes_url = likes_url;
    }

    public String getShots_url() {
        return shots_url;
    }

    public void setShots_url(String shots_url) {
        this.shots_url = shots_url;
    }

    public String getTeams_url() {
        return teams_url;
    }

    public void setTeams_url(String teams_url) {
        this.teams_url = teams_url;
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

    @Override
    public String toString() {
        return "Entity_User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", html_url='" + html_url + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", bio='" + bio + '\'' +
                ", location='" + location + '\'' +
                ", links=" + links +
                ", buckets_count=" + buckets_count +
                ", comments_received_count=" + comments_received_count +
                ", followers_count=" + followers_count +
                ", followings_count=" + followings_count +
                ", likes_count=" + likes_count +
                ", likes_received_count=" + likes_received_count +
                ", projects_count=" + projects_count +
                ", rebounds_received_count=" + rebounds_received_count +
                ", shots_count=" + shots_count +
                ", teams_count=" + teams_count +
                ", can_upload_shot=" + can_upload_shot +
                ", type='" + type + '\'' +
                ", pro=" + pro +
                ", buckets_url='" + buckets_url + '\'' +
                ", followers_url='" + followers_url + '\'' +
                ", likes_url='" + likes_url + '\'' +
                ", shots_url='" + shots_url + '\'' +
                ", teams_url='" + teams_url + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
