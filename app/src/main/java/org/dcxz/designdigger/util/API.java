package org.dcxz.designdigger.util;

/**
 * DribbbleAPI工具,提供OAuth2认证所需的资源以及Dribbble提供的接入口
 * <br/>
 * Created by DC on 2016/12/13.<br/>
 */

@SuppressWarnings("WeakerAccess")
public class API {
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
        public static final String ACCESS_TOKEN_DEFAULT = "e57b6ea8ad4ca7051144a6f6d584673f8d8c8c7f98db0c4f70d24b3c4fe5e988";
        /**
         * 动态ACCESS_TOKEN
         */
        public static String ACCESS_TOKEN = ACCESS_TOKEN_DEFAULT;

        /**
         * 替换ACCESS_TOKEN,标识用户登录与否
         *
         * @param accessToken 将要设置为的ACCESS_TOKEN
         */
        public static void setAccessToken(String accessToken) {
            ACCESS_TOKEN = accessToken;
        }

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
         * 活动范围,即用户可操作的范围
         */
        public static final String SCOPES = "scope";

        /**
         * 活动范围,即用户可操作的范围
         */
        public static class SCOPE {
            /**
             * 公开域,用户只能读取来自服务器的信息(默认)
             */
            public static final String PUBLIC = "public";

            /**
             * 可写域,允许用户接触自身的资源,但不包括评论与Shot
             */
            public static final String WRITE = "write";

            /**
             * 评论域,允许用户创建,更新,删除评论
             */
            public static final String COMMENT = "comment";

            /**
             * 上传域,允许用户创建,更新,删除Shot与Attachment
             */
            public static final String UPLOAD = "upload";
            /**
             * 完整域
             */
            public static final String FULL = SCOPE.PUBLIC + "+" + SCOPE.COMMENT + "+" + SCOPE.WRITE + "+" + SCOPE.UPLOAD;
        }

        /**
         * 获取临时令牌的URL,需要配合{@link String#format(String, Object...)}使用<br/>
         * POST: https://dribbble.com/oauth/token/?...code=%s<br/>
         */
        public static final String TOKEN = "https://dribbble.com/oauth/token/?"
                + CLIENT_ID + "=" + CLIENT_ID_VALUE + "&"
                + CLIENT_SECRET + "=" + CLIENT_SECRET_VALUE + "&"
                + SCOPES + "=" + SCOPE.FULL + "&"
                + "code=%s";
    }

    /**
     * API接入点
     */
    public static class END_POINT {// TODO: 2016/12/22 规范接入点格式
        /**
         * 入口
         */
        public static final String ENTRY = "https://api.dribbble.com/v1";
        /**
         * 获取当前用户时使用的URL
         */
        public static final String USER = ENTRY + "/user";
        /**
         * 获取第一页Shots时使用的URL<br/>
         * 直接访问会获得popular,shots,now条件下的第一页的12个对象.
         */
        public static final String SHOTS = ENTRY + "/shots";
        /**
         * 获取特定页Shots时使用的URL<br/>
         * 直接访问会获得popular,shots,now条件下的特定页的12个对象.<br/>
         * 需要配合{@link String#format(String, Object...)}使用<br/>
         */
        public static final String SHOTS_PAGE = SHOTS + "/?page=%s";

        //"https://api.dribbble.com/v1/users/glebich/shots"请求指定用户的shots
        //"https://api.dribbble.com/v1/user/followers"当前ACCESS_TOKEN持有者(23448678)的粉丝列表
        //"https://api.dribbble.com/v1/user/following"当前ACCESS_TOKEN持有者的关注用户列表
        //"https://api.dribbble.com/v1/user/following/shots"当前ACCESS_TOKEN持有者的关注用户的shots列表
    }
}
