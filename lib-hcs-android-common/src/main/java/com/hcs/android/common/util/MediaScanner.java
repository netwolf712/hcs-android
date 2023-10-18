package com.hcs.android.common.util;

/*
MediaScanner.java
Copyright (C) 2018  Belledonne Communications, Grenoble, France

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

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.hcs.android.common.util.log.KLog;

import java.io.File;

public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private final MediaScannerConnection mMediaConnection;
    private boolean mIsConnected;
    private File mFileWaitingForScan;
    private MediaScannerListener mListener;

    public MediaScanner(Context context) {
        mIsConnected = false;
        mMediaConnection = new MediaScannerConnection(context, this);
        mMediaConnection.connect();
        mFileWaitingForScan = null;
    }

    @Override
    public void onMediaScannerConnected() {
        mIsConnected = true;
        KLog.i("[MediaScanner] Connected");
        if (mFileWaitingForScan != null) {
            scanFile(mFileWaitingForScan, null);
            mFileWaitingForScan = null;
        }
    }

    public void scanFile(File file, MediaScannerListener listener) {
        scanFile(file, FileUtil.getMimeFromFile(file.getAbsolutePath()), listener);
    }

    private void scanFile(File file, String mime, MediaScannerListener listener) {
        mListener = listener;

        if (!mIsConnected) {
            KLog.w("[MediaScanner] Not connected yet...");
            mFileWaitingForScan = file;
            return;
        }

        if (mMediaConnection != null) {
            KLog.i("[MediaScanner] Scanning file " + file.getAbsolutePath() + " with MIME " + mime);
            mMediaConnection.scanFile(file.getAbsolutePath(), mime);
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        KLog.i("[MediaScanner] Scan completed : " + path + " => " + uri);
        if (mListener != null) {
            mListener.onMediaScanned(path, uri);
        }
    }

    public void destroy() {
        KLog.i("[MediaScanner] Disconnecting");
        mMediaConnection.disconnect();
        mIsConnected = false;
    }
}
