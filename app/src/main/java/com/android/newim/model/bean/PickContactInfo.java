package com.android.newim.model.bean;

/**
 * Created by shengli.zhang on 2017/4/26.
 */

public class PickContactInfo {
    private UserInfo user;
    private boolean isChecked;

    public PickContactInfo() {
    }

    public PickContactInfo(UserInfo user, boolean isChecked) {
        this.user = user;
        this.isChecked = isChecked;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
