package com.example.networkclient.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.networkclient.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;


/**
 * Created by pengdongyuan on 16/7/26.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public final static String IS_START_ANIM = "IS_START_ANIM";
    public final static String IS_CLOSE_ANIM = "IS_CLOSE_ANIM";
    protected boolean isStartAnim = true;
    protected boolean isCloseAnim = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        parseIntent(getIntent());
        showActivityInAnim();
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        initWindow();

    }

    private void parseIntent(Intent intent){
        if (intent != null) {
            isStartAnim = intent.getBooleanExtra(IS_START_ANIM, true);
            isCloseAnim = intent.getBooleanExtra(IS_CLOSE_ANIM, true);
        }
    }

    @TargetApi(19)
    private void initWindow(){
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(this.getResources().getColor(R.color.colorPrimary));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    protected abstract int getLayoutRes();


    protected void showActivityInAnim(){
        if (isStartAnim) {
            overridePendingTransition(R.anim.activity_down_up_anim, R.anim.activity_exit_anim);
        }
    }

    protected void showActivityExitAnim(){
        if (isCloseAnim) {
            overridePendingTransition(R.anim.activity_exit_anim, R.anim.activity_up_down_anim);
        }
    }


    @Override
    public void finish() {
        super.finish();
        showActivityExitAnim();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //call before super.onCreate(savedInstanceState)
    protected void launchWithNoAnim() {
        isStartAnim = false;
    }

    protected void exitWithNoAnim() {
        isCloseAnim = false;
    }

}
