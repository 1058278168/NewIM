package com.android.newim.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newim.R;
import com.android.newim.model.Model;
import com.android.newim.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by shengli.zhang on 2017/4/22.
 */
public class NewContactActivity extends Activity implements View.OnClickListener {

    private TextView tvAddFind;
    private EditText etAddName;
    private RelativeLayout rlAdd;
    private ImageView ivAddPhoto;
    private TextView tvAddName;
    private Button btAddAdd;
    private UserInfo userInfo;
    private String name;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-04-22 21:39:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        tvAddFind = (TextView) findViewById(R.id.tv_add_find);
        etAddName = (EditText) findViewById(R.id.et_add_name);
        rlAdd = (RelativeLayout) findViewById(R.id.rl_add);
        ivAddPhoto = (ImageView) findViewById(R.id.iv_add_photo);
        tvAddName = (TextView) findViewById(R.id.tv_add_name);
        btAddAdd = (Button) findViewById(R.id.bt_add_add);

        btAddAdd.setOnClickListener(this);
        tvAddFind.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-04-22 21:39:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btAddAdd) {
            // Handle clicks for btAddAdd
            UserInfo contactByHx = Model.getInstance().getDbManager().getContactTableDao()
                    .getContactByHx(name);
            if (contactByHx != null) {
                Toast.makeText(NewContactActivity.this, "已经添加了该好友", Toast.LENGTH_SHORT).show();
                return;
            }
            add();
        } else if (v == tvAddFind) {
            //search
            find();
        }
    }

    //添加好友
    private void add() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(), "添加好友");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewContactActivity.this, "添加好友邀请成功", Toast
                                    .LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewContactActivity.this, "添加好友邀请失败" + e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //查找好友
    private void find() {
        name = etAddName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(NewContactActivity.this, "输入的用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //去服务器查找用户是否存在
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                userInfo = new UserInfo(name);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rlAdd.setVisibility(View.VISIBLE);
                        tvAddName.setText(name);
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        findViews();
    }
}
