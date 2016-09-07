package com.example.networkclient.mvp.presenters.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.networkclient.R;
import com.example.networkclient.mvp.presenters.Presenter;
import com.example.networkclient.mvp.views.View;
import com.example.networkclient.mvp.views.impl.SmsView;
import com.example.networkclient.utils.DynamicCodeUtil;

/**
 * Created by pengdongyuan491 on 16/9/2.
 */
public class SmsPresenter implements Presenter, android.view.View.OnClickListener {

    private Context context;
    private SmsView view;
    private int btnId;

    public SmsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        view.initToolbar();
        switch (btnId) {
            case R.id.btn_broad_sms:
                view.setToolBarTitle("监听系统广播获取验证码测试");
                DynamicCodeUtil.getDynamicCodeByBroadCast(context, new DynamicCodeUtil.DynamicCodeReceiveListerner() {
                    @Override
                    public void onReceiveCode(String code) {
                        view.upDateTextViewText(code);
                    }
                });
                break;
            case R.id.btn_db_sms:
                view.setToolBarTitle("监听数据库获取验证码测试");
                DynamicCodeUtil.getDynamicCodeByContentObserver(context, new DynamicCodeUtil.DynamicCodeReceiveListerner() {
                    @Override
                    public void onReceiveCode(String code) {
                        view.upDateTextViewText(code);
                    }
                });
                break;
            default:
                break;
        }
    }

    public void parseIntent(Intent intent) {
        btnId = intent.getIntExtra(HomePresenter.BTNID, 0);
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
        DynamicCodeUtil.unRegister(context);
    }

    @Override
    public void attachView(View v) {
        this.view = (SmsView) v;
    }

    @Override
    public void onClick(android.view.View view) {
    }


}
