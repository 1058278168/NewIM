package com.android.newim.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.newim.model.bean.UserInfo;
import com.android.newim.model.db.UserAccountDB;

/**
 * 用户账号数据库的操作类
 * Created by shengli.zhang on 2017/4/22.
 */

public class UserAccountDao {
    public final UserAccountDB mHelp;

    public UserAccountDao(Context context) {
        mHelp = new UserAccountDB(context);
    }

    //添加账号
    public void addAccount(UserInfo user) {
        //获取数据库对象
        SQLiteDatabase db = mHelp.getReadableDatabase();
        //执行添加操作
        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_HXID, user.getHxid());
        values.put(UserAccountTable.COL_NAME, user.getName());
        values.put(UserAccountTable.COL_NICK, user.getNick());
        values.put(UserAccountTable.COL_PHOTO, user.getPhoto());
        db.replace(UserAccountTable.TAB_NAME, null, values);
    }

    //根据hxid查询账号信息
    public UserInfo getAccountByHxId(String hxid) {
        //获取数据库对象
        SQLiteDatabase db = mHelp.getReadableDatabase();
        //执行查询操作
        String sql = "select * from " + UserAccountTable.TAB_NAME + " where " + UserAccountTable
                .COL_HXID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxid});
        UserInfo info = null;
        if (cursor.moveToNext()) {
            info = new UserInfo();
            info.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            info.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            info.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            info.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }
        //关闭资源
        cursor.close();
        //返回数据
        return info;
    }
}
