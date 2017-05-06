package com.android.newim.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.newim.model.bean.GroupInfo;
import com.android.newim.model.bean.InvitionInfo;
import com.android.newim.model.bean.UserInfo;
import com.android.newim.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 邀请信息表的操作类
 * Created by shengli.zhang on 2017/4/23.
 */

public class InviteTableDao {
    private DBHelper mHelper;

    public InviteTableDao(DBHelper mHelper) {
        this.mHelper = mHelper;
    }

    //添加邀请
    public void addInvition(InvitionInfo invitionInfo) {
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行添加语句
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_REASON, invitionInfo.getReason());
        values.put(InviteTable.COL_STATUS, invitionInfo.getStatus().ordinal());
        UserInfo user = invitionInfo.getUser();
        if (user != null) {//联系人
            values.put(InviteTable.COL_USER_HXID, user.getHxid());
            values.put(InviteTable.COL_USER_NAME, user.getName());
        } else {//群组
            values.put(InviteTable.COL_GROUP_HXID, invitionInfo.getGroup().getGroupId());
            values.put(InviteTable.COL_GROUP_NAME, invitionInfo.getGroup().getGroupName());
            values.put(InviteTable.COL_USER_HXID, invitionInfo.getGroup().getInvitePerson());
        }
        db.replace(InviteTable.TAB_NAME, null, values);
    }

    //获取所有邀请信息
    public List<InvitionInfo> getInvitations() {
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行查询语句
        String sql = "select * from " + InviteTable.TAB_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        List<InvitionInfo> invitionInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            InvitionInfo info = new InvitionInfo();
            info.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));
            info.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTable
                    .COL_STATUS))));
            String hxid = cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID));
            if (hxid != null) {//个人用户
                UserInfo user = new UserInfo();
                user.setHxid(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                user.setName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                user.setNick(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                info.setUser(user);
            } else {//群组
                GroupInfo group = new GroupInfo();
                group.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTable
                        .COL_GROUP_HXID)));
                group.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTable
                        .COL_GROUP_NAME)));
                group.setInvitePerson(cursor.getString(cursor.getColumnIndex(InviteTable
                        .COL_USER_HXID)));
                info.setGroup(group);
            }
            invitionInfos.add(info);
        }
        //关闭链接
        cursor.close();
        //返数据
        return invitionInfos;
    }

    // 将int类型状态转换为邀请的状态
    private InvitionInfo.InvitationStatus int2InviteStatus(int intStatus) {

        if (intStatus == InvitionInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvitionInfo.InvitationStatus.NEW_INVITE;
        }

        if (intStatus == InvitionInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvitionInfo.InvitationStatus.INVITE_ACCEPT;
        }

        if (intStatus == InvitionInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvitionInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvitionInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvitionInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InvitionInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvitionInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvitionInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvitionInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InvitionInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvitionInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InvitionInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvitionInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InvitionInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvitionInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InvitionInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvitionInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InvitionInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvitionInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }

        if (intStatus == InvitionInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvitionInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InvitionInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvitionInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }

        return null;
    }

    //移除数据库操作
    public void removeInvitarion(String hxid) {
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行删除操作
        db.delete(InviteTable.TAB_NAME, InviteTable.COL_USER_HXID + "=?", new String[]{hxid});
    }

    //更新数据库操作
    public void updateInvitation(InvitionInfo.InvitationStatus status, String hxid) {
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行更新操作
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_STATUS, status.ordinal());
        db.update(InviteTable.TAB_NAME, values, InviteTable.COL_USER_HXID + "=?", new
                String[]{hxid});
    }
}
