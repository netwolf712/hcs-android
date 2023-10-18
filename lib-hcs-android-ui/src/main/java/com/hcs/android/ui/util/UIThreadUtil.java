package com.hcs.android.ui.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.log.KLog;

/**
 * UI线程助手
 * 界面刷新必须在界面线程里操作，但有时候多线程对代码逻辑实现又会简单很多
 * 那怎么办呢？更多都是采用handler机制
 * 所以这里其实就是对handler机制的封装
 * 将任务post到UI线程去
 */
public final class UIThreadUtil {

	private UIThreadUtil() {

	}

	private static final Handler mUIHandler = new Handler(Looper.getMainLooper());
	private static final Thread mUiThread = mUIHandler.getLooper().getThread();


	/**
	 * 在UI线程执行任务
	 * @param task 任务
	 */
	public static final void runOnUiThread(@NonNull final Runnable task) {
		if (Thread.currentThread() != mUiThread) {
			mUIHandler.post(task);
		} else {
			try {
				task.run();
			} catch (final Exception e) {
				KLog.e("runOnUIThread failed",e);
			}
		}
	}

	public static final void runOnUiThread(@NonNull final Runnable task, final long duration) {
		if ((duration > 0) || Thread.currentThread() != mUiThread) {
			mUIHandler.postDelayed(task, duration);
		} else {
			try {
				task.run();
			} catch (final Exception e) {
				KLog.e("runOnUIThread failed",e);
			}
		}
	}

	public static final void removeFromUiThread(@NonNull final Runnable task) {
		mUIHandler.removeCallbacks(task);
	}
}
