package com.hcs.calldemo.factory;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.hcs.calldemo.viewmodel.CallLogViewModel;
import com.hcs.calldemo.viewmodel.ChatViewModel;
import com.hcs.calldemo.viewmodel.FriendViewModel;
import com.hcs.calldemo.viewmodel.MenuViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;
    private final Application mApplication;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application);
                }
            }
        }
        return INSTANCE;
    }
    private ViewModelFactory(Application application) {
        this.mApplication = application;
    }
    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FriendViewModel.class)) {
            return (T) new FriendViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(MenuViewModel.class)) {
            return (T) new MenuViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(CallLogViewModel.class)) {
            return (T) new CallLogViewModel(mApplication, new CommonViewModel(mApplication));
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
