package com.android.newim.model.db;

import android.content.Context;
import android.util.Log;

import com.android.newim.model.dao.ContactTableDao;
import com.android.newim.model.dao.InviteTableDao;

/**
 * Created by shengli.zhang on 2017/4/23.
 */

public class DBManager {

    private final DBHelper dbHelper;
    private final ContactTableDao contactTableDao;
    private final InviteTableDao inviteTableDao;

    public DBManager(Context context, String name) {
        dbHelper = new DBHelper(context, name);
        contactTableDao = new ContactTableDao(dbHelper);
        inviteTableDao = new InviteTableDao(dbHelper);
        Log.e("TAG", contactTableDao.toString() + "  " + inviteTableDao.toString());
    }

    public ContactTableDao getContactTableDao() {
        return contactTableDao;
    }

    public InviteTableDao getInviteTableDao() {
        return inviteTableDao;
    }

    public void close() {
        dbHelper.close();
    }
}
