package org.dcxz.designdigger.entity;

import java.io.Serializable;

/**
 * <br/>
 * Created by DC on 2016/12/13.<br/>
 */
public class Entity_Images implements Serializable {
    public static final long serialVersionUID = 0L;
    /**
     * 高清图像地址
     */
    private String hidpi;
    /**
     * 普通图像地址
     */
    private String normal;
    /**
     * 预览图像地址(200*150)
     */
    private String teaser;

    public Entity_Images() {
    }

    public String getHidpi() {
        return hidpi;
    }

    public void setHidpi(String hidpi) {
        this.hidpi = hidpi;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    @Override
    public String toString() {
        return "Entity_Images{" +
                "hidpi='" + hidpi + '\'' +
                ", normal='" + normal + '\'' +
                ", teaser='" + teaser + '\'' +
                '}';
    }
}
