package com.hcs.android.maintain;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.DateUtil;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.TimeWorker;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.maintain.constant.PreferenceConstant;
import com.hcs.android.maintain.entity.TimeConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * NTP同步管理器
 */
public class SNTPManager {
    private final Context mContext;

    /**
     * NTP服务器
     */
    private String mNtpServer;

    /**
     * 是否启用ntp功能
     */
    private boolean mEnableNtp;

    /**
     * 更新周期
     * 单位：毫秒
     */
    private Long mUpdateTime;

    /**
     * 工作定时器
     */
    private TimeWorker mTimerWorker;

    /**
     * SNTP客户端
     */
    private SNTPClient mSNTPClient;

    /**
     * NTP服务器连接超时时间
     * 单位：毫秒
     */
    private final static int SERVER_CONNECT_TIME_OUT = 30000;

    private SNTPManager(Context context) {
        mContext = context;
        mSNTPClient = new SNTPClient();
        mNtpServer = SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_NTP_SERVER, mContext.getString(R.string.default_maintain_ntp_server));
        mUpdateTime = SettingsHelper.getInstance(mContext).getLong(PreferenceConstant.PREF_KEY_NTP_TIME, mContext.getResources().getInteger(R.integer.default_maintain_ntp_time));
        mEnableNtp = SettingsHelper.getInstance(mContext).getBoolean(PreferenceConstant.PREF_KEY_NTP_ENABLE, mContext.getResources().getBoolean(R.bool.default_maintain_ntp_enable));
    }

    @SuppressLint("StaticFieldLeak")
    private static SNTPManager mInstance = null;

    public static SNTPManager getInstance() {
        if (mInstance == null) {
            synchronized (SNTPManager.class) {
                if (mInstance == null) {
                    mInstance = new SNTPManager(BaseApplication.getAppContext());
                }
            }
        }
        return mInstance;
    }

    /**
     * 更新时间到RTC
     *
     * @param millisecond unix时间戳
     */
    public void updatedTime(Long millisecond) {
        //命令格式：date MMddHHmmyyyy.ss set
        String timeStr = DateUtil.formatDate(millisecond, "MMddHHmmyyyy.ss");
        ExeCommand.executeSuCmd("date " + timeStr + " set");
    }

    /**
     * 开始NTP同步
     */
    public void startSNTPClient() {
        if (StringUtil.isEmpty(mNtpServer) || !mEnableNtp) {
            stopSNTPClient();
            return;
        }
        if (mTimerWorker == null) {
            mTimerWorker = new TimeWorker();
        } else {
            //先停止之前的
            mTimerWorker.stopWork();
        }
        mTimerWorker.startWork(mUpdateTime, () -> {
            if (mSNTPClient.requestTime(mNtpServer, SERVER_CONNECT_TIME_OUT)) {
                long now = mSNTPClient.getNtpTime() + SystemClock.elapsedRealtime() - mSNTPClient.getNtpTimeReference();
                KLog.i("get ntp time " + DateUtil.formatDate(new Date(now), DateUtil.FormatType.yyyyMMddHHmmss));
                long currentTime = System.currentTimeMillis();
                //只有时差相差比较大的时候才需要更新时间
                if (Math.abs(currentTime - now) > SERVER_CONNECT_TIME_OUT) {
                    KLog.i("current time " + DateUtil.formatDate(new Date(currentTime), DateUtil.FormatType.yyyyMMddHHmmss) + ",needed to update");
                    updatedTime(now);
                }
            }
        });
    }

    /**
     * 关闭NTP同步
     */
    public void stopSNTPClient() {
        if (mTimerWorker != null) {
            mTimerWorker.stopWork();
        }
    }

    /**
     * 更新NTP服务器
     */
    public void updateNTPServer(TimeConfig timeConfig) {
        if(timeConfig.getUnixTime() != null){
            updatedTime(timeConfig.getUnixTime());
        }
        mEnableNtp = timeConfig.isEnableNtp();
        mNtpServer = timeConfig.getNtpServer();
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_NTP_ENABLE, mEnableNtp);
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_NTP_SERVER, mNtpServer);
        if (mEnableNtp) {
            startSNTPClient();
        } else {
            stopSNTPClient();
        }
        if(timeConfig.isHour24() != isHour24()){
            setHour24(timeConfig.isHour24());
        }
        if(timeConfig.isAutoTimeZone() != isTimeZoneAuto()){
            setAutoTimeZone(timeConfig.isAutoTimeZone() ? 1 : 0);
        }
        if(!timeConfig.isAutoTimeZone()){
            setTimeZone(timeConfig.getTimeZoneId());
        }
    }

    /**
     * 获取时间配置
     */
    public TimeConfig getTimeConfig() {
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setUnixTime(convertLocalToUTC(new Date()));
        timeConfig.setEnableNtp(mEnableNtp);
        timeConfig.setNtpServer(mNtpServer);
        timeConfig.setHour24(isHour24());
        timeConfig.setAutoTimeZone(isTimeZoneAuto());
        timeConfig.setTimeZoneId(TimeZone.getDefault().getID());
        timeConfig.setTimeZoneName(TimeZone.getDefault().getDisplayName());
        return timeConfig;
    }

    /**
     * 将本地时间转换为UTC时间
     */
    @SuppressLint("SimpleDateFormat")
    private long convertLocalToUTC(Date date){
         SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Local time zone
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        //Time in GMT
        try{
            return Objects.requireNonNull(dateFormatLocal.parse(dateFormatGmt.format(date))).getTime();
        }catch (Exception e){
            KLog.e(e);
        }
        return date.getTime();
    }
    /**
     * 设置24小时制
     */
    public void setHour24(boolean isHour24) {
        if (isHour24) {
            android.provider.Settings.System.putString(mContext.getContentResolver(),
                    android.provider.Settings.System.TIME_12_24, "24");
        } else {
            android.provider.Settings.System.putString(mContext.getContentResolver(),
                    android.provider.Settings.System.TIME_12_24, "12");
        }
    }

    /**
     * 是否24小时制
     */
    public boolean isHour24() {
        return DateFormat.is24HourFormat(mContext);
    }

    /**
     * 是否自动获取时区
     */
    public boolean isTimeZoneAuto() {
        try {
            return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置自动获取时区
     */
    public void setAutoTimeZone(int autoTimeZone) {
        android.provider.Settings.Global.putInt(mContext.getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME_ZONE, autoTimeZone);
    }

    /**
     * 设置系统时区
     */
    public void setTimeZone(String timeZoneId) {
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        alarm.setTimeZone(timeZoneId);//默认时区的id
    }
}
