package com.android.newim;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.android.newim.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by shengli.zhang on 2017/4/19.
 */

public class IMApplication extends Application {

    private static Context mContext;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);//设置自主选择是否接受好友邀请
        options.setAutoAcceptGroupInvitation(false);//设置自主选择是否接受群组邀请
        EaseUI.getInstance().init(this, options);//初始化EaseUI
        Model.getInstance().init(this);//初始化数据模型层
        mContext = this;
    }

    // 获取全局上下文对象
    public static Context getGlobalApplication() {
        return mContext;
    }

}
