package com.android.newim.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.newim.IMApplication;

/**
 * Created by shengli.zhang on 2017/4/23.
 */

public class SpUtils {
    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;

    private SpUtils() {
    }

    public static SpUtils getInstance() {
        if (mSp == null) {
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("IM",
                    Context.MODE_PRIVATE);
        }
        return instance;
    }


    // 保存
    public void save(String key, Object value) {
        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).apply();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).apply();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    // 读取
    // 读取String类型数据
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    // 读取boolean类型数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    // 读取int类型数据
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }
}
