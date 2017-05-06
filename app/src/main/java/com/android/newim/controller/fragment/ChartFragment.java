package com.android.newim.controller.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.android.newim.controller.activity.ChartActivity;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

/**
 * Created by shengli.zhang on 2017/4/22.
 */

public class ChartFragment extends EaseConversationListFragment {
    @Override
    protected void initView() {
        super.initView();
        //点击跳转到绘画列表页面
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId());
                //如果是群聊，还需传入会话类型，默认是单聊
                if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                }
                startActivity(intent);
            }
        });

        conversationList.clear();//清空稽核数据
        //设置会话消息监听
        EMClient.getInstance().chatManager().addMessageListener(emMessaageListtener);
    }

    private EMMessageListener emMessaageListtener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> list) {
            //设置数据
            EaseUI.getInstance().getNotifier().onNewMesg(list);

            //刷新页面
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };
}
