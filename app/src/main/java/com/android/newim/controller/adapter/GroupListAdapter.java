package com.android.newim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.newim.R;
import com.android.newim.controller.activity.GroupListActivity;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import static com.android.newim.R.id.tv_grouplist_name;

/**
 * Created by shengli.zhang on 2017/4/24.
 */

public class GroupListAdapter extends BaseAdapter {
    private Context mContext;
    private List<EMGroup> mGroup = new ArrayList<>();//EMGroup环信提供的群组信息

    public GroupListAdapter(Context context) {
        mContext = context;
    }

    //通过刷新的方法显示群组信息
    public void refresh(List<EMGroup> groups) {
        if (groups != null && groups.size() >= 0) {
            mGroup.clear();//先清空
            //临时保存
            mGroup.addAll(groups);
            //提示适配器更新数据
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mGroup == null ? 0 : mGroup.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroup.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建或获取viewholder
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_grouplist, null);
            holder.tv_grouplist_name = (TextView) convertView.findViewById(tv_grouplist_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //获取当前item数据
        EMGroup emGroup = mGroup.get(position);
        //展示数据
        holder.tv_grouplist_name.setText(emGroup.getGroupName());
        //返回数据
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_grouplist_name;
    }
}
