package com.android.newim.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.newim.R;
import com.android.newim.controller.adapter.GroupListAdapter;
import com.android.newim.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * Created by shengli.zhang on 2017/4/24.
 */

public class GroupListActivity extends AppCompatActivity {

    private ListView lv_grouplist;
    private GroupListAdapter mAdapter;
    private LinearLayout ll_grouplist;
    private List<EMGroup> mGroups;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        initview();//初始化view

        initData(); //初始化数据

        initListener();//初始化监听
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        //群组列表item点击事件
        lv_grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("TAG", "position==>" + position);
                if (position == 0) {
                    return;
                }
                Intent intent = new Intent(GroupListActivity.this, ChartActivity.class);
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get
                        (position - 1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, emGroup.getGroupId());
                startActivity(intent);
            }
        });
        //添加新群组条目点击事件
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化view
     */
    private void initview() {
        lv_grouplist = (ListView) findViewById(R.id.lv_grouplist);
        //为listview添加头布局
        View headView = View.inflate(GroupListActivity.this, R.layout.header_grouplist, null);
        lv_grouplist.addHeaderView(headView);

        ll_grouplist = (LinearLayout) headView.findViewById(R.id.ll_grouplist);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //为listView设置适配器
        mAdapter = new GroupListAdapter(this);
        lv_grouplist.setAdapter(mAdapter);

        //从服务器获取全部的群组信息
        getGroupFromServer();
    }

    /**
     * 从服务器获得全部群组信息
     */
    private void getGroupFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从网络获取数据
                    mGroups = EMClient.getInstance().groupManager()
                            .getJoinedGroupsFromServer();
                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //提示加载群组信息成功
                            Toast.makeText(GroupListActivity.this, "加载群组信息成功", Toast
                                    .LENGTH_SHORT).show();
                            refresh();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //提示加载群组信息成功
                            Toast.makeText(GroupListActivity.this, "加载群组信息失败" + e.toString(), Toast
                                    .LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * 刷新界面
     */
    public void refresh() {
        mAdapter.refresh(mGroups);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
