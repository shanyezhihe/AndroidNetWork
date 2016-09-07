package com.example.networkclient.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.example.networkclient.R;
import com.example.networkclient.base.BaseActivity;
import com.example.networkclient.mvp.presenters.impl.HomePresenter;
import com.example.networkclient.mvp.views.impl.HomeView;
import com.example.networkclient.utils.ToolBarUtil;

import butterknife.BindView;

public class HomeActivity extends BaseActivity implements HomeView{

    private HomePresenter homePresenter;
    @BindView(R.id.btn_netserver)
    Button btnNetServer;
    @BindView(R.id.btn_localserver)
    Button btnLocalServer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_broad_sms)
    Button btn_broad_sms;
    @BindView(R.id.btn_db_sms)
    Button btn_db_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActivityInAnim();
        homePresenter=new HomePresenter(this);
        homePresenter.attachView(this);
        homePresenter.onCreate(savedInstanceState);
        btnLocalServer.setOnClickListener(homePresenter);
        btnNetServer.setOnClickListener(homePresenter);
        btn_broad_sms.setOnClickListener(homePresenter);
        btn_db_sms.setOnClickListener(homePresenter);
    }

//    @OnClick(R.id.btn_localserver)
//    public void localServerClick(android.view.View view){
//        Toast.makeText(HomeActivity.this, "toast", Toast.LENGTH_SHORT).show();
//        homePresenter.openRequestActivity(view.getId());
//    }
//
//    @OnClick(R.id.btn_localserver)
//    public void netServerClick(android.view.View view){
//        homePresenter.openRequestActivity(view.getId());
//    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_home;
    }


    @Override
    public void initToolBar() {
        ToolBarUtil.initToolbar(toolbar,this);
    }


}
