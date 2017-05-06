package com.android.newim.model.dao;

/**
 * Created by shengli.zhang on 2017/4/23.
 */

public class InviteTable {
    public static final String TAB_NAME = "tab_invite";
    //环信用户
    public static final String COL_USER_HXID = "user_hxid";
    public static final String COL_USER_NAME = "user_name";
    //群组
    public static final String COL_GROUP_NAME = "group_name";
    public static final String COL_GROUP_HXID = "group_hxid";
    public static final String COL_REASON = "reason";//邀请原因
    public static final String COL_STATUS = "status";//邀请状态

    public static final String CREATE_TAB = "create table "
            + TAB_NAME + "("
            + COL_USER_HXID + " text primary key,"
            + COL_USER_NAME + " text,"
            + COL_GROUP_NAME + " text,"
            + COL_GROUP_HXID + " text,"
            + COL_REASON + " text,"
            + COL_STATUS + " integer);";
}
