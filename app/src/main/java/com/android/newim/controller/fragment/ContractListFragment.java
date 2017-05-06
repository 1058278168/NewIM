package com.android.newim.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.newim.R;
import com.android.newim.controller.activity.ChartActivity;
import com.android.newim.controller.activity.GroupListActivity;
import com.android.newim.controller.activity.InviteActivity;
import com.android.newim.controller.activity.NewContactActivity;
import com.android.newim.model.Model;
import com.android.newim.model.bean.UserInfo;
import com.android.newim.utils.Constant;
import com.android.newim.utils.SpUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shengli.zhang on 2017/4/22.
 */

public class ContractListFragment extends EaseContactListFragment {

    private LinearLayout ll_contact_invite, ll_contact_group;
    private ImageView iv_contact_red;
    private LocalBroadcastManager broadcastManager;
    private String mHxid;


    @Override
    protected void initView() {
        super.initView();
        titleBar.setRightImageResource(R.drawable.em_add);
        View view = View.inflate(getActivity(), R.layout.header_fragment_contact, null);
        ll_contact_invite = (LinearLayout) view.findViewById(R.id.ll_contact_invite);
        ll_contact_group = (LinearLayout) view.findViewById(R.id.ll_contact_group);
        iv_contact_red = (ImageView) view.findViewById(R.id.iv_contact_red);
        listView.addHeaderView(view);

        //listView点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                if (user == null) {
                    return;
                }
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        titleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewContactActivity.class);
                startActivity(intent);
            }
        });

        //初始化红点
        boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE, false);
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_contact_red.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
            }
        });

        //点击群组
        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //注册邀请信息变化广播
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(contactInviteReceiver, new IntentFilter(Constant
                .CONTACT_INVITE_CHANGED));
        //注册联系人变化的广播
        broadcastManager.registerReceiver(ContactChangedReceiver, new IntentFilter(Constant
                .CONTACT_CHANGED));

        //获取全部联系人信息
        getConttactsInfo();

        //设置listview与contentmenu的绑定，长按删除好友列表中的该好友
        registerForContextMenu(listView);
    }

    //加载contentmenu布局
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //获得选中的条目的信息
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(((AdapterView
                .AdapterContextMenuInfo)
                menuInfo).position);
        //获取hxid
        mHxid = easeUser.getUsername();
        //加载布局
        getActivity().getMenuInflater().inflate(R.menu.delete, menu);
    }

    //删除选中的条目
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contact_delete) {
            //删除联系人
            deleteContact();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    //执行删除联系人的操作
    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //在onCreateContextMenu中获取hxid
                try {
                    //删除服务器的联系人
                    EMClient.getInstance().contactManager().deleteContact(mHxid);
                    //更新本地数据库
                    //删除本地数据库表中的该条信息
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHx
                            (mHxid);
                    //移除邀请信息
                    Model.getInstance().getDbManager().getInviteTableDao().removeInvitarion(mHxid);
                    //更新内存（刷新数据）
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除好友" + mHxid + "成功", Toast
                                    .LENGTH_SHORT).show();
                            refreshContacts();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除好友" + mHxid + "失败", Toast
                                    .LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    /**
     * 获取全部联系人信息
     */
    private void getConttactsInfo() {
        //从服务器获取联系人信息
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> hxids = EMClient.getInstance().contactManager()
                            .getAllContactsFromServer();
                    //保存到本地数据库中
                    if (hxids != null && hxids.size() >= 0) {
                        List<UserInfo> contacts = new ArrayList<>();
                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }
                        Model.getInstance().getDbManager().getContactTableDao().saveContacts
                                (contacts, true);
                        //更新UI
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContacts();
                            }
                        });
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 刷新联系人列表
     */
    private void refreshContacts() {
        //从本地数据库中获得全部联系人
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao()
                .getContacts();
        //校验
        if (contacts != null && contacts.size() >= 0) {
            Map<String, EaseUser> contactMap = new HashMap<>();
            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());
                contactMap.put(contact.getHxid(), easeUser);
            }
            //设置数据
            setContactsMap(contactMap);//EaseContactList设置数据
            //通知适配器数据变化，刷新来呢西人列表
            refresh();
        }
    }

    /**
     * 邀请信息变化的广播
     */
    private BroadcastReceiver contactInviteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
        }
    };

    /**
     * 联系人变化的广播
     */
    private BroadcastReceiver ContactChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshContacts();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(contactInviteReceiver);
        broadcastManager.unregisterReceiver(ContactChangedReceiver);
        unregisterForContextMenu(listView);
    }
}
