package com.android.newim.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.android.newim.R;
import com.android.newim.controller.adapter.PickContactAdapter;
import com.android.newim.model.Model;
import com.android.newim.model.bean.PickContactInfo;
import com.android.newim.model.bean.UserInfo;
import com.android.newim.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shengli.zhang on 2017/4/26.
 */

public class PickContactActivity extends AppCompatActivity {

    private TextView tv_pick_save;//保存选择的联系人
    private ListView lv_pick;//显示联系人列表
    private List<PickContactInfo> mPicks;
    private List<String> mExistMember;//群组中已经存在的人
    private PickContactAdapter pickContactAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        //获取数据
        getData();
        //初始化view
        initViews();
        //初始化数据
        initData();
        //初始化监听
        initListener();
    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            mExistMember = group.getMembers();
        }
        if (mExistMember == null) {
            mExistMember = new ArrayList<>();
        }
    }

    private void initData() {
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao()
                .getContacts();
        mPicks = new ArrayList<>();
        for (UserInfo contact : contacts) {
            PickContactInfo pickContactInfo = new PickContactInfo(contact, false);
            mPicks.add(pickContactInfo);
        }
        pickContactAdapter = new PickContactAdapter(this, mPicks, mExistMember);
        lv_pick.setAdapter(pickContactAdapter);
    }

    private void initListener() {
        //listview的item的点击事件
        lv_pick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Checkbox的切换
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_pick);
                checkBox.setChecked(!checkBox.isChecked());
                //修改数据
                PickContactInfo pickContactInfo = mPicks.get(position);
                pickContactInfo.setChecked(checkBox.isChecked());
                //刷新页面
                pickContactAdapter.notifyDataSetChanged();
            }
        });
        //保存按钮点击事件
        tv_pick_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取到已经选择的联系人
                List<String> pickContacts = pickContactAdapter.getPickContacts();
                //给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members", pickContacts.toArray(new String[0]));
                setResult(Activity.RESULT_OK, intent);
                //关闭页面
                finish();
            }
        });
    }

    private void initViews() {
        tv_pick_save = (TextView) findViewById(R.id.tv_pick_save);
        lv_pick = (ListView) findViewById(R.id.lv_pick);
    }
}
