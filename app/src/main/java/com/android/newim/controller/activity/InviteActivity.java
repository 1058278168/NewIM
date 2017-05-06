package com.android.newim.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.android.newim.R;
import com.android.newim.controller.adapter.InviteAdapter;
import com.android.newim.model.Model;
import com.android.newim.model.bean.InvitionInfo;
import com.android.newim.model.bean.UserInfo;
import com.android.newim.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * Created by shengli.zhang on 2017/4/23.
 */

public class InviteActivity extends AppCompatActivity {

    private ListView lv_invite;
    private InviteAdapter mAdapter;
    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        lv_invite = (ListView) findViewById(R.id.lv_invite);
        initData();
    }

    private void initData() {
        mAdapter = new InviteAdapter(InviteActivity.this, onInviteListener);
        lv_invite.setAdapter(mAdapter);
        refresh();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant
                .CONTACT_INVITE_CHANGED));
        broadcastManager.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant
                .GROUP_INVITE_CHANGED));
    }

    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 刷新页面
            refresh();
        }
    };

    //刷新界面
    private void refresh() {
        //获取数据库中所有的邀请信息
        List<InvitionInfo> invitations = Model.getInstance().getDbManager().getInviteTableDao()
                .getInvitations();
        //刷新适配器
        mAdapter.refresh(invitations);
    }

    InviteAdapter.OnInviteListener onInviteListener = new InviteAdapter.OnInviteListener() {
        /**
         * 接受好友邀请
         * @param invationInfo
         */
        @Override
        public void onAccept(final InvitionInfo invationInfo) {//接受邀请
            //通知环信服务器，接受了好友邀请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo
                                .getUser().getHxid());
                        //更新数据库，在数据库中更新该条状态
                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitation
                                (InvitionInfo.InvitationStatus.INVITE_ACCEPT, invationInfo
                                        .getUser().getHxid());
                        //刷新UI界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受了好友邀请", Toast
                                        .LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败" + e.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        /**
         * 拒绝好友邀请
         * @param invationInfo
         */
        @Override
        public void onReject(final InvitionInfo invationInfo) {//拒绝邀请
            //告诉服务器，拒绝了好友邀请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo
                                .getUser().getHxid());
                        //更新数据库，在数据库中删除改该条好友信息
                        Model.getInstance().getDbManager().getInviteTableDao().removeInvitarion
                                (invationInfo.getUser().getHxid());
                        //更新UI界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝了好友邀请", Toast
                                        .LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * 接受群邀请
         * @param invationInfo
         */
        @Override
        public void onInviteAccept(final InvitionInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //通知环信服务器接受了群邀请
                    try {
                        EMClient.getInstance().groupManager().acceptInvitation(invationInfo
                                .getGroup().getGroupId(), invationInfo.getGroup().getInvitePerson
                                ());
                        //跟新本地数据库
                        invationInfo.setStatus(InvitionInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvition
                                (invationInfo);
                        //更新UI，内存数据发生变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT)
                                        .show();

                                // 刷新页面
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        //更新UI，内存数据发生变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败" + e.toString(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                }
            });
        }

        /**
         * 拒绝群邀请
         * @param invationInfo
         */
        @Override
        public void onInviteReject(final InvitionInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //告诉服务器拒绝了群邀请
                    try {
                        EMClient.getInstance().groupManager().declineInvitation(invationInfo
                                        .getGroup
                                                ().getGroupId(), invationInfo.getGroup()
                                        .getInvitePerson(),
                                "拒绝邀请");
                        //更新本地数据库
                        invationInfo.setStatus(InvitionInfo.InvitationStatus.GROUP_REJECT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvition
                                (invationInfo);
                        //更新UI，内存数据发生变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请", Toast.LENGTH_SHORT)
                                        .show();

                                // 刷新页面
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请失败" + e.toString(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                }
            });
        }

        /**
         * 接受群申请
         * @param invationInfo
         */
        @Override
        public void onApplicationAccept(final InvitionInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //告诉服务器接受了群申请
                    try {
                        EMClient.getInstance().groupManager().acceptApplication(invationInfo
                                .getGroup().getGroupId(), invationInfo.getGroup().getInvitePerson
                                ());
                        //更新本地数据库
                        invationInfo.setStatus(InvitionInfo.InvitationStatus
                                .GROUP_ACCEPT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvition
                                (invationInfo);
                        //更新UI，内存数据发生变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受申请", Toast.LENGTH_SHORT)
                                        .show();

                                // 刷新页面
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受申请失败" + e.toString(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                }
            });
        }

        /**
         * 拒绝群申请
         * @param invationInfo
         */
        @Override
        public void onApplicationReject(final InvitionInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //告诉服务器拒绝了群邀请
                    try {
                        EMClient.getInstance().groupManager().declineApplication(invationInfo
                                .getGroup().getGroupId(), invationInfo.getGroup().getInvitePerson
                                (), "拒绝申请");
                        //更新本地数据库
                        invationInfo.setStatus(InvitionInfo.InvitationStatus
                                .GROUP_REJECT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvition
                                (invationInfo);
                        //更新UI，内存数据发生变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请", Toast.LENGTH_SHORT)
                                        .show();

                                // 刷新页面
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请失败" + e.toString(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(InviteChangedReceiver);
    }
}
