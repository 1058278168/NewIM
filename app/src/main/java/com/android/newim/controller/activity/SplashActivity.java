package com.android.newim.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.newim.R;
import com.android.newim.model.Model;
import com.android.newim.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

/**
 * Created by shengli.zhang on 2017/4/21.
 */

public class SplashActivity extends Activity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isFinishing()) {//如果当前activity已经推出，则不处理
                return;
            }
            toMainorLogin();//判断当前账号是否登陆过,通过请求环信的服务器来判断
        }
    };

    //请求服务器是耗时操作，在子线程中运行
    private void toMainorLogin() {


        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //判断当前账号是否登陆过

                if (EMClient.getInstance().isLoggedInBefore()) {//登陆过,则进入主界面
                    UserInfo account = Model.getInstance().getUserAccountDao()
                            .getAccountByHxId(EMClient.getInstance().getCurrentUser());
                    if (account == null) {//如果当前账号没有数据
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Model.getInstance().loginSuccess(account);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {//没登陆过，则跳转到登陆界面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                //结束当前页面
                finish();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        handler.sendMessageDelayed(Message.obtain(), 2000);//Message.obtain：从消息池中任意取出一个
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);//移除消息
    }
}
