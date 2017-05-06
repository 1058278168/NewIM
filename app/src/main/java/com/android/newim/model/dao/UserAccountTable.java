package com.android.newim.model.dao;

/**
 * Created by shengli.zhang on 2017/4/22.
 */

public class UserAccountTable {
    public static final String TAB_NAME = "tab_account";
    public static final String COL_NAME = "name";
    public static final String COL_HXID = "hxid";
    public static final String COL_NICK = "nick";
    public static final String COL_PHOTO = "photo";
    public static final String CREATE_TABLE = "create table " + TAB_NAME + "("
            + COL_HXID + " text primary key,"
            + COL_NAME + " text,"
            + COL_NICK + " text,"
            + COL_PHOTO + " text);";
}
