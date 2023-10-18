package com.hcs.android.business.request.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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
        if (modelClass.isAssignableFrom(PlaceViewModel.class)) {
            return (T) new PlaceViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(CallLogViewModel.class)) {
            return (T) new CallLogViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(HandoverViewModel.class)) {
            return (T) new HandoverViewModel(mApplication, new CommonViewModel(mApplication));
        }else if (modelClass.isAssignableFrom(AttachmentViewModel.class)) {
            return (T) new AttachmentViewModel(mApplication, new CommonViewModel(mApplication));
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
