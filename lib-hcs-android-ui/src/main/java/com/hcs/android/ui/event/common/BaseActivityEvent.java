package com.hcs.android.ui.event.common;

import com.hcs.android.ui.event.BaseEvent;

/**
 * Description: <BaseActivityEvent><br>
 * Author:      mxdl<br>
 * Date:        2018/4/4<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class BaseActivityEvent<T> extends BaseEvent<T> {
    public BaseActivityEvent(int code) {
        super(code);
    }
}
