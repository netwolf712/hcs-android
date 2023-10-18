package com.hcs.android.maintain.constant;

public class PreferenceConstant {
    /**
     * 是否开启dmesg
     */
    public final static String PREF_KEY_LOG_DMESG = "hcs_maintain_log_dmesg";

    /**
     * 是否开启logcat
     */
    public final static String PREF_KEY_LOG_LOGCAT = "hcs_maintain_log_logcat";

    /**
     * 是否开启pcap
     */
    public final static String PREF_KEY_LOG_PCAP = "hcs_maintain_log_pcap";

    /**
     * 缓存内的文件保存时间
     */
    public final static String PREF_KEY_CACHE_KEEP_TIME = "hcs_maintain_cache_keep_time";

    /**
     * 缓存内的文件大小总和上限
     */
    public final static String PREF_KEY_MAX_CACHE_SIZE = "hcs_maintain_max_cache_size";

    /**
     * 上传文件存放路径
     */
    public final static String PREF_KEY_UPLOAD_FILE_PATH = "hcs_maintain_upload_file_path";

    /**
     * 产品名称
     */
    public final static String PREF_KEY_PRODUCT_NAME = "hcs_maintain_product_name";

    /**
     * 是否启用NTP服务器
     */
    public final static String PREF_KEY_NTP_ENABLE = "hcs_maintain_ntp_enable";

    /**
     * NTP服务器
     */
    public final static String PREF_KEY_NTP_SERVER = "hcs_maintain_ntp_server";

    /**
     * NTP更新周期
     */
    public final static String PREF_KEY_NTP_TIME = "hcs_maintain_ntp_time";
}
