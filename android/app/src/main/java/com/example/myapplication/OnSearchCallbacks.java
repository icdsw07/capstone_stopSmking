package com.example.myapplication;

import android.support.annotation.NonNull;

import com.example.myapplication.Data.UserData;

public interface OnSearchCallbacks {
    void onSuccess(@NonNull UserData value);

    void onError(@NonNull Throwable throwable);
}
