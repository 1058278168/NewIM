package com.android.newim.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.LocalBroadcastManager;

import com.android.newim.model.bean.GroupInfo;
import com.android.newim.model.bean.InvitionInfo;
import com.android.newim.model.bean.UserInfo;
import com.android.newim.utils.Constant;
import com.android.newim.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;

/**
 * 全局监听的类
 * Created by shengli.zhang on 2017/4/23.
 */

public class EventListener {
    private Context mContext;
    private final LocalBroadcastManager localBroadcastManager;

    public EventListener(Context context) {
        mContext = context;
        //创建发送广播的管理者对象
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        //注册一个联系人添加的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        //注册一个添加群组的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangedListener);
    }

    private final EMContactListener emContactListener = new EMContactListener() {
        /**
         * 联系人添加后执行
         * @param hxid
         */
        @Override
        public void onContactAdded(String hxid) {
            //本地数据库更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo
                    (hxid), true);
            //发送联系人变化的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        /**
         * 联系人删除后执行
         * @param hxid
         */
        @Override
        public void onContactDeleted(String hxid) {
            //更新本地数据库
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHx(hxid);
            //发送联系人变化的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_REMOVE));
        }

        /**
         * 接收到联系人的新邀请
         * @param hxid
         * @param reason
         */
        @Override
        public void onContactInvited(String hxid, String reason) {
            //更新本地数据库
            InvitionInfo invitationInfo = new InvitionInfo();
            invitationInfo.setUser(new UserInfo(hxid));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitionInfo.InvitationStatus.NEW_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitationInfo);
            //红点提示的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送好友邀请的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        /**
         * 联系人同意了好友邀请
         * @param hxid
         */
        @Override
        public void onContactAgreed(String hxid) {
            //更新本地数据库
            InvitionInfo invitation = new InvitionInfo();
            invitation.setUser(new UserInfo(hxid));
            invitation.setStatus(InvitionInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitation);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送好友同意的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        /**
         * 联系人拒绝了好友邀请,不需要更新数据库
         * @param hxid
         */
        @Override
        public void onContactRefused(String hxid) {
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送好友同意的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };

    //监听群消息
    private final EMGroupChangeListener emGroupChangedListener = new EMGroupChangeListener() {
        //收到 群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String
                reason) {
            //更新数据
            InvitionInfo invitionInfo = new InvitionInfo();
            invitionInfo.setReason(reason);
            GroupInfo groupInfo = new GroupInfo(groupName, groupId, inviter);
            invitionInfo.setGroup(groupInfo);
            invitionInfo.setStatus(InvitionInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitionInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请通知
        @Override
        public void onApplicationReceived(String groupId, String groupName, String applicant,
                                          String reason) {
            //更新数据
            InvitionInfo invitionInfo = new InvitionInfo();
            invitionInfo.setReason(reason);
            GroupInfo groupInfo = new GroupInfo(groupName, groupId, applicant);
            invitionInfo.setGroup(groupInfo);
            invitionInfo.setStatus(InvitionInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitionInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被接受
        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
            //更新数据
            InvitionInfo invitionInfo = new InvitionInfo();
            GroupInfo groupInfo = new GroupInfo(groupName, groupId, accepter);
            invitionInfo.setGroup(groupInfo);
            invitionInfo.setStatus(InvitionInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitionInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播通知
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被拒绝
        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner,
                                          String reason) {
            //更新数据
            InvitionInfo invitionInfo = new InvitionInfo();
            GroupInfo groupInfo = new GroupInfo(groupName, groupId, decliner);
            invitionInfo.setReason(reason);
            invitionInfo.setGroup(groupInfo);
            invitionInfo.setStatus(InvitionInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitionInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播通知
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            //更新数据
            InvitionInfo invitionInfo = new InvitionInfo();
            GroupInfo groupInfo = new GroupInfo(groupId, groupId, inviter);
            invitionInfo.setReason(reason);
            invitionInfo.setGroup(groupInfo);
            invitionInfo.setStatus(InvitionInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitionInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播通知
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {
            //更新数据
            InvitionInfo invitionInfo = new InvitionInfo();
            GroupInfo groupInfo = new GroupInfo(groupId, groupId, inviter);
            invitionInfo.setReason(reason);
            invitionInfo.setGroup(groupInfo);
            invitionInfo.setStatus(InvitionInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitionInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播通知
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        //收到 群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String
                inviteMessage) {
            //更新数据
            InvitionInfo invitionInfo = new InvitionInfo();
            GroupInfo groupInfo = new GroupInfo(groupId, groupId, inviter);
            invitionInfo.setReason(inviteMessage);
            invitionInfo.setGroup(groupInfo);
            invitionInfo.setStatus(InvitionInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvition(invitionInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播通知
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
    };
}
