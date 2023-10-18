package com.hcs.android.call.util;

/*
LinphoneUtils.java
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.hcs.android.call.api.LinphoneManager;
import com.hcs.android.call.api.LinphonePreferences;
import com.hcs.android.call.R;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Call.State;
import org.linphone.core.ChatRoom;
import org.linphone.core.ChatRoomCapabilities;
import org.linphone.core.Core;
import org.linphone.core.Factory;
import org.linphone.core.ProxyConfig;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** Helpers. */
public final class LinphoneUtils {
    private static Context sContext = null;
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private LinphoneUtils() {}

    public static void dispatchOnUIThread(Runnable r) {
        sHandler.post(r);
    }

    // private static final String sipAddressRegExp =
    // "^(sip:)?(\\+)?[a-z0-9]+([_\\.-][a-z0-9]+)*@([a-z0-9]+([\\.-][a-z0-9]+)*)+\\.[a-z]{2,}(:[0-9]{2,5})?$";
    // private static final String strictSipAddressRegExp =
    // "^sip:(\\+)?[a-z0-9]+([_\\.-][a-z0-9]+)*@([a-z0-9]+([\\.-][a-z0-9]+)*)+\\.[a-z]{2,}$";

    private static boolean isSipAddress(String numberOrAddress) {
        Factory.instance().createAddress(numberOrAddress);
        return true;
    }

    public static boolean isNumberAddress(String numberOrAddress) {
        ProxyConfig proxy = LinphoneManager.getLc().createProxyConfig();
        return proxy.normalizePhoneNumber(numberOrAddress) != null;
    }

    public static boolean isStrictSipAddress(String numberOrAddress) {
        return isSipAddress(numberOrAddress) && numberOrAddress.startsWith("sip:");
    }

    public static String getDisplayableAddress(Address addr) {
        return "sip:" + addr.getUsername() + "@" + addr.getDomain();
    }

    public static String getAddressDisplayName(String uri) {
        Address lAddress;
        lAddress = Factory.instance().createAddress(uri);
        return getAddressDisplayName(lAddress);
    }

