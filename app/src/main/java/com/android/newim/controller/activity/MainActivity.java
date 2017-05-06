package com.android.newim.controller.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.newim.R;
import com.android.newim.controller.fragment.ChartFragment;
import com.android.newim.controller.fragment.ContractListFragment;
import com.android.newim.controller.fragment.SettingFragment;

public class MainActivity extends FragmentActivity {

    private FrameLayout flMain;
    private RadioGroup rgMain;
    private RadioButton rbMainChat;
    private RadioButton rbMainContact;
    private RadioButton rbMainSetting;
    private ChartFragment chartFragment;
    private ContractListFragment contractListFragment;
    private SettingFragment settingFragment;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-04-22 19:59:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        flMain = (FrameLayout) findViewById(R.id.fl_main);
        rgMain = (RadioGroup) findViewById(R.id.rg_main);
        rbMainChat = (RadioButton) findViewById(R.id.rb_main_chat);
        rbMainContact = (RadioButton) findViewById(R.id.rb_main_contact);
        rbMainSetting = (RadioButton) findViewById(R.id.rb_main_setting);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-04-22 19:59:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initData();
        initListener();
    }

    private void initListener() {
        //RadioGroup的切换事件相应
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;
                switch (checkedId) {
                    case R.id.rb_main_chat://会话
                        fragment = chartFragment;
                        break;
                    case R.id.rb_main_contact://联系人
                        fragment = contractListFragment;
                        break;
                    case R.id.rb_main_setting://设置
                        fragment = settingFragment;
                        break;
                    default:
                        break;
                }
                //实现fragment的切换
                switchFragment(fragment);
            }
        });
        rgMain.check(R.id.rb_main_chat);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

    private void initData() {
        chartFragment = new ChartFragment();
        contractListFragment = new ContractListFragment();
        settingFragment = new SettingFragment();
    }
}
