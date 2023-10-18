package com.hcs.android.business.constant;

public class PreferenceConstant {
    /**
     * 全局标题
     */
    public final static String PREF_KEY_WHOLE_TITLE = "whole_title";
    /**
     * 设备类型
     */
    public final static String PREF_KEY_DEVICE_TYPE = "device_type";

    /**
     * 本机号码的属性
     */
    public final static String PREF_KEY_PHONE_NUMBER = "phone_number";

    /**
     * 上级主机号码的属性
     */
    public final static String PREF_KEY_PARENT_NUMBER = "parent_number";

    /**
     * 服务器地址
     */
    public final static String PREF_KEY_SERVICE_ADDRESS = "service_address";

    /**
     * 服务器端口
     */
    public final static String PREF_KEY_SERVICE_PORT = "service_port";

    /**
     * 信令广播地址
     */
    public final static String PREF_KEY_MULTICAST_ADDRESS = "multicast_address";

    /**
     * 信令广播端口
     */
    public final static String PREF_KEY_MULTICAST_PORT = "multicast_port";

    /**
     * 语音广播地址
     */
    public final static String PREF_KEY_AUDIO_MULTICAST_ADDRESS = "audio_multicast_address";

    /**
     * 要求分机播放广播开始提示音
     */
    public final static String PREF_KEY_PLAY_AUDIO_MULTICAST_READ_CODE = "play_audio_multicast_ready_code";

    /**
     * 全局音频广播的音量
     */
    public final static String PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_VOLUME = "audio_multicast_group_whole_volume";

    /**
     * 全局音频广播的文件列表
     */
    public final static String PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_FILE_LIST = "audio_multicast_group_whole_file_list";


    /**
     * 全局音频广播的文件播放方式
     */
    public final static String PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_PLAY_TYPE = "audio_multicast_group_whole_play_type";

    /**
     * 检测自动广播的时间周期
     * 单位毫秒
     */
    public final static String PREF_KEY_AUDIO_MULTICAST_AUTO_CHECK_PERIOD = "audio_multicast_auto_check_period";

    /**
     * 病区号
     */
    public final static String PREF_KEY_SECTION_NO = "section_no";

    /**
     * 病区名称
     */
    public final static String PREF_KEY_SECTION_NAME = "section_name";

    /**
     * 主题（显示风格）
     */
    public final static String PREF_KEY_THEME = "theme";

    /**
     * 房间id
     */
    public final static String PREF_KEY_ROOM_NO = "room_no";

    /**
     * 房间名称
     */
    public final static String PREF_KEY_ROOM_NAME = "room_name";

    /**
     * 病床号
     */
    public final static String PREF_KEY_BED_NO = "bed_no";

    /**
     * 病床名称
     */
    public final static String PREF_KEY_BED_NAME = "bed_name";


    /**
     * 是否开启dmesg
     */
    public final static String PREF_KEY_LOG_DMESG = "log_dmesg";

    /**
     * 分区号
     */
    public final static String PREF_KEY_GROUP_SN = "group_sn";

    /**
     * 是否开启logcat
     */
    public final static String PREF_KEY_LOG_LOGCAT = "log_logcat";

    /**
     * 是否开启pcap
     */
    public final static String PREF_KEY_LOG_PCAP = "log_pcap";


    /**
     * 各种类型分机配置id前缀
     */
    public final static String PREF_KEY_CURRENT_CONFIG_ID_PREFIX = "current_config_id_";

    /**
     * 各种类型分机模板id前缀
     */
    public final static String PREF_KEY_CURRENT_TEMPLATE_ID_PREFIX = "current_template_id_";

    /**
     * 床头分机专有配置
     */
    /**
     * 屏保提示语
     */
    public final static String PREF_KEY_BED_SCREEN_SCREEN_SAVER = "bed_screen_screen_saver";

    /**
     * 屏保触发时间
     */
    public final static String PREF_KEY_BED_SCREEN_SCREEN_SAVER_TIME = "bed_screen_screen_saver_time";

