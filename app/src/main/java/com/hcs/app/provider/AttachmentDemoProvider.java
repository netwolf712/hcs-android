package com.hcs.app.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.app.fragment.AttachmentDemoFragment;


@Route(path = "/menu/attachment",name = "附件管理")
public class AttachmentDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return AttachmentDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
