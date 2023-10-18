package com.hcs.android.ui.provider;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface IFragmetProvider extends IProvider {
    Fragment getFragment();
}
