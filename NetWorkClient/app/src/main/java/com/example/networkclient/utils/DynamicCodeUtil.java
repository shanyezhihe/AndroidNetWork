package com.example.networkclient.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pengdongyuan491 on 16/9/5.
 */
public class DynamicCodeUtil {
    private static String dynamicCode = null;
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static Uri SMS_INBOX = Uri.parse("content://sms/");
    private static SmsBroadCastReceiver mSmsBroadCastReceiver;
    private static SmsContent smsContent;
    private static DynamicCodeReceiveListerner mDynamicCodeReceiveListerner;

    /*
    通过系统广播获取验证码
     */
    public static void getDynamicCodeByBroadCast(Context context, DynamicCodeReceiveListerner dynamicCodeReceiveListerner) {
        mDynamicCodeReceiveListerner = dynamicCodeReceiveListerner;
        mSmsBroadCastReceiver = new SmsBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        context.registerReceiver(mSmsBroadCastReceiver, intentFilter);
    }

    /*
    通过监听数据库变化获取验证码
     */

    public static void getDynamicCodeByContentObserver(Context context, DynamicCodeReceiveListerner dynamicCodeReceiveListerner) {
        mDynamicCodeReceiveListerner = dynamicCodeReceiveListerner;
        smsContent = new SmsContent(new Handler(), context);
        context.getContentResolver().registerContentObserver(SMS_INBOX, true, smsContent);
    }

    /*
    取消注册,防止内存泄漏
     */
    public static void unRegister(Context context) {
        if (mSmsBroadCastReceiver != null) {
            context.unregisterReceiver(mSmsBroadCastReceiver);
            mSmsBroadCastReceiver=null;
        }
        if (smsContent != null) {
            context.getContentResolver().unregisterContentObserver(smsContent);
            smsContent=null;
        }
    }

    static class SmsBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broad","广播来了");
            if (intent.getAction().equals(ACTION)) {
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String content = smsMessage.getDisplayMessageBody();
                    Log.e("CONTENT", content);
                    dynamicCode = getDynamicPassword(content);
                    mDynamicCodeReceiveListerner.onReceiveCode(dynamicCode);
                }
            }
        }
    }

    /**
     * 监听短信数据库
     */
    static class SmsContent extends ContentObserver {

        private Cursor cursor = null;
        private Context context;

        public SmsContent(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Cursor cursor = this.context.getContentResolver().query(SMS_INBOX, null, null, null, "date desc");
            Log.e("content", "cursor.isBeforeFirst() " + cursor.isBeforeFirst() + " cursor.getCount() " + cursor.getCount());
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int smsbodyColumn = cursor.getColumnIndex("body");
                    String smsBody = cursor.getString(smsbodyColumn);
                    Log.e("body", smsBody + "这里是有数据的");
                    dynamicCode = getDynamicPassword(smsBody);
                    mDynamicCodeReceiveListerner.onReceiveCode(dynamicCode);
                }
            }

        }
    }

    /**
     * 从字符串中截取连续6位数字
     * 用于从短信中获取动态密码
     *
     * @param str 短信内容
     * @return 截取得到的6位动态密码
     */
    private static String getDynamicPassword(String str) {
        Pattern pattern = Pattern.compile("[0-9\\.]+");
        Matcher m = pattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            if (m.group().length() == 6) {
                dynamicPassword = m.group();
                break;
            }
        }
        return dynamicPassword;
    }


    public interface DynamicCodeReceiveListerner {
        void onReceiveCode(String code);
    }
}
