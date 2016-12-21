package org.dcxz.designdigger.util;

/**
 * DribbbleAPI工具,提供OAuth2认证所需的资源以及Dribbble提供的接入口
 * <br/>
 * Created by DC on 2016/12/13.<br/>
 */

@SuppressWarnings("WeakerAccess")
public class API {
    /**
     * API接入点
     */
    public static final String END_POINT = "https://api.dribbble.com/v1";
    /**
     * APIShot请求接入点,直接访问会获得popular,shots,now条件下的第一页的12个对象.
     */
    public static final String END_POINT_SHOTS = END_POINT + "/shots";
    /**
     * APIShot请求接入点,直接访问会获得popular,shots,now条件下的特定页的12个对象.
     */
    public static final String END_POINT_SHOTS_PAGE = END_POINT_SHOTS + "/?page=";

    /**
     * Oauth2认证所需要的信息
     */
    public static class Oauth2 {

        public static final String REDIRECT_URI = "redirect_uri";
        /**
         * 重定向地址,即注册应用时的CallbackURL
         */
        public static final String REDIRECT_URI_VALUE = "https://github.com/XieEDeHeiShou/DesignDigger";

        public static final String CLIENT_ID = "client_id";
        /**
         * APP注册时获得的客户端id
         */
        public static final String CLIENT_ID_VALUE = "e8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce";

        public static final String CLIENT_SECRET = "client_secret";
        /**
         * APP注册时获得的客户端秘钥
         */
        public static final String CLIENT_SECRET_VALUE = "fa2f50b0a7b4ccd358718b7c6cb3384f364326c850f5feaf92ba07c6ea5ee04d";

        /**
         * APP注册时获取的静态链接口令
         */
        public static final String ACCESS_TOKEN = "e57b6ea8ad4ca7051144a6f6d584673f8d8c8c7f98db0c4f70d24b3c4fe5e988";
        /**
         * 请求头需要包含的KEY
         */
        public static final String AUTHORIZATION = "Authorization";
        /**
         * 认证类型
         */
        public static final String AUTHORIZATION_TYPE = "Bearer ";
        /**
         * 请求认证所需的URL
         */
        public static final String AUTHORIZE = "https://dribbble.com/oauth/authorize/?"
                + CLIENT_ID + "=" + CLIENT_ID_VALUE + "&"
                + REDIRECT_URI + "=" + REDIRECT_URI_VALUE;
        /**
         * 获取临时令牌的URL,需要配合{@link String#format(String, Object...)}使用<br/>
         * https://dribbble.com/oauth/token/?...code=%s<br/>
         */
        public static final String TOKEN = "https://dribbble.com/oauth/token/?"
                + CLIENT_ID + "=" + CLIENT_ID_VALUE + "&"
                + CLIENT_SECRET + "=" + CLIENT_SECRET_VALUE + "&"
                + "code=%s";
    }

    //"https://api.dribbble.com/v1/users/glebich/shots"请求指定用户的shots
    //"https://api.dribbble.com/v1/user/followers"当前ACCESS_TOKEN持有者(23448678)的粉丝列表
    //"https://api.dribbble.com/v1/user/following"当前ACCESS_TOKEN持有者的关注用户列表
    //"https://api.dribbble.com/v1/user/following/shots"当前ACCESS_TOKEN持有者的关注用户的shots列表
}
