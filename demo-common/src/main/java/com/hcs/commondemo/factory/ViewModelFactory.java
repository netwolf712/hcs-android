package com.hcs.commondemo.factory;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.hcs.commondemo.viewmodel.IPCameraViewModel;
import com.hcs.commondemo.viewmodel.MenuViewModel;
import com.hcs.commondemo.viewmodel.MulticastViewModel;
import com.hcs.commondemo.viewmodel.RecordLogViewModel;

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
        if (modelClass.isAssignableFrom(MenuViewModel.class)) {
            return (T) new MenuViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(RecordLogViewModel.class)) {
            return (T) new RecordLogViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(MulticastViewModel.class)) {
            return (T) new MulticastViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(IPCameraViewModel.class)) {
            return (T) new IPCameraViewModel(mApplication, new CommonViewModel(mApplication));
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
