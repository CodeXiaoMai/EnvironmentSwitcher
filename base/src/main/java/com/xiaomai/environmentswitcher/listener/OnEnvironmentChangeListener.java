package com.xiaomai.environmentswitcher.listener;

public interface OnEnvironmentChangeListener {
    void onEnvironmentChange(String module, String oldUrl, String newUrl);
}