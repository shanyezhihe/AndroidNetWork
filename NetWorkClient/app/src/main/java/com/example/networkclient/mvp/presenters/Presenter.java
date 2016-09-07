package com.example.networkclient.mvp.presenters;

import android.os.Bundle;

import com.example.networkclient.mvp.views.View;


/**
 * Created by pengdongyuan on 16/7/27.
 */
public interface Presenter {
    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onStart();

    void onPause();

    void onStop();

    void onDestroy();

    void attachView(View v);
}
