package com.example.networkclient.utils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.networkclient.R;
import com.example.networkclient.base.BaseActivity;


/**
 * Created by pengdongyuan491 on 16/8/13.
 */
public class ToolBarUtil {
    public static void initToolbar(Toolbar toolbar, AppCompatActivity activity){
        if (toolbar == null || activity == null)
            return;
        if (activity instanceof BaseActivity){
            toolbar.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        }else {
//            toolbar.setBackgroundColor(activity.getResources().getColor(R.color.toolbar_bg_color));
        }
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(activity.getResources().getColor(R.color.white));
        toolbar.collapseActionView();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null){
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        }
    }
}
