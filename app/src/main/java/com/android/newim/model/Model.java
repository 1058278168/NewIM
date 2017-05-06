package com.android.newim.model;

import android.content.Context;

import com.android.newim.model.bean.UserInfo;
import com.android.newim.model.dao.ContactTableDao;
import com.android.newim.model.dao.UserAccountDao;
import com.android.newim.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据模型层全局类
 * Created by shengli.zhang on 2017/4/22.
 */

public class Model {
    Context mContext;

    //创建model对象
    private static Model model = new Model();
    //创建线程池对象
    public ExecutorService executors = Executors.newCachedThreadPool();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;

    //构造方法私有化
    private Model() {

    }

    //获取单例对象
    public static Model getInstance() {

        return model;
    }

    //初始化模型层
    public void init(Context context) {
        mContext = context;

        userAccountDao = new UserAccountDao(mContext);

        //开启全局监听
        EventListener listener = new EventListener(mContext);
    }

    //获取全局线程池对象
    public ExecutorService getGlobalThreadPool() {
        return executors;
    }

    public UserAccountDao getUserAccountDao() {

        return userAccountDao;
    }

    //用戶登陸成功后的处理方法
    public void loginSuccess(UserInfo account) {
        //校验
        if (account == null) {
            return;
        }
        if (dbManager != null) {
            dbManager.close();
        }
        dbManager = new DBManager(mContext, account.getName());
    }

    public DBManager getDbManager() {
        return dbManager;
    }


}
