package org.dcxz.designdigger.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 单个的Shot("投篮")对象<br/>
 * 每一个属性都紧跟着一个关键字以便进行键值对访问.
 * <br/>
 * Created by DC on 2016/12/13.<br/>
 */

public class Entity_Shot implements Serializable {
    public static final long serialVersionUID = 0L;
    /**
     * id,int类型应该足够了.
     */
    private int id;
    /**
     * 作品名
     */
    private String title;
    /**
     * 作品描述
     */
    private String description;
    /**
     * 图像宽度
     */
    private int width;
    /**
     * 图像高度
     */
    private int height;
    /**
     * 图像对象
     */
    private Entity_Images images;
    /**
     * 总浏览数
     */
    private int views_count;
    /**
     * 总点赞数
     */
    private int likes_count;
    /**
     * 总评论数
     */
    private int comments_count;
    /**
     * 总附件数
     */
    private int attachments_count;
    /**
     * 总"篮板球"数
     */
    private int rebounds_count;
    /**
     * 球桶
     */
    private int buckets_count;
    /**
     * 创建时间
     */
    private String created_at;
    /**
     * 更新时间
     */
    private String updated_at;
    /**
     * 该作品在Internet上的地址
     */
    private String html_url;
    /**
     * 附件地址,不确定是否支持多地址
     */
    private String attachments_url;
    /**
     * 球桶地址
     */
    private String buckets_url;
    /**
     * 评论地址
     */
    private String comments_url;
    /**
     * 点赞地址
     */
    private String likes_url;
    /**
     * 项目地址
     */
    private String projects_url;
    /**
     * "打篮板",向某作品致敬,修改
     */
    private String rebounds_url;
    /**
     * 是不是动态图
     */
    private boolean animated;
    /**
     * 作品标签
     */
    private ArrayList<String> tags;
    /**
     * 用户对象
     */
    private Entity_User user;
    /**
     * 所属团队
     */
    private Entity_Team team;

    public Entity_Shot() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Entity_Images getImages() {
        return images;
    }

    public void setImages(Entity_Images images) {
        this.images = images;
    }

    public int getViews_count() {
        return views_count;
    }

    public void setViews_count(int views_count) {
        this.views_count = views_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getAttachments_count() {
        return attachments_count;
    }

    public void setAttachments_count(int attachments_count) {
        this.attachments_count = attachments_count;
    }

    public int getRebounds_count() {
        return rebounds_count;
    }

    public void setRebounds_count(int rebounds_count) {
        this.rebounds_count = rebounds_count;
    }

    public int getBuckets_count() {
        return buckets_count;
    }

    public void setBuckets_count(int buckets_count) {
        this.buckets_count = buckets_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
        this.created_at = created_at.replace("T", " ").replace("Z", "");
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
        this.updated_at = updated_at.replace("T", " ").replace("Z", "");
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getAttachments_url() {
        return attachments_url;
    }

    public void setAttachments_url(String attachments_url) {
        this.attachments_url = attachments_url;
    }

    public String getBuckets_url() {
        return buckets_url;
    }

    public void setBuckets_url(String buckets_url) {
        this.buckets_url = buckets_url;
    }

    public String getComments_url() {
        return comments_url;
    }

    public void setComments_url(String comments_url) {
        this.comments_url = comments_url;
    }

    public String getLikes_url() {
        return likes_url;
    }

    public void setLikes_url(String likes_url) {
        this.likes_url = likes_url;
    }

    public String getProjects_url() {
        return projects_url;
    }

    public void setProjects_url(String projects_url) {
        this.projects_url = projects_url;
    }

    public String getRebounds_url() {
        return rebounds_url;
    }

    public void setRebounds_url(String rebounds_url) {
        this.rebounds_url = rebounds_url;
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Entity_User getUser() {
        return user;
    }

    public void setUser(Entity_User user) {
        this.user = user;
    }

    public Entity_Team getTeam() {
        return team;
    }

    public void setTeam(Entity_Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "Entity_Shot{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", images=" + images +
                ", views_count=" + views_count +
                ", likes_count=" + likes_count +
                ", comments_count=" + comments_count +
                ", attachments_count=" + attachments_count +
                ", rebounds_count=" + rebounds_count +
                ", buckets_count=" + buckets_count +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", html_url='" + html_url + '\'' +
                ", attachments_url='" + attachments_url + '\'' +
                ", buckets_url='" + buckets_url + '\'' +
                ", comments_url='" + comments_url + '\'' +
                ", likes_url='" + likes_url + '\'' +
                ", projects_url='" + projects_url + '\'' +
                ", rebounds_url='" + rebounds_url + '\'' +
                ", animated=" + animated +
                ", tags=" + tags +
                ", user=" + user +
                ", team=" + team +
                '}';
    }
}