    /**
     * 息屏时间
     */
    public final static String PREF_KEY_BED_SCREEN_SCREEN_SHUT_TIME = "bed_screen_screen_shut_time";

    /**
     * 功能列表数量
     */
    public final static String PREF_KEY_BED_SCREEN_FUNCTION_COUNT = "bed_screen_function_count";

    /**
     * 功能id前缀
     */
    public final static String PREF_KEY_BED_SCREEN_FUNCTION_ID_PREFIX = "bed_screen_function_id_prefix";

    /**
     * 功能名称前缀
     */
    public final static String PREF_KEY_BED_SCREEN_FUNCTION_NAME_PREFIX = "bed_screen_function_name_prefix";


    /**
     * 外接按钮id
     */
    public final static String PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_BUTTON_ID = "bed_screen_hotkey_external_button_id";

    /**
     * 外接按钮呼叫时显示的名称
     */
    public final static String PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_CALL_NAME = "bed_screen_hotkey_external_call_name";

    /**
     * 外接按钮是否为紧急呼叫
     */
    public final static String PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_EMERGENCY_CALL = "bed_screen_hotkey_external_is_emergency_call";

    /**
     * 手持按钮id
     */
    public final static String PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_BUTTON_ID = "bed_screen_hotkey_in_hand_button_id";

    /**
     * 手持按钮呼叫时显示的名称
     */
    public final static String PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_CALL_NAME = "bed_screen_hotkey_in_hand_call_name";

    /**
     * 手持按钮是否为紧急呼叫
     */
    public final static String PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_EMERGENCY_CALL = "bed_screen_hotkey_in_hand_is_emergency_call";


    /**
     * 门口屏功能列表数量
     */
    public final static String PREF_KEY_ROOM_SCREEN_FUNCTION_COUNT = "room_screen_function_count";

    /**
     * 门口屏功能id前缀
     */
    public final static String PREF_KEY_ROOM_SCREEN_FUNCTION_ID_PREFIX = "room_screen_function_id_prefix";

    /**
     * 门口屏功能名称前缀
     */
    public final static String PREF_KEY_ROOM_SCREEN_FUNCTION_NAME_PREFIX = "room_screen_function_name_prefix";

    /**
     * 门口屏屏保提示语
     */
    public final static String PREF_KEY_ROOM_SCREEN_SCREEN_SAVER = "room_screen_screen_saver";

    /**
     * 门口屏屏保触发时间
     */
    public final static String PREF_KEY_ROOM_SCREEN_SCREEN_SAVER_TIME = "room_screen_screen_saver_time";

    /**
     * 门口屏息屏时间
     */
    public final static String PREF_KEY_ROOM_SCREEN_SCREEN_SHUT_TIME = "room_screen_screen_shut_time";


    /**
     * 主机的一键呼叫号码
     */
    public final static String PREF_KEY_DIRECT_CALL_NO = "direct_call_no";

    /**
     * 心跳间隔
     * 单位毫秒
     */
    public final static String HEART_BEAT_SPAN = "heart_beat_span";

    /**
     * 工作现成间隔
     * 单位毫秒
     */
    public final static String WORK_LOOP_SPAN = "work_loop_span";

    /**
     * 默认最大值的前缀
     */
    public final static String DEFAULT_MAX_PRE = "default_max_";

    /**
     * 最大值的前缀
     */
    public final static String MAX_PRE = "max_";
    /**
     * 最大值的后缀
     */
    public final static String MAX_SUFFIX = "_count";

    /**
     * 主机基础号码
     */
    public final static String MASTER_BASE_NO = "master_base_no";

    /**
     * 最大病区数
     * 主机专有配置
     */
    public final static String MAX_SECTION_COUNT = "max_section_count";

    /**
     * 最大病房数
     * 主机专有配置
     */
    public final static String MAX_ROOM_COUNT = "max_room_count";

    /**
     * 最大床数
     * 主机专有配置
     */
    public final static String MAX_BED_COUNT = "max_bed_count";

