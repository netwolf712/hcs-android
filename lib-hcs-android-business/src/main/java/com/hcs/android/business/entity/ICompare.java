package com.hcs.android.business.entity;

import androidx.annotation.NonNull;

public interface ICompare <T>{
    boolean handleCompare(@NonNull T src, @NonNull T dst);
}
