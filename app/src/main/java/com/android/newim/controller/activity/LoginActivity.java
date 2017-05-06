package com.android.newim.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.newim.R;
import com.android.newim.model.Model;
import com.android.newim.model.bean.UserInfo;
import com.android.newim.model.dao.UserAccountDao;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by shengli.zhang on 2017/4/21.
 */
public class LoginActivity extends Activity implements View.OnClickListener {


    private EditText etLoginName;
    private EditText etLoginPwd;
    private Button btLoginRegist;
    private Button btLoginLogin;
    private String account, pwd;
    private UserAccountDao userAccountDao;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-04-22 09:33:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        etLoginName = (EditText) findViewById(R.id.et_login_name);
        etLoginPwd = (EditText) findViewById(R.id.et_login_pwd);
        btLoginRegist = (Button) findViewById(R.id.bt_login_regist);
        btLoginLogin = (Button) findViewById(R.id.bt_login_login);

        btLoginRegist.setOnClickListener(this);
        btLoginLogin.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-04-22 09:33:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btLoginRegist) {
            // Handle clicks for btLoginRegist
            regist();
        } else if (v == btLoginLogin) {
            // Handle clicks for btLoginLogin
            login();
        }
    }

    //登陆
    private void login() {
        //获取用户名密码
        account = etLoginName.getText().toString();
        pwd = etLoginPwd.getText().toString();
        //校验用户名和密码
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码错误,请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        //请求服务器登陆
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(account, pwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {//登陆成功后的处理操作
                        //保存数据到本地数据库
                        Model.getInstance().loginSuccess(new UserInfo(account));
//                        UserAccountDao uAD = new UserAccountDao(LoginActivity.this);
//                        uAD.addAccount(new UserInfo(account));
                        userAccountDao = Model.getInstance().getUserAccountDao();
                        userAccountDao.addAccount(new UserInfo(account));
                        //提示登陆成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT)
                                        .show();
                                //跳转到主页面
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {//登录失败的处理操作

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //提示登录失败
                                Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {//登陆过过程中的处理操作

                    }
                });
            }
        });
    }

    //注册
    private void regist() {
        //获取用户名和密码
        account = etLoginName.getText().toString();
        pwd = etLoginPwd.getText().toString();
        //校验用户名和密码
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码错误,请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        //联网请求注册账号
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(account, pwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败" + e.toString(), Toast
                                    .LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
    }
}
