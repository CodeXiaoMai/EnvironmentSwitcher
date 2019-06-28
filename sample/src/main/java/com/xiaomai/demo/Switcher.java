package com.xiaomai.demo;

import android.content.Context;
import android.text.TextUtils;

import com.xiaomai.environmentswitcher.EnvironmentSwitcher;
import com.xiaomai.environmentswitcher.bean.EnvironmentBean;
import com.xiaomai.environmentswitcher.bean.ModuleBean;
import com.xiaomai.environmentswitcher.listener.OnTypeChangeListener;

import java.util.ArrayList;
import java.util.List;

public class Switcher {

    private List<ModuleBean> moduleBeanList = new ArrayList<>();

    private void init() {
        ModuleBean moduleBean = new ModuleBean("app", "test");

        EnvironmentBean environmentBean = new EnvironmentBean("test", "test", "测试", moduleBean);

        ArrayList<EnvironmentBean> environmentBeans = new ArrayList<>();

        environmentBeans.add(environmentBean);

        moduleBean.setEnvironments(environmentBeans);

    }

    private OnTypeChangeListener listener;

    private void setType(Context context, String type) {
        for (ModuleBean bean : moduleBeanList) {
            for (EnvironmentBean environment : bean.getEnvironments()) {
                if (TextUtils.equals(type, environment.getName())) {
                    if (bean.getName().equals("app")) {
                        EnvironmentSwitcher.setAppEnvironment(context, environment);
                    }
                    break;
                }
            }
        }
        listener.onTypeChanged(moduleBeanList);
    }
}