    public static String getAddressDisplayName(Address address) {
        if (address == null) return null;

        String displayName = address.getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            displayName = address.getUsername();
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = address.asStringUriOnly();
        }
        return displayName;
    }

    public static String getUsernameFromAddress(String address) {
        if (address.contains("sip:")) address = address.replace("sip:", "");

        if (address.contains("@")) address = address.split("@")[0];

        return address;
    }

    public static boolean onKeyBackGoHome(Activity activity, int keyCode, KeyEvent event) {
        if (!(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            return false; // continue
        }

        activity.startActivity(
                new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        return true;
    }

    public static String timestampToHumanDate(Context context, long timestamp, int format) {
        return timestampToHumanDate(context, timestamp, context.getString(format));
    }

    public static String timestampToHumanDate(Context context, long timestamp, String format) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp * 1000); // Core returns timestamps in seconds...

            SimpleDateFormat dateFormat;
            if (isToday(cal)) {
                dateFormat =
                        new SimpleDateFormat(
                                context.getResources().getString(R.string.today_date_format),
                                Locale.getDefault());
            } else {
                dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            }

            return dateFormat.format(cal.getTime());
        } catch (NumberFormatException nfe) {
            return String.valueOf(timestamp);
        }
    }

    private static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return false;
        }

        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static List<Call> getCallsInState(Core lc, Collection<State> states) {
        List<Call> foundCalls = new ArrayList<>();
        for (Call call : lc.getCalls()) {
            if (states.contains(call.getState())) {
                foundCalls.add(call);
            }
        }
        return foundCalls;
    }

    private static boolean isCallRunning(Call call) {
        if (call == null) {
            return false;
        }

        State state = call.getState();

        return state == State.Connected
                || state == State.Updating
                || state == State.UpdatedByRemote
                || state == State.StreamsRunning
                || state == State.Resuming;
    }

    public static boolean isCallEstablished(Call call) {
        if (call == null) {
            return false;
        }

        State state = call.getState();

        return isCallRunning(call)
                || state == State.Paused
                || state == State.PausedByRemote
                || state == State.Pausing;
    }

    public static boolean isHighBandwidthConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null
                && info.isConnected()
                && isConnectionFast(info.getType(), info.getSubtype()));
    }

    private static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false;
            }
        }
        // in doubt, assume connection is good.
        return true;
    }

    public static void reloadVideoDevices() {
        Core core = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (core == null) return;

        Log.i("[Utils] Reloading camera");
        core.reloadVideoDevices();

        boolean useFrontCam = LinphonePreferences.instance().useFrontCam();
        int camId = 0;
        AndroidCameraConfiguration.AndroidCamera[] cameras =
                AndroidCameraConfiguration.retrieveCameras();
        for (AndroidCameraConfiguration.AndroidCamera androidCamera : cameras) {
            if (androidCamera.frontFacing == useFrontCam) {
                camId = androidCamera.id;
                break;
            }
        }
        String[] devices = core.getVideoDevicesList();
        if (camId >= devices.length) {
            camId = 0;
        }
        String newDevice = devices[camId];
        core.setVideoDevice(newDevice);
    }

    public static String getDisplayableUsernameFromAddress(String sipAddress) {
        String username = sipAddress;
        Core lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc == null) return username;

        if (username.startsWith("sip:")) {
            username = username.substring(4);
        }

        if (username.contains("@")) {
            return username.split("@")[0];
        }
        return username;
    }

    public static String getFullAddressFromUsername(String username) {
        String sipAddress = username;
        Core lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc == null || username == null) return sipAddress;

        if (!sipAddress.startsWith("sip:")) {
            sipAddress = "sip:" + sipAddress;
        }

        if (!sipAddress.contains("@")) {
            ProxyConfig lpc = lc.getDefaultProxyConfig();
            if (lpc != null) {
                sipAddress = sipAddress + "@" + lpc.getDomain();
            } else {
            }
        }
        return sipAddress;
    }

    public static void displayError(boolean isOk, TextView error, String errorText) {
        if (isOk) {
            error.setVisibility(View.INVISIBLE);
            error.setText("");
        } else {
            error.setVisibility(View.VISIBLE);
            error.setText(errorText);
        }
    }

    public static Spanned getTextWithHttpLinks(String text) {
        if (text == null) return null;

        if (text.contains("<")) {
            text = text.replace("<", "&lt;");
        }
        if (text.contains(">")) {
            text = text.replace(">", "&gt;");
        }
        if (text.contains("\n")) {
            text = text.replace("\n", "<br>");
        }
        if (text.contains("http://")) {
            int indexHttp = text.indexOf("http://");
            int indexFinHttp =
                    text.indexOf(" ", indexHttp) == -1
                            ? text.length()
                            : text.indexOf(" ", indexHttp);
            String link = text.substring(indexHttp, indexFinHttp);
            String linkWithoutScheme = link.replace("http://", "");
            text =
                    text.replaceFirst(
                            Pattern.quote(link),
                            "<a href=\"" + link + "\">" + linkWithoutScheme + "</a>");
        }
        if (text.contains("https://")) {
            int indexHttp = text.indexOf("https://");
            int indexFinHttp =
                    text.indexOf(" ", indexHttp) == -1
                            ? text.length()
                            : text.indexOf(" ", indexHttp);
            String link = text.substring(indexHttp, indexFinHttp);
            String linkWithoutScheme = link.replace("https://", "");
            text =
                    text.replaceFirst(
                            Pattern.quote(link),
                            "<a href=\"" + link + "\">" + linkWithoutScheme + "</a>");
        }

        return Html.fromHtml(text);
    }

    private static Context getContext() {
        if (sContext == null && LinphoneManager.isInstanced())
            sContext = LinphoneManager.getInstance().getContext();
        return sContext;
    }

    public static ArrayList<ChatRoom> removeEmptyOneToOneChatRooms(ChatRoom[] rooms) {
        ArrayList<ChatRoom> newRooms = new ArrayList<>();
        for (ChatRoom room : rooms) {
            if (room.hasCapability(ChatRoomCapabilities.OneToOne.toInt())
                    && room.getHistorySize() == 0) {
                // Hide 1-1 chat rooms without messages
            } else {
                newRooms.add(room);
            }
        }
        return newRooms;
    }

    public static String getRecordingsDirectory(Context mContext) {
        String recordingsDir =
                Environment.getExternalStorageDirectory()
                        + "/"
                        + mContext.getString(
                        mContext.getResources()
                                .getIdentifier(
                                        "app_name", "string", mContext.getPackageName()))
                        + "/recordings";
        File file = new File(recordingsDir);
        if (!file.isDirectory() || !file.exists()) {
            Log.w("Directory " + file + " doesn't seem to exists yet, let's create it");
            file.mkdirs();
        }
        return recordingsDir;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCallRecordingFilename(Context context, Address address) {
        String fileName = getRecordingsDirectory(context) + "/";

        String name =
                address.getDisplayName() == null ? address.getUsername() : address.getDisplayName();
        fileName += name + "_";

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        fileName += format.format(new Date()) + ".mkv";

        return fileName;
    }

    /**
     * 将呼叫方向转换成字符
     */
    public static String convertCallDirToString(Context context, Call.Dir dir){
        if(dir == Call.Dir.Incoming){
            return context.getString(R.string.call_incoming);
        }else{
            return context.getString(R.string.call_outgoing);
        }
    }

    /**
     * 将呼叫状态转换为字符串
     */
    public static String convertCallStatusToString(Context context,Call.Status status){
        switch (status){
            case Success:
                return context.getString(R.string.call_success);
            case Missed:
                return context.getString(R.string.call_missed);
            case Aborted:
                return context.getString(R.string.call_aborted);
            case Declined:
                return context.getString(R.string.call_declined);
            case EarlyAborted:
                return context.getString(R.string.call_early_aborted);
            case AcceptedElsewhere:
                return context.getString(R.string.call_accept_else_where);
            case DeclinedElsewhere:
                return context.getString(R.string.call_declined_else_where);
            default:
                return "";
        }
    }
}
