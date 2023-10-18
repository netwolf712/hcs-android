package com.hcs.android.call.api;

/*
CallEasier.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import com.hcs.android.call.BandwidthManager;
import com.hcs.android.call.LinphoneService;
import com.hcs.android.call.util.LinphoneUtils;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallLog;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.MediaEncryption;
import org.linphone.core.tools.Log;

/** Handle call updating, reinvites. */
public class CallEasier {

    private static CallEasier sInstance;

    public static synchronized CallEasier getInstance() {
        if (sInstance == null) sInstance = new CallEasier();
        return sInstance;
    }

    private CallEasier() {}

    private BandwidthManager getBandwidthManager() {
        return BandwidthManager.getInstance();
    }

    public Call inviteAddress(Address lAddress, boolean forceZRTP) {
        boolean isLowBandwidthConnection =
                !LinphoneUtils.isHighBandwidthConnection(
                        LinphoneService.instance().getApplicationContext());

        return inviteAddress(lAddress, false, isLowBandwidthConnection, forceZRTP);
    }

    public Call inviteAddress(Address lAddress) {
        return inviteAddress(lAddress, false);
    }

    public Call inviteAddress(
            Address lAddress, boolean videoEnabled, boolean lowBandwidth, boolean forceZRTP) {
        Core lc = LinphoneManager.getLc();

        CallParams params = lc.createCallParams(null);
        getBandwidthManager().updateWithProfileSettings(params);

        if (videoEnabled && params.isVideoEnabled()) {
            params.setVideoEnabled(true);
        } else {
            params.setVideoEnabled(false);
        }

        if (lowBandwidth) {
            params.setLowBandwidthEnabled(true);
            Log.d("Low bandwidth enabled in call params");
        }

        if (forceZRTP) {
            params.setMediaEncryption(MediaEncryption.ZRTP);
        }

        String recordFile =
                LinphoneUtils.getCallRecordingFilename(
                        LinphoneManager.getInstance().getContext(), lAddress);
        params.setRecordFile(recordFile);

        return lc.inviteAddressWithParams(lAddress, params);
    }

    public Call inviteAddress(Address lAddress, boolean videoEnabled, boolean lowBandwidth) {
        return inviteAddress(lAddress, videoEnabled, lowBandwidth, false);
    }

    /**
     * 将语音通话切换为视频通话
     * 如果之前已经是视频通话或带宽过低则返回失败
     * 否则更新呼叫
     * @return
     */
    public boolean reinviteWithVideo() {
        Core lc = LinphoneManager.getLc();
        Call lCall = lc.getCurrentCall();
        if (lCall == null) {
            Log.e("Trying to reinviteWithVideo while not in call: doing nothing");
            return false;
        }
        CallParams params = lc.createCallParams(lCall);

        if (params.isVideoEnabled()) return false;

        // Check if video possible regarding bandwidth limitations
        getBandwidthManager().updateWithProfileSettings(params);

        // Abort if not enough bandwidth...
        if (!params.isVideoEnabled()) {
            return false;
        }

        // Not yet in video call: try to re-invite with video
        lCall.update(params);
        return true;
    }

    /**
     * 关闭视频通话，切换回语音通话
     */
    public boolean reinviteWithoutVideo() {
        Core lc = LinphoneManager.getLc();
        Call lCall = lc.getCurrentCall();
        if (lCall == null) {
            Log.e("Trying to reinviteWithVideo while not in call: doing nothing");
            return false;
        }
        CallParams params = lc.createCallParams(lCall);

        if (!params.isVideoEnabled()) return false;
        params.setVideoEnabled(false);

        // Not yet in video call: try to re-invite with video
        lCall.update(params);
        return true;
    }
    /**
     * Change the preferred video size used by linphone core. (impact landscape/portrait buffer).
     * Update current call, without reinvite. The camera will be restarted when mediastreamer chain
     * is recreated and setParameters is called.
     */
    public void updateCall() {
        Core lc = LinphoneManager.getLc();
        Call lCall = lc.getCurrentCall();
        if (lCall == null) {
            Log.e("Trying to updateCall while not in call: doing nothing");
            return;
        }
        CallParams params = lc.createCallParams(lCall);
        getBandwidthManager().updateWithProfileSettings(params);
        lCall.update(null);
    }

    /**
     * 获取呼叫日志
     */
    public CallLog[] getCallLogs(){
        Core lc = LinphoneManager.getLc();
        if(lc != null){
            return lc.getCallLogs();
        }
        return null;
    }
}
