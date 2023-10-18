package com.hcs.android.ui.player;


import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.Surface;

public class EglInfo {
    // 一个标签
    private String tag;

    // 使用FBO时请将此设为 true 避免window surface的干扰
    private Boolean uesPbufferSurface;

    private EGLDisplay eglDisplay;

    // 用于共享的上下文 注意不会直接使用 而是基于此 生成自己的上下文
    // 别 和下面的的 上下文 搞反了 ....
    private EGLContext sharedEglContext;

    // 实际使用的上下文
    private EGLContext eglContext;

    private EGLSurface eglSurface;

    // 当uesPbufferSurface 为 false时 该surface必须非空 有效 否则我throw异常
    private Surface surface;

    private EGLConfig eglConfig;

    private Integer numConfigs;

    private int[] listAttrib;
    private int[] listConfigs;

    /**
     * EGL版本
     */

    private int eglContextClientVersion = 2;

    private final Integer redSize = 8;
    private final Integer greenSize = 8;


    private final Integer blueSize = 8;


    private final Integer alphaSize = 8;


    private final Integer depthSize = 8;


    private final Integer stencilSize = 8;


    private final Integer renderType = 4;


    private final Integer defaultValue = 0;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getUesPbufferSurface() {
        return uesPbufferSurface;
    }

    public void setUesPbufferSurface(Boolean uesPbufferSurface) {
        this.uesPbufferSurface = uesPbufferSurface;
    }

    public EGLDisplay getEglDisplay() {
        return eglDisplay;
    }

    public void setEglDisplay(EGLDisplay eglDisplay) {
        this.eglDisplay = eglDisplay;
    }

    public EGLContext getSharedEglContext() {
        return sharedEglContext;
    }

    public void setSharedEglContext(EGLContext sharedEglContext) {
        this.sharedEglContext = sharedEglContext;
    }

    public EGLContext getEglContext() {
        return eglContext;
    }

    public void setEglContext(EGLContext eglContext) {
        this.eglContext = eglContext;
    }

    public EGLSurface getEglSurface() {
        return eglSurface;
    }

    public void setEglSurface(EGLSurface eglSurface) {
        this.eglSurface = eglSurface;
    }

    public Surface getSurface() {
        return surface;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public EGLConfig getEglConfig() {
        return eglConfig;
    }

    public void setEglConfig(EGLConfig eglConfig) {
        this.eglConfig = eglConfig;
    }

    public Integer getNumConfigs() {
        return numConfigs;
    }

    public void setNumConfigs(Integer numConfigs) {
        this.numConfigs = numConfigs;
    }

    public int[] getListAttrib() {
        return listAttrib;
    }

    public void setListAttrib(int[] listAttrib) {
        this.listAttrib = listAttrib;
    }

    public int[] getListConfigs() {
        return listConfigs;
    }

    public void setListConfigs(int[] listConfigs) {
        this.listConfigs = listConfigs;
    }

    public int getEglContextClientVersion() {
        return eglContextClientVersion;
    }

    public void setEglContextClientVersion(int eglContextClientVersion) {
        this.eglContextClientVersion = eglContextClientVersion;
    }

    public Integer getRedSize() {
        return redSize;
    }

    public Integer getGreenSize() {
        return greenSize;
    }

    public Integer getBlueSize() {
        return blueSize;
    }

    public Integer getAlphaSize() {
        return alphaSize;
    }

    public Integer getDepthSize() {
        return depthSize;
    }

    public Integer getStencilSize() {
        return stencilSize;
    }

    public Integer getRenderType() {
        return renderType;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }
}