    /**
     * 最大分组数
     * 主机专有配置
     */
    public final static String MAX_GROUP_COUNT = "max_group_count";

    /**
     * 最大走廊屏
     * 主机专有配置
     */
    public final static String MAX_CORRIDOR_COUNT = "max_corridor_count";

    /**
     * 最大卫生间
     * 主机专有配置
     */
    public final static String MAX_WASH_ROOM_COUNT = "max_wash_room_count";

    /**
     * 判断分机离线的时长
     * 单位毫秒
     */
    public final static String SLAVE_OFFLINE_TIME = "slave_offline_time";

    /**
     * 振铃提示音路径
     */
    public final static String RING_TONE_PATH = "ring_tone_path";

    /**
     * 回铃提示音路径
     */
    public final static String RING_BACK_TONE_PATH = "ring_back_tone_path";

    /**
     * TTS提示音模式
     */
    public final static String TTS_MODE = "tts_mode";


    /**
     * 分机与主机最大失去链接的次数
     * 若超过次数则分机采用广播形式寻找主机
     * 在此之前都采用点对点形式询问并等待回复
     */
    public final static String MAX_LOST_CONNECT_COUNT = "max_lost_connect_count";

    /**
     * 最大的呼叫等待时间
     * 分机呼叫主机时有一个等待主机摘机的最大等待时间
     * 若主机超过时间还未摘机，则呼叫主机的上一级主机
     */
    public final static String MAX_CALL_WAIT_TIME = "max_call_wait_time";

    /**
     * 以下为数据命令完整性处理3人组
     * （呼叫控制命令暂不予处理）
     * 具体策略为主机每次给分机发送数据更新请求时，对每个命令做计数
     * （命令计数分为主计数和分组计数，分组计数主要用于数据太大需要做分组发送，这个已经有UpdateDataHelper专门处理）
     * 分机在收到计数时根据顺序做处理，若数据不连续，则等待
     * 若等待过程中期望的数据一直没达到，则触发等待超时或后续命令过长两种熔断机制
     * 每种熔断机制触发后，都会重新发送REQ_GET_CONFIG请求，要求主机直接发送所有数据重新全部更新
     */
    /**
     * 最后一个处理的数据相关的命令的id
     */
    public final static String LAST_DATA_COMMAND_INDEX = "last_data_command_index";

    /**
     * 在期望的命令到达前允许等待累积最多未处理的命令数量
     * 若超过则重新发送配置请求命令，要求更新所有数据
     */
    public final static String MAX_WAIT_DATA_COMMAND_COUNT = "max_wait_data_command_count";

    /**
     * 期望的命令允许最大等待时间
     * 若超过则重新发送配置请求命令，要求更新所有数据
     */
    public final static String MAX_WAIT_DATA_COMMAND_TIME = "max_wait_data_command_time";

    /**
     * 字典数据的更新时间
     * 方便进行全局的字典数据更新
     */
    public final static String DICT_UPDATE_TIME = "dict_update_time";

    /**
     * 继任主机的更新时间
     * 方便进行全局的继任主机数据更新
     */
    public final static String STEP_MASTER_UPDATE_TIME = "step_master_update_time";

    /**
     * 时间段的更新时间
     * 方便进行全局的时间段数据更新
     */
    public final static String TIME_SLOT_UPDATE_TIME = "time_slot_update_time";

    /**
     * 托管的主机号码
     * 不为空表示当前主机托管中
     * 否则没在托管
     */
    public final static String TRUST_PHONE_NO = "trust_phone_no";

    /**
     * 使能自动托管工作
     */
    public final static String AUTO_TRUST = "auto_trust";

    /**
     * 自动托管检查周期
     */
    public final static String AUTO_TRUST_CHECK_TIME = "auto_trust_check_time";

    /**
     * 托管状态
     */
    public final static String TRUST_STATE = "trust_state";

