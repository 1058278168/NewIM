package com.android.newim.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.newim.R;
import com.android.newim.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by shengli.zhang on 2017/4/24.
 */

public class NewGroupActivity extends AppCompatActivity {
    private static final int REQUEST_CONTACT_LIST = 1000;
    private EditText et_newgroup_name;//群组名称
    private EditText et_newgroup_desc;//群组简介
    private CheckBox cb_newgroup_public;//是否公开
    private CheckBox cb_newgroup_invite;//是否开放群邀请
    private Button bt_newgroup_create;//创建群按钮

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        //初始化view
        initViews();

        //初始化监听
        initListener();
    }

    //初始化监听
    private void initListener() {
        //创建群按钮点击事件
        bt_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
                startActivityForResult(intent, REQUEST_CONTACT_LIST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CONTACT_LIST) {
                //创建群
                createGroup(data.getStringArrayExtra("members"));
            }
        }
    }

    //创建群
    private void createGroup(final String[] memberses) {
        final String groupName = et_newgroup_name.getText().toString();//群名称
        final String groupDes = et_newgroup_desc.getText().toString();//群描述
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();
                options.maxUsers = 200;//群容纳的最大人数
                EMGroupManager.EMGroupStyle groupStyle = null;
                if (cb_newgroup_public.isChecked()) {//公开群
                    if (cb_newgroup_invite.isChecked()) {//开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {//不开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                } else {//不公开群
                    if (cb_newgroup_invite.isChecked()) {//开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    } else {//不开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }
                options.style = groupStyle;//创建群的类型

                try {
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDes,
                            memberses, "创建群聊", options);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败"+e.toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }

            }
        });
    }

    //初始化view
    private void initViews() {
        et_newgroup_name = (EditText) findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = (EditText) findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = (CheckBox) findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = (CheckBox) findViewById(R.id.cb_newgroup_invite);
        bt_newgroup_create = (Button) findViewById(R.id.bt_newgroup_create);
    }
}
