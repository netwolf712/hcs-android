package com.hcs.android.onvif.onvifBean;

/**
 * Author ： BlackHao
 * Time : 2018/1/10 17:12
 * Description : onvif MediaProfile
 */

public class MediaProfile {
    //token
    private String token;
    //name
    private String name;

    private VideoEncoderConfiguration videoEncode;
    private VideoSourceConfiguration videSource;
    private AudioEncoderConfiguration audioEncode;
    private AudioSourceConfiguration audioSource;

    private PTZConfiguration ptzConfiguration;

    //RTSP 地址
    private String rtspUrl;

    public MediaProfile() {
        this.videoEncode = new VideoEncoderConfiguration();
        this.audioEncode = new AudioEncoderConfiguration();
        this.videSource = new VideoSourceConfiguration();
        this.audioSource = new AudioSourceConfiguration();
        this.ptzConfiguration = new PTZConfiguration();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public VideoEncoderConfiguration getVideoEncode() {
        return videoEncode;
    }

    public void setVideoEncode(VideoEncoderConfiguration videoEncode) {
        this.videoEncode = videoEncode;
    }

    public VideoSourceConfiguration getVideSource() {
        return videSource;
    }

    public void setVideSource(VideoSourceConfiguration videSource) {
        this.videSource = videSource;
    }

    public AudioEncoderConfiguration getAudioEncode() {
        return audioEncode;
    }

    public void setAudioEncode(AudioEncoderConfiguration audioEncode) {
        this.audioEncode = audioEncode;
    }

    public AudioSourceConfiguration getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(AudioSourceConfiguration audioSource) {
        this.audioSource = audioSource;
    }

    public PTZConfiguration getPtzConfiguration() {
        return ptzConfiguration;
    }

    public void setPtzConfiguration(PTZConfiguration ptzConfiguration) {
        this.ptzConfiguration = ptzConfiguration;
    }

    @Override
    public String toString() {
        return "MediaProfile{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", videoEncode=" + videoEncode +
                ", audioEncode=" + audioEncode +
                ", rtspUrl='" + rtspUrl + '\'' +
                '}';
    }

    //视频编码信息
    public class VideoEncoderConfiguration {
        private String token;
        //编码格式
        private String encoding;
        //分辨率
        private int width;
        private int height;
        //帧率
        private int frameRate;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getFrameRate() {
            return frameRate;
        }

        public void setFrameRate(int frameRate) {
            this.frameRate = frameRate;
        }

        @Override
        public String toString() {
            return "VideoEncoderConfiguration{" +
                    "token='" + token + '\'' +
                    ", encoding='" + encoding + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", frameRate=" + frameRate +
                    '}';
        }
    }

    public class VideoSourceConfiguration{
        //<tt:Name>VideoS_000</tt:Name>
        //          <tt:UseCount>3</tt:UseCount>
        //          <tt:SourceToken>000</tt:SourceToken>
        //          <tt:Bounds height="1536" width="2048" y="0" x="0"/>

        private String videoSourceConfigurationToken;
        private String name;
        private String userCount;
        private String videoSourceToken;
        //分辨率
        private int width;
        private int height;

        public String getVideoSourceConfigurationToken() {
            return videoSourceConfigurationToken;
        }

        public void setVideoSourceConfigurationToken(String videoSourceConfigurationToken) {
            this.videoSourceConfigurationToken = videoSourceConfigurationToken;
        }

        public String getVideoSourceToken() {
            return videoSourceToken;
        }

        public void setVideoSourceToken(String videoSourceToken) {
            this.videoSourceToken = videoSourceToken;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserCount() {
            return userCount;
        }

        public void setUserCount(String userCount) {
            this.userCount = userCount;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    //音频编码信息
    public class AudioEncoderConfiguration {
        private String tokenName;
        //编码格式
        private String encoding;
        private String audioSourceToken;
        //采样率
        private int sampleRate;
        //比特率
        private int bitrate;

        public String getTokenName() {
            return tokenName;
        }

        public void setTokenName(String token) {
            this.tokenName = token;
        }


        public String getAudioSourceToken() {
            return audioSourceToken;
        }

        public void setAudioSourceToken(String audioSourceToken) {
            this.audioSourceToken = audioSourceToken;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public int getSampleRate() {
            return sampleRate;
        }

        public void setSampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
        }

        public int getBitrate() {
            return bitrate;
        }

        public void setBitrate(int bitrate) {
            this.bitrate = bitrate;
        }

        @Override
        public String toString() {
            return "AudioEncoderConfiguration{" +
                    "token='" + token + '\'' +
                    ", encoding='" + encoding + '\'' +
                    ", sampleRate=" + sampleRate +
                    ", bitrate=" + bitrate +
                    '}';
        }
    }

    public class AudioSourceConfiguration{
        //<tt:Name>VideoS_000</tt:Name>
        //          <tt:UseCount>3</tt:UseCount>
        //          <tt:SourceToken>000</tt:SourceToken>
        //          <tt:Bounds height="1536" width="2048" y="0" x="0"/>

        private String audioSourceConfigurationToken;
        private String name;
        private String userCount;
        private String videoSourceToken;
        //分辨率
        private int width;
        private int height;

        public String getAudioSourceConfigurationToken() {
            return audioSourceConfigurationToken;
        }

        public void setAudioSourceConfigurationToken(String videoSourceConfigurationToken) {
            this.audioSourceConfigurationToken = videoSourceConfigurationToken;
        }

        public String getAudioSourceToken() {
            return videoSourceToken;
        }

        public void setAudioSourceToken(String videoSourceToken) {
            this.videoSourceToken = videoSourceToken;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserCount() {
            return userCount;
        }

        public void setUserCount(String userCount) {
            this.userCount = userCount;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }


    //PTZ 信息
    public class PTZConfiguration {
        //PTZ Token
        private String token;
        //Node Token
        private String nodeToken;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNodeToken() {
            return nodeToken;
        }

        public void setNodeToken(String nodeToken) {
            this.nodeToken = nodeToken;
        }

        @Override
        public String toString() {
            return "PTZConfiguration{" +
                    "token='" + token + '\'' +
                    ", nodeToken='" + nodeToken + '\'' +
                    '}';
        }
    }

}
