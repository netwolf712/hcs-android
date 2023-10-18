package com.hcs.android.ui.player;

import static android.opengl.EGL14.EGL_BAD_ALLOC;
import static android.opengl.EGL14.EGL_BAD_CONFIG;
import static android.opengl.EGL14.EGL_BAD_MATCH;
import static android.opengl.EGL14.EGL_BAD_NATIVE_WINDOW;
import static android.opengl.EGL14.EGL_NO_SURFACE;
import static android.opengl.EGL14.EGL_SUCCESS;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;

import androidx.annotation.NonNull;


/**
 * 1、得到Egl实例
 * 2、得到默认的显示设备（就是窗口）
 * 3、初始化默认显示设备
 * 4、设置显示设备的属性
 * 5、从系统中获取对应属性的配置
 * 6、创建EglContext
 * 7、创建渲染的Surface
 * 8、绑定EglContext和Surface到显示设备中
 * 9、刷新数据，显示渲染场景
 */
public class EglHelper {

    /**
     * 查询EGL的版本信息
     */
    public static int getEglVersion(){
        //2. 得到默认的显示设备（就是窗口）
        EGLDisplay mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        //3. 初始化默认显示设备
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEglDisplay, version,0,version,1)) {
            throw new RuntimeException("eglInitialize failed");
        }
        //要支持GLES3.0 要求EGL版本在1.4以上。
        if(version[1] >= 4){
            return 3;
        }else{
            return 3;
        }
    }
    /***
     * 初始化 initEgl
     */
    public static void initEgl(@NonNull EglInfo mEglInfo) {
        //1. 得到Egl实例
        initEglDisplay(mEglInfo);
        initEglContext(mEglInfo);
        initEglSurface(mEglInfo);
        makeEglCurrent(mEglInfo);
    }

    /***
     * 创建一个原始共享上下文
     */
    public static EGLContext createSharedEglContext() {
        EglInfo mEglInfo = new EglInfo();

        initEglDisplay(mEglInfo);
        initEglContext(mEglInfo);
        return mEglInfo.getEglContext();
    }

    /**
     * 初始化显示设备
     */
    public static void initEglDisplay(@NonNull EglInfo mEglInfo) {

        //1. 得到默认的显示设备（就是窗口）
        EGLDisplay mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        //2. 初始化默认显示设备
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEglDisplay, version,0,version,1)) {
            throw new RuntimeException("eglInitialize failed");
        }

        //4. 设置显示设备的属性
        int[] attrib_list = new int[]{
                EGL14.EGL_RED_SIZE, mEglInfo.getRedSize(),
                EGL14.EGL_GREEN_SIZE, mEglInfo.getGreenSize(),
                EGL14.EGL_BLUE_SIZE, mEglInfo.getBlueSize(),
                EGL14.EGL_ALPHA_SIZE, mEglInfo.getAlphaSize(),
                EGL14.EGL_DEPTH_SIZE, mEglInfo.getDepthSize(),
                EGL14.EGL_STENCIL_SIZE, mEglInfo.getStencilSize(),
                EGL14.EGL_RENDERABLE_TYPE, mEglInfo.getRenderType(),//egl版本  2.0
                EGL14.EGL_NONE};

        int[] num_config = new int[1];
        if (!EGL14.eglChooseConfig(mEglDisplay, attrib_list,0, null,0, 1, num_config,0)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int numConfigs = num_config[0];
        if (numConfigs <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        mEglInfo.setListConfigs(num_config);
        mEglInfo.setNumConfigs(numConfigs);
        mEglInfo.setListAttrib(attrib_list);

        mEglInfo.setEglDisplay(mEglDisplay);
    }

    /**
     * 初始化上下文
     */
    public static void initEglContext(@NonNull EglInfo mEglInfo) {
        EGLDisplay mEglDisplay = mEglInfo.getEglDisplay();
        EGLContext mSharedEGLContext = mEglInfo.getSharedEglContext();

        //5. 从系统中获取对应属性的配置
        EGLConfig[] configs = new EGLConfig[mEglInfo.getNumConfigs()];
        if (!EGL14.eglChooseConfig(mEglDisplay, mEglInfo.getListAttrib(),0,
                configs,0, mEglInfo.getNumConfigs(), mEglInfo.getListConfigs(),0)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        EGLConfig eglConfig = chooseConfig(mEglInfo, configs);
        if (eglConfig == null) {
            eglConfig = configs[0];
        }
        mEglInfo.setEglConfig(eglConfig);

        //6. 创建EglContext
        int[] contextAttr = new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, mEglInfo.getEglContextClientVersion(),
                EGL14.EGL_NONE
        };

        mSharedEGLContext = (mSharedEGLContext == null) ? EGL14.EGL_NO_CONTEXT : mSharedEGLContext;

        mEglInfo.setEglContext(
                    EGL14.eglCreateContext(mEglDisplay, eglConfig, mSharedEGLContext, contextAttr,0));
    }

    /**
     * 初始化EglSurface
     */
    public static void initEglSurface(@NonNull EglInfo mEglInfo) {
        EGLSurface mEglSurface;
        EGLDisplay mEglDisplay = mEglInfo.getEglDisplay();

        checkEglSurface(mEglInfo);

        //7. 创建渲染的Surface
        if(mEglInfo.getUesPbufferSurface()) {
            // 显卡内存开辟区域
            mEglSurface = EGL14.eglCreatePbufferSurface(mEglDisplay,
                    mEglInfo.getEglConfig(), null,0);
        } else {
            // 渲染到提供的surface上
            // 该处是一个容易阻塞的地方
            mEglSurface = EGL14.eglCreateWindowSurface(mEglDisplay,
                    mEglInfo.getEglConfig(), mEglInfo.getSurface(), null,0);
        }

        if (mEglSurface == EGL_NO_SURFACE) {
            int error;
            while((error = EGL14.eglGetError()) != EGL_SUCCESS){
                switch(error) {
                    case EGL_BAD_MATCH:{
                        //提供的原生窗口不匹配或者不支持渲染
//                        throw new RuntimeException("dd 提供的原生窗口不匹配或者不支持渲染");
                    }
                    case EGL_BAD_CONFIG:{
                        //系统不支持该配置
//                        throw new RuntimeException("dd 系统不支持该配置");
                    }
                    case EGL_BAD_NATIVE_WINDOW:{
                        //提供的原生窗口无效
//                        throw new RuntimeException("dd 提供的原生窗口无效");
                    }
                    case EGL_BAD_ALLOC:{
                        //无法为新的EGL分配资源或者该窗口已经被关联
//                        throw new RuntimeException("dd 无法为新的EGL分配资源或者该窗口已经被关联");
                    }
                }
            }
        }
        mEglInfo.setEglSurface(mEglSurface);
    }

    /**
     * 切换上下文
     */
    public static void makeEglCurrent(@NonNull EglInfo mEglInfo) {
        //8. 绑定EglContext和Surface到显示设备中
        if (!EGL14.eglMakeCurrent(mEglInfo.getEglDisplay(),
                mEglInfo.getEglSurface(), mEglInfo.getEglSurface(), mEglInfo.getEglContext())) {
            throw new RuntimeException("eglMakeCurrent fail");
        }
    }

    //9. 刷新数据(前后缓冲交换)，显示渲染场景
    public static boolean swapBuffers(@NonNull EglInfo mEglInfo) {
        return EGL14.eglSwapBuffers(mEglInfo.getEglDisplay(), mEglInfo.getEglSurface());
    }

    /**
     * 销毁EglInfo里的对象
     */
    public static void destroyEgl(@NonNull EglInfo mEglInfo) {

        EGLSurface mEglSurface = mEglInfo.getEglSurface();
        EGLDisplay mEglDisplay = mEglInfo.getEglDisplay();
        EGLContext mEglContext = mEglInfo.getEglContext();

        if (mEglSurface != null && mEglSurface != EGL_NO_SURFACE) {
            EGL14.eglMakeCurrent(mEglDisplay, EGL_NO_SURFACE,
                    EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);

            EGL14.eglDestroySurface(mEglDisplay, mEglSurface);
            mEglInfo.setEglSurface(null);
        }

        if (mEglContext != null) {
            EGL14.eglDestroyContext(mEglDisplay, mEglContext);
            mEglInfo.setEglContext(null);
        }

        if (mEglDisplay != null) {
            EGL14.eglTerminate(mEglDisplay);
            mEglInfo.setEglDisplay(null);
        }
    }

    /**
     * 检查EglSurface相关设置
     */
    public static void checkEglSurface(@NonNull EglInfo mEglInfo) {
        if(mEglInfo.getUesPbufferSurface() == null) {
            throw new RuntimeException("UesPbufferSurface is null !! ");
        }

        if(!mEglInfo.getUesPbufferSurface() && mEglInfo.getSurface() == null) {
            //提示:检查一下EncodeRenderer从MediaCodec的Encode里获取的surface是否为空
            throw new RuntimeException("Use native windows but mSurface is null !! ");
        }
    }

    private static EGLConfig chooseConfig(@NonNull EglInfo mEglInfo, EGLConfig[] configs) {
        EGLDisplay display = mEglInfo.getEglDisplay();
        int mDefaultValue = mEglInfo.getDefaultValue();
        int mDepthSize = mEglInfo.getDepthSize();
        int mStencilSize = mEglInfo.getStencilSize();
        int mRedSize = mEglInfo.getRedSize();
        int mGreenSize = mEglInfo.getGreenSize();
        int mBlueSize = mEglInfo.getBlueSize();
        int mAlphaSize = mEglInfo.getAlphaSize();

        for (EGLConfig config : configs) {
            int d = findConfigAttrib(display, config, EGL14.EGL_DEPTH_SIZE, mDefaultValue);
            int s = findConfigAttrib(display, config, EGL14.EGL_STENCIL_SIZE, mDefaultValue);
            if ((d >= mDepthSize) && (s >= mStencilSize)) {
                int r = findConfigAttrib(display, config, EGL14.EGL_RED_SIZE, mDefaultValue);
                int g = findConfigAttrib(display, config, EGL14.EGL_GREEN_SIZE, mDefaultValue);
                int b = findConfigAttrib(display, config, EGL14.EGL_BLUE_SIZE, mDefaultValue);
                int a = findConfigAttrib(display, config, EGL14.EGL_ALPHA_SIZE, mDefaultValue);
                if ((r == mRedSize) && (g == mGreenSize) && (b == mBlueSize) && (a == mAlphaSize)) {
                    return config;
                }
            }
        }
        return null;
    }

    private static int findConfigAttrib(EGLDisplay display,
                                 EGLConfig config, int attribute, int defaultValue) {
        int[] value = new int[1];
        if (EGL14.eglGetConfigAttrib(display, config, attribute, value,0)) {
            return value[0];
        }
        return defaultValue;
    }
}

