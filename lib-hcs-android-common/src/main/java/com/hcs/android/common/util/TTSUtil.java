package com.hcs.android.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.log.KLog;

import java.util.Locale;

public class TTSUtil {
    private final Context mContext;


    private static final String TAG = "SpeechUtils";
    @SuppressLint("StaticFieldLeak")
    private static TTSUtil singleton;

    private final TextToSpeech textToSpeech; // TTS对象

    /**
     * TTS语速
     */
    public final static String TTS_SPEED_RATE = "tts_speed_rate";

    /**
     * 音调
     */
    public final static String TTS_PITCH = "tts_pitch";

    /**
     * 语速
     * 默认为正常语速
     */
    private final String DEFAULT_SPEED_RATE = "1.0";

    /**
     * 音调
     * 默认为女生
     */
    private final String DEFAULT_PITCH = "1.0";

    /**
     * 是否设置成功
     */
    private boolean mIsOpened = false;

    /**
     * 讲话结束监听器
     */
    private ISimpleCustomer<String> mSpeechFinishListener;

    public static TTSUtil getInstance(Context context) {
        if (singleton == null) {
            synchronized (TTSUtil.class) {
                if (singleton == null) {
                    singleton = new TTSUtil(context);
                }
            }
        }
        return singleton;
    }

    public TTSUtil(Context context) {
        mContext = context;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    //textToSpeech.setLanguage(Locale.US);
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    textToSpeech.setPitch(getPitch());// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setSpeechRate(getSpeedRate());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //系统不支持中文播报
                        KLog.e("chinese not supported");
                        mIsOpened = false;
                    }else{
                        mIsOpened = true;
                    }
                }
            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                if(mSpeechFinishListener != null){
                    mSpeechFinishListener.accept(s);
                }
            }

            @Override
            public void onError(String s) {
                //报错也算讲话结束
                if(mSpeechFinishListener != null){
                    mSpeechFinishListener.accept(s);
                }
            }
        });
    }

    public void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text,
                    TextToSpeech.QUEUE_ADD, null,"speechId");
        }

    }

    public void setSpeechFinishListener(ISimpleCustomer<String> speechFinishListener){
        mSpeechFinishListener = speechFinishListener;
    }
    public void stop(){
        if(textToSpeech != null && textToSpeech.isSpeaking()){
            textToSpeech.stop();
        }
    }

    /**
     * 设置语速
     */
    public void setSpeedRate(float speedRate){
        if(textToSpeech != null){
            textToSpeech.setSpeechRate(speedRate);
        }
        SettingsHelper.getInstance(mContext).putData(TTS_SPEED_RATE,String.valueOf(speedRate));
    }

    /**
     * 获取语速
     */
    public float getSpeedRate(){
        String speedStr = SettingsHelper.getInstance(mContext).getString(TTS_SPEED_RATE,DEFAULT_SPEED_RATE);
        try{
            return Float.parseFloat(speedStr);
        }catch (Exception e){
            KLog.e(e);
        }
        return Float.parseFloat(DEFAULT_SPEED_RATE);
    }

    /**
     * 设置音调
     */
    public void setPitch(float pitch){
        if(textToSpeech != null){
            textToSpeech.setPitch(pitch);
        }
        SettingsHelper.getInstance(mContext).putData(TTS_PITCH,String.valueOf(pitch));
    }

    /**
     * 获取音调
     */
    public float getPitch(){
        String pitchStr = SettingsHelper.getInstance(mContext).getString(TTS_PITCH,DEFAULT_PITCH);
        try{
            return Float.parseFloat(pitchStr);
        }catch (Exception e){
            KLog.e(e);
        }
        return Float.parseFloat(DEFAULT_PITCH);
    }

    /**
     * TTS是否正在讲话
     */
    public boolean isSpeaking(){
        if(textToSpeech != null){
            return textToSpeech.isSpeaking();
        }
        return false;
    }
}
