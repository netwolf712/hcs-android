package com.hcs.android.common.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;

/**
 * 设置助手类
 */
public class SettingsHelper{

    /**
     * 属性键值的前缀
     */
    public final static String PREF_KEY_PREFIX = "pref_key_";

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static SettingsHelper mInstance;
    public static SettingsHelper getInstance(Context context){
        mContext = context;
        if(mInstance == null){
            synchronized (SettingsHelper.class){
                mInstance = new SettingsHelper();
            }
        }
        return mInstance;
    }
    private Context getContext(){
        return mContext;
    }
    /**
     * 回填数据
     * @param key 键值
     * @param value 需要填入的值
     */
    public void putData(String key,int value){
        putData(key,"",value);
    }
    public void putData(String key,String keyIndex,int value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putInt(getKey(key,keyIndex),value);
        editor.apply();
    }

    public void putData(String key,boolean value){
        putData(key,"",value);
    }
    public void putData(String key,String keyIndex,boolean value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean(getKey(key,keyIndex),value);
        editor.apply();
    }

    public void putData(String key,long value){
        putData(key,"",value);
    }
    public void putData(String key,String keyIndex,long value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putLong(getKey(key,keyIndex),value);
        editor.apply();
    }

    public void putData(String key,String value){
        putData(key,"",value);
    }

    public void putData(String key,String keyIndex,String value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(getKey(key,keyIndex),value);
        editor.apply();
    }

    /**
     * 获取配置项的键值
     * @param keyName 键名称
     * @return "pref_key_" + 键名称，返回的字符串
     */
    private String getKey(String keyName){
        return getContext().getString(ResourceUtil.getStringId(getContext(),PREF_KEY_PREFIX + keyName));
    }

    /**
     * 获取配置项的键值
     * @param keyPrefix 键前缀
     * @param keyIndex 键索引
     * @return "pref_key_" + 键名称 + 键索引，返回的字符串
     */
    @NonNull
    private String getKey(String keyPrefix, String keyIndex){
        return getKey(keyPrefix) + keyIndex;
    }

    /**
     * 对mSharedPreferences.getInt的封装
     * 有些属性确实是int类型，但读取时认为是字符串
     * 此时需要以字符串的形式读取再转成int
     * @param key 键值
     * @param defaultValue 默认值
     * @return 读取的数据
     */
    public int getInt(String key,int defaultValue){
        return getInt(key,"",defaultValue);
    }
    public int getInt(String key,String keyIndex,int defaultValue){
        try{
            return PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getKey(key,keyIndex),defaultValue);
        }catch (Exception e) {
            String value = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getKey(key), String.valueOf(defaultValue));
            if (!StringUtil.isEmpty(value)) {
                try {
                    return Integer.parseInt(value);
                } catch (Exception sube) {
                    KLog.e("get key " + key + " failed", sube);
                }
            }
        }
        return defaultValue;
    }

    public String getString(String key,String defaultValue){
        return getString(key,"",defaultValue);
    }
    public String getString(String key,String keyIndex,String defaultValue){
        try{
            return PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getKey(key,keyIndex),defaultValue);
        }catch (Exception e){
            KLog.e(e);
        }
        return defaultValue;
    }

    public boolean getBoolean(String key,boolean defaultValue){
        return getBoolean(key,"",defaultValue);
    }
    public boolean getBoolean(String key,String keyIndex,boolean defaultValue){
        try{
            return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getKey(key,keyIndex),defaultValue);
        }catch (Exception e){
            KLog.e(e);
        }
        return defaultValue;
    }

    public long getLong(String key,long defaultValue){
        return getLong(key,"",defaultValue);
    }
    public long getLong(String key,String keyIndex,long defaultValue){
        try{
            return PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(getKey(key,keyIndex),defaultValue);
        }catch (Exception e){
            KLog.e(e);
        }
        return defaultValue;
    }
}
