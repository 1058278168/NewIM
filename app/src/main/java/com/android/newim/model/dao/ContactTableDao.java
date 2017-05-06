package com.android.newim.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.newim.model.bean.UserInfo;
import com.android.newim.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人数据库操作的类
 * Created by shengli.zhang on 2017/4/23.
 */

public class ContactTableDao {
    DBHelper mHelper;

    public ContactTableDao(DBHelper mHelper) {
        this.mHelper = mHelper;
    }

    //获取所有联系人信息
    public List<UserInfo> getContacts() {
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行查询语句
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable
                .COL_IS_CONTACT + "=1";
        Cursor cursor = db.rawQuery(sql, null);
        List<UserInfo> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            UserInfo contact = new UserInfo();
            contact.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            contact.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            contact.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
            contacts.add(contact);
        }
        //关闭资源
        cursor.close();
        //返回数据
        return contacts;
    }

    //根据环信id获取单个联系人信息
    public UserInfo getContactByHx(String hxid) {
        if (hxid == null) {
            return null;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行查询语句
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_HXID
                + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxid});
        UserInfo user = null;
        if (cursor.moveToNext()) {
            user = new UserInfo();
            user.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            user.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            user.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            user.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
        }
        //关闭资源
        cursor.close();
        //返回数据
        return user;
    }

    //根据环信id获取联系人信息
    public List<UserInfo> getContactsByHx(List<String> hxids) {
        if (hxids == null || hxids.size() <= 0) {
            return null;
        }
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行查询语句
        List<UserInfo> users = new ArrayList<>();
        for (String hxid : hxids) {
            UserInfo user = getContactByHx(hxid);
            users.add(user);
        }
        //返回数据
        return users;
    }

    //保存单个联系人信息
    public void saveContact(UserInfo user, boolean isMyContact) {
        //校验UserInfo是否为空
        if (user == null) {
            return;
        }
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行添加操作
        ContentValues values = new ContentValues();
        values.put(ContactTable.COL_HXID, user.getHxid());
        values.put(ContactTable.COL_NAME, user.getName());
        values.put(ContactTable.COL_NICK, user.getNick());
        values.put(ContactTable.COL_PHOTO, user.getPhoto());
        values.put(ContactTable.COL_IS_CONTACT, isMyContact ? 1 : 0);
        db.replace(ContactTable.TAB_NAME, null, values);
    }

    //保存联系人信息
    public void saveContacts(List<UserInfo> contacts, boolean isMyContact) {
        //校验contacts是否为空
        if (contacts == null || contacts.size() <= 0) {
            return;
        }
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行添加语句
        for (UserInfo contact : contacts) {
            saveContact(contact, isMyContact);
        }
    }

    //删除联系人信息
    public void deleteContactByHx(String hxid) {
        //校验
        if (hxid == null) {
            return;
        }
        //获得数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行删除语句
        db.delete(ContactTable.TAB_NAME, ContactTable.COL_HXID + "=?", new String[]{hxid});
    }
}
