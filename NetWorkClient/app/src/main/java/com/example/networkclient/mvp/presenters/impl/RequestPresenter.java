package com.example.networkclient.mvp.presenters.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.networkclient.R;
import com.example.networkclient.constant.Constant;
import com.example.networkclient.mvp.presenters.Presenter;
import com.example.networkclient.mvp.views.View;
import com.example.networkclient.mvp.views.impl.RequestView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pengdongyuan491 on 16/8/30.
 */
public class RequestPresenter implements Presenter, android.view.View.OnClickListener {
    private HttpURLConnection httpURLConnection;
    private BufferedReader bufferedReader;
    private URL url;
    private InputStream inputStream;
    private Context context;
    private RequestView view;
    private StringBuilder stringBuilder;
    private String data = null;
    private NetWorkHandler netWorkHandler;
    private String currentVersion="1.5";
    public RequestPresenter(Context context) {
        this.context = context;
    }

    private int btnId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        netWorkHandler=new NetWorkHandler();
        view.initToolbar();
        switch (btnId) {
            case R.id.btn_localserver:
                view.setToolBarTitle("请求本地服务器测试");
                break;
            case R.id.btn_netserver:
                view.setToolBarTitle("请求网络服务器测试");
                break;
            default:
                break;
        }
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
        netWorkHandler=null;
    }

    @Override
    public void attachView(View v) {
        this.view = (RequestView) v;
    }

    public void parseIntent(Intent intent) {
        btnId = intent.getIntExtra(HomePresenter.BTNID, 0);
    }

    @Override
    public void onClick(android.view.View view) {
        switch (view.getId()) {
            case R.id.start_netclient:
                switch (btnId) {
                    case R.id.btn_localserver:
                        loadDataFromLocalServer();
                        break;
                    case R.id.btn_netserver:
                        loadDataFromRemoteServer();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    //加载远程服务器数据

    private void loadDataFromRemoteServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    url = new URL(Constant.REMOTEURL);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    stringBuilder = new StringBuilder();
                    String tempLine = null;
                    while ((tempLine = bufferedReader.readLine()) != null) {
                        stringBuilder.append(tempLine).append("\n");
                    }
                    data=stringBuilder.toString();
                    Log.e("data",data);
                    Message msg=new Message();
                    Bundle b=new Bundle();
                    b.putString("data",data);
                    msg.setData(b);
                    netWorkHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bufferedReader.close();
                        inputStream.close();
                    } catch (Exception e) {

                    }
                }
            }
        }).start();


    }

    //加载本地自己搭建的服务器数据
    private void loadDataFromLocalServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg=new Message();
                    Bundle b=new Bundle();
                    url = new URL(Constant.LOCALBASEURL+"?currentversion="+currentVersion);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    stringBuilder = new StringBuilder();
                    String tempLine = null;
                    while ((tempLine = bufferedReader.readLine()) != null) {
                        stringBuilder.append(tempLine).append("\n");
                    }
                    data=stringBuilder.toString();
                    Log.e("data",data);
                    b.putString("data",data);
                    msg.setData(b);
                    netWorkHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bufferedReader.close();
                        inputStream.close();
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    private class NetWorkHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            String data = b.getString("data");
            JSONObject jsonObject;
            try {
                jsonObject=new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
                view.upDateTextViewText(data);
                return;
            }
            StringBuilder stringBuilder=new StringBuilder();
            if (Boolean.valueOf(jsonObject.optString("hasNewVersion"))){
                stringBuilder.append("当前版本号:"+currentVersion+"\n\n"+"最新版本号:"+jsonObject.opt("latest_version")+"\n\n"+jsonObject.opt("desc"));
            }else {
                stringBuilder.append("当前版本号:"+currentVersion+"\n\n"+jsonObject.opt("desc"));
            }
            view.upDateTextViewText(stringBuilder.toString());
        }
    }

}
