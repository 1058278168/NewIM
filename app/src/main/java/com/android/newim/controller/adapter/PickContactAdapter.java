package com.android.newim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.newim.R;
import com.android.newim.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shengli.zhang on 2017/4/26.
 */

public class PickContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();
    private List<String> mExistMembe = new ArrayList<>();

    public PickContactAdapter(Context context, List<PickContactInfo>
            picks, List<String> existMember) {
        mContext = context;
        if (picks != null && picks.size() >= 0) {
            mPicks.clear();
            mPicks.addAll(picks);
        }
        mExistMembe.clear();
        mExistMembe.addAll(existMember);
    }

    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_pick, null);
            holder.cb_pick = (CheckBox) convertView.findViewById(R.id.cb_pick);
            holder.tv_pick_name = (TextView) convertView.findViewById(R.id.tv_pick_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //获取当前item数据
        PickContactInfo pickContactInfo = mPicks.get(position);
        //显示数据
        holder.cb_pick.setChecked(pickContactInfo.isChecked());
        holder.tv_pick_name.setText(pickContactInfo.getUser().getName());

        if (mExistMembe.contains(pickContactInfo.getUser().getHxid())) {
            holder.cb_pick.setChecked(true);
            pickContactInfo.setChecked(true);
        }
        return convertView;
    }

    private class ViewHolder {
        private CheckBox cb_pick;
        private TextView tv_pick_name;
    }

    //获取选择的联系人
    public List<String> getPickContacts() {
        List<String> picks = new ArrayList<>();
        for (PickContactInfo pick : mPicks) {
            if (pick.isChecked()) {
                picks.add(pick.getUser().getName());
            }
        }
        return picks;
    }
}
