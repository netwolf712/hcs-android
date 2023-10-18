package com.hcs.android.server.config;

/**
 * 全局配置项
 */
public class Config {
    /**
     * 服务器地址的配置属性
     */
    public static final String PREF_SERVER_ADDRESS = "hcs_server_server_address";

    /**
     * 服务器端口的配置属性
     */
    public static final String PREF_SERVER_PORT = "hcs_server_server_port";

    /**
     * 服务器socket响应超时时间
     */
    public static final String PREF_SOCKET_TIMEOUT_TIME = "hcs_server_socket_timeout_time";

    /**
     * 内部访问web的默认用户名
     */
    public static final String DEFAULT_INNER_WEB_USERNAME = "InnerAdmin";

    /**
     * 内部访问web的默认密码
     */
    public static final String DEFAULT_INNER_WEB_PASSWORD = "InnerAdmin";

    /**
     * 外部访问web的默认用户名
     */
    public static final String DEFAULT_OUTER_WEB_USERNAME = "Admin";

    /**
     * 外部访问web的默认密码
     */
    public static final String DEFAULT_OUTER_WEB_PASSWORD = "Admin";

    /**
     * 服务器访问web的默认用户名
     */
    public static final String DEFAULT_SERVICE_WEB_USERNAME = "ServiceAdmin";

    /**
     * 服务器访问web的默认密码
     */
    public static final String DEFAULT_SERVICE_WEB_PASSWORD = "ServiceAdmin";

    /**
     * 访问令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌自定义标识
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 登录超时时间30分钟
     */
    public static final Long EXPIRE_TIME = 1800000L;

    /**
     * 最大缓存空间
     * 1G
     */
    public static final int MAX_SIZE_IN_MEMORY = 10 * 1024 * 1024;
}
