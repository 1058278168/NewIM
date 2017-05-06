package com.android.newim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.newim.R;
import com.android.newim.model.Model;
import com.android.newim.model.bean.InvitionInfo;
import com.android.newim.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shengli.zhang on 2017/4/23.
 */

public class InviteAdapter extends BaseAdapter {

    private Context mContext;
    private List<InvitionInfo> mInvitionInfos = new ArrayList<>();
    private OnInviteListener mOnInviteListener;

    public InviteAdapter(Context context, OnInviteListener onInviteListener) {
        mContext = context;
        mOnInviteListener = onInviteListener;
    }

    //刷新adapter的数据
    public void refresh(List<InvitionInfo> invitionInfos) {
        if (invitionInfos != null && invitionInfos.size() >= 0) {
            mInvitionInfos.clear();
            mInvitionInfos.addAll(invitionInfos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvitionInfos == null ? 0 : mInvitionInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvitionInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建或获取ViewHolder对象
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_invite, null);
            viewHolder.tv_invite_name = (TextView) convertView.findViewById(R.id.tv_invite_name);
            viewHolder.tv_invite_reason = (TextView) convertView.findViewById(R.id
                    .tv_invite_reason);
            viewHolder.bt_invite_accept = (Button) convertView.findViewById(R.id.bt_invite_accept);
            viewHolder.bt_invite_reject = (Button) convertView.findViewById(R.id.bt_invite_reject);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //获取当前item数据
        final InvitionInfo invitionInfo = mInvitionInfos.get(position);
        //显示当前item数据
        UserInfo user = invitionInfo.getUser();
        if (user != null) {//联系人邀请
            viewHolder.tv_invite_name.setText(user.getName());
            //接受和拒绝按钮不显示
            viewHolder.bt_invite_accept.setVisibility(View.GONE);
            viewHolder.bt_invite_reject.setVisibility(View.GONE);
            if (invitionInfo.getStatus() == InvitionInfo.InvitationStatus.NEW_INVITE) {//新邀请
                if (invitionInfo.getReason() == null) {
                    viewHolder.tv_invite_reason.setText("添加好友");
                } else {
                    viewHolder.tv_invite_reason.setText(invitionInfo.getReason());
                }
                viewHolder.bt_invite_accept.setVisibility(View.VISIBLE);
                viewHolder.bt_invite_reject.setVisibility(View.VISIBLE);
            } else if (invitionInfo.getStatus() == InvitionInfo.InvitationStatus.INVITE_ACCEPT)
            {//接受邀请
                if (invitionInfo.getReason() == null) {
                    viewHolder.tv_invite_reason.setText("接受邀请");
                } else {
                    viewHolder.tv_invite_reason.setText(invitionInfo.getReason());
                }
            } else if (invitionInfo.getStatus() == InvitionInfo.InvitationStatus
                    .INVITE_ACCEPT_BY_PEER) {//邀请被接收
                if (invitionInfo.getReason() == null) {
                    viewHolder.tv_invite_reason.setText("邀请被接受");
                } else {
                    viewHolder.tv_invite_reason.setText(invitionInfo.getReason());
                }
            }
            viewHolder.bt_invite_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onAccept(invitionInfo);
                }
            });
            viewHolder.bt_invite_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onReject(invitionInfo);
                }
            });
        } else {
            //群邀请
            //邀请人
            viewHolder.tv_invite_name.setText(invitionInfo.getGroup().getInvitePerson());
            //拒绝和接受按钮不显示
            viewHolder.bt_invite_reject.setVisibility(View.GONE);
            viewHolder.bt_invite_accept.setVisibility(View.GONE);
            //原因
            switch (invitionInfo.getStatus()) {
                case GROUP_ACCEPT_APPLICATION:
                    viewHolder.tv_invite_reason.setText("您接受了群申请");
                    break;
                case GROUP_INVITE_ACCEPTED:
                    viewHolder.tv_invite_reason.setText("您的群邀请已被接受");
                    break;
                case GROUP_APPLICATION_DECLINED:
                    viewHolder.tv_invite_reason.setText("您的群申请已被拒绝");
                    break;
                case GROUP_INVITE_DECLINED:
                    viewHolder.tv_invite_reason.setText("您的群邀请已被拒绝");
                    break;
                case GROUP_ACCEPT_INVITE:
                    viewHolder.tv_invite_reason.setText("您接受了群邀请");
                    break;
                case GROUP_REJECT_INVITE:
                    viewHolder.tv_invite_reason.setText("您拒绝了群邀请");
                    break;
                case GROUP_APPLICATION_ACCEPTED:
                    viewHolder.tv_invite_reason.setText("您的群申请已被接受");
                    break;
                case GROUP_REJECT_APPLICATION:
                    viewHolder.tv_invite_reason.setText("您拒绝了群申请");
                    break;
                case NEW_GROUP_INVITE:
                    viewHolder.bt_invite_accept.setVisibility(View.VISIBLE);
                    viewHolder.bt_invite_reject.setVisibility(View.VISIBLE);
                    viewHolder.tv_invite_reason.setText("您收到了新的群邀请信息");
                    //接受群邀请
                    viewHolder.bt_invite_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteAccept(invitionInfo);
                        }
                    });
                    //拒绝群邀请
                    viewHolder.bt_invite_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteReject(invitionInfo);
                        }
                    });
                    break;
                case NEW_GROUP_APPLICATION:
                    viewHolder.bt_invite_accept.setVisibility(View.VISIBLE);
                    viewHolder.bt_invite_reject.setVisibility(View.VISIBLE);
                    viewHolder.tv_invite_reason.setText("您收到了新的群申请信息");
                    //接受群申请
                    viewHolder.bt_invite_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationAccept(invitionInfo);
                        }
                    });
                    //拒绝群申请
                    viewHolder.bt_invite_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationReject(invitionInfo);
                        }
                    });
                    break;
                default:
                    break;
            }

        }
        //返回view
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_invite_name, tv_invite_reason;
        private Button bt_invite_accept, bt_invite_reject;
    }

    public interface OnInviteListener {
        // 联系人接受按钮的点击事件
        void onAccept(InvitionInfo invationInfo);

        // 联系人拒绝按钮的点击事件
        void onReject(InvitionInfo invationInfo);

        // 接受群邀请按钮处理
        void onInviteAccept(InvitionInfo invationInfo);

        // 拒绝群邀请按钮处理
        void onInviteReject(InvitionInfo invationInfo);

        // 接受群申请按钮处理
        void onApplicationAccept(InvitionInfo invationInfo);

        // 拒绝群申请按钮处理
        void onApplicationReject(InvitionInfo invationInfo);
    }
}
