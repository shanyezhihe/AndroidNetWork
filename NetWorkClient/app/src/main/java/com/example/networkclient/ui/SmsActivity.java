package com.example.networkclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.example.networkclient.R;
import com.example.networkclient.base.BaseActivity;
import com.example.networkclient.mvp.presenters.impl.SmsPresenter;
import com.example.networkclient.mvp.views.impl.SmsView;
import com.example.networkclient.utils.ToolBarUtil;

import butterknife.BindView;

/**
 * Created by pengdongyuan491 on 16/9/2.
 */
public class SmsActivity  extends BaseActivity implements SmsView{

    @BindView(R.id.toolbar)
    Toolbar toolBar;

    @BindView(R.id.start_netclient)
    Button btn;
    @BindView(R.id.tv_data)
    TextView tv_data;
    private SmsPresenter smsPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActivityInAnim();
        btn.setVisibility(android.view.View.GONE);
        smsPresenter=new SmsPresenter(this);
        smsPresenter.attachView(this);
        smsPresenter.parseIntent(getIntent());
        smsPresenter.onCreate(savedInstanceState);
        btn.setOnClickListener(smsPresenter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        smsPresenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        smsPresenter.onDestroy();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_request;
    }

    @Override
    public void initToolbar() {
        ToolBarUtil.initToolbar(toolBar,this);
    }

    @Override
    public void setToolBarTitle(String title) {
        toolBar.setTitle(title);
    }

    @Override
    public void upDateTextViewText(String text) {
        tv_data.setText(text);
    }
}
