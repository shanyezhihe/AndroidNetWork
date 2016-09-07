package com.example.networkclient.mvp.presenters.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.networkclient.R;
import com.example.networkclient.mvp.presenters.Presenter;
import com.example.networkclient.mvp.views.View;
import com.example.networkclient.mvp.views.impl.HomeView;
import com.example.networkclient.ui.RequestActivity;
import com.example.networkclient.ui.SmsActivity;

import java.util.List;

/**
 * Created by pengdongyuan491 on 16/8/13.
 */
public class HomePresenter implements Presenter , android.view.View.OnClickListener{
    public static final String BTNID="btn_id";
    private HomeView view;
    private List<String> drawList;
    private Context context;

    public HomePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        view.initToolBar();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void attachView(View v) {
        this.view = (HomeView) v;
    }

    private  void openRequestActivity(int btnId){
        Intent intent=new Intent(context, RequestActivity.class);
        intent.putExtra(BTNID,btnId);
        context.startActivity(intent);
    }
    private void  openSmsActicity(int btnId){
        Intent intent=new Intent(context, SmsActivity.class);
        intent.putExtra(BTNID,btnId);
        context.startActivity(intent);
    }

    @Override
    public void onClick(android.view.View view) {
        if (view.getId()== R.id.btn_netserver||view.getId()==R.id.btn_localserver){
        openRequestActivity(view.getId());
        }else {
            openSmsActicity(view.getId());
        }

    }
}
