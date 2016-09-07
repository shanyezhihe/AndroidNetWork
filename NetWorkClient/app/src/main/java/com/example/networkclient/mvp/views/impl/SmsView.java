package com.example.networkclient.mvp.views.impl;

import com.example.networkclient.mvp.views.View;

/**
 * Created by pengdongyuan491 on 16/9/2.
 */
public interface SmsView extends View {

    void initToolbar();
    void setToolBarTitle(String title);
    void upDateTextViewText(String text);
}
