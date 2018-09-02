package com.xiaomai.environmentswitcher.listener;

import com.xiaomai.environmentswitcher.bean.EnvironmentBean;
import com.xiaomai.environmentswitcher.bean.ModuleBean;

public interface OnEnvironmentChangeListener {

    void onEnvironmentChange(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment);
}