package com.hcs.android.client.entity;


import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitConfig {

    private String baseUrl; //请求基本路径

    private boolean retryOnConnectionFailure = true; //错误重新连接

    private long connectTimeout = 30;//连接超时时间,单位秒

    private long readTimeout = 10;//设置新连接的默认读取超时,单位秒

    private long writeTimeout = 10;//设置新连接的默认写入超时,单位秒

    private String contentType = "application/json";//请求头类型

    private String charset = "UTF-8";//字符编码格式

    private HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.BODY;//日志级别

    private boolean addLogInterceptor = true;//是否添加日志拦截器

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public void setRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public HttpLoggingInterceptor.Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(HttpLoggingInterceptor.Level logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isAddLogInterceptor() {
        return addLogInterceptor;
    }

    public void setAddLogInterceptor(boolean addLogInterceptor) {
        this.addLogInterceptor = addLogInterceptor;
    }
}