    /**
     * 托管检查周期
     * 单位：毫秒
     * 托管设备使用此定时器定时发送托管通知，告知从设备当前的托管对象是自己
     * 被托管设备使用此定时器检查托管是否成功，若未成功则再次发送托管请求
     */
    public final static String TRUST_CHECK_TIME = "trust_check_time";

    /**
     * 病员隐私政策
     * 是否启用
     */
    public final static String PRIVACY_ENABLE = "privacy_enable";
    /**
     * 病员隐私政策
     * 启用病员隐私政策的设备类型
     * 多个设备类型间用半角,隔开
     */
    public final static String PRIVACY_DEVICE_TYPE = "privacy_device_type";

    /**
     * 病员隐私政策
     * 病员病员隐私的处理规则
     * 开始字符
     */
    public final static String PRIVACY_RULE_HIDE_START = "privacy_rule_hide_start";

    /**
     * 病员隐私政策
     * 病员病员隐私的处理规则
     * 隐藏长度
     */
    public final static String PRIVACY_RULE_HIDE_LENGTH = "privacy_rule_hide_length";

    /**
     * 病员隐私政策
     * 病员病员隐私的处理规则
     * 用于隐藏替换的字符
     */
    public final static String PRIVACY_RULE_REPLACE_CHARACTER = "privacy_rule_replace_character";

    /**
     * 音量调节
     * 主机扬声器音量
     */
    public final static String MASTER_SPEAKER_DAY_VOLUME = "master_speaker_day_volume";

    /**
     * 音量调节
     * 主机振铃音量
     */
    public final static String MASTER_RING_DAY_VOLUME = "master_ring_day_volume";

    /**
     * 音量调节
     * 主机按键音量
     */
    public final static String MASTER_KEY_DAY_VOLUME = "master_key_day_volume";

    /**
     * 音量调节
     * 分机扬声器音量
     */
    public final static String SLAVE_SPEAKER_DAY_VOLUME = "slave_speaker_day_volume";

    /**
     * 音量调节
     * 是否启用夜晚音量调节功能
     */
    public final static String NIGHT_VOLUME_SET_ENABLE = "night_volume_set_enable";

    /**
     * 音量调节-夜晚
     * 主机扬声器音量
     */
    public final static String MASTER_SPEAKER_NIGHT_VOLUME = "master_speaker_night_volume";


    /**
     * 音量调节-夜晚
     * 主机振铃音量
     */
    public final static String MASTER_RING_NIGHT_VOLUME = "master_ring_night_volume";

    /**
     * 音量调节-夜晚
     * 主机按键音量
     */
    public final static String MASTER_KEY_NIGHT_VOLUME = "master_key_night_volume";

    /**
     * 音量调节-夜晚
     * 分机扬声器音量
     */
    public final static String SLAVE_SPEAKER_NIGHT_VOLUME = "slave_speaker_night_volume";

    /**
     * 年龄计算策略
     * 是否使用虚岁
     * false 使用周岁，true，使用虚岁
     */
    public final static String AGE_RULE_USE_NOMINAL = "age_rule_use_nominal";

    /**
     * 年龄计算策略
     * 需要显示月份的年龄
     * （小于多少周岁的需要显示月份，格式为xx岁xx月）
     */
    public final static String AGE_RULE_SHOW_YEAR_MONTH_AGE = "age_rule_show_year_month_age";

    /**
     * 年龄计算策略
     * 需要显示天数的年龄
     * （小于多少月份的需要显示天数，格式为xx月xx天）
     */
    public final static String AGE_RULE_SHOW_MONTH_DAY_AGE = "age_rule_show_month_day_age";

    /**
     * 吸氧时间
     */
    public final static String OXYGEN_INHALATION_TIME = "oxygen_inhalation_time";

    /**
     * 用药时间
     */
    public final static String MEDICATION_TIME = "medication_time";

    /**
     * 输液时间
     */
    public final static String INFUSION_TIME = "infusion_time";

    /**
     * 皮试时间
     */
    public final static String SKIN_TEST_TIME = "skin_test_time";

    /**
     * 护理时间
     */
    public final static String NURSING_TIME = "nursing_time";
}
