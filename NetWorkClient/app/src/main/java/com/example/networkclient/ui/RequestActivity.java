package com.example.networkclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.example.networkclient.R;
import com.example.networkclient.base.BaseActivity;
import com.example.networkclient.mvp.presenters.impl.RequestPresenter;
import com.example.networkclient.mvp.views.impl.RequestView;
import com.example.networkclient.utils.ToolBarUtil;

import butterknife.BindView;

/**
 * Created by pengdongyuan491 on 16/8/30.
 */
public class RequestActivity extends BaseActivity implements RequestView{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.start_netclient)
    Button btnNetClient;
    @BindView(R.id.tv_data)
    TextView tvData;

    private RequestPresenter requestPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPresenter=new RequestPresenter(this);
        requestPresenter.attachView(this);
        requestPresenter.parseIntent(getIntent());
        requestPresenter.onCreate(savedInstanceState);
        btnNetClient.setOnClickListener(requestPresenter);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_request;
    }

    @Override
    public void initToolbar() {
        ToolBarUtil.initToolbar(toolbar,this);
    }

    @Override
    public void setToolBarTitle(String title) {
        if (toolbar!=null){
            toolbar.setTitle(title);
        }
    }

    @Override
    public void upDateTextViewText(String text) {
        if (text!=null) {
            tvData.setText(text);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestPresenter.onDestroy();
    }
}
