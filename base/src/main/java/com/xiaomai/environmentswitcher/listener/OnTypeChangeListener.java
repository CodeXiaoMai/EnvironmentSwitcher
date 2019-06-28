package com.xiaomai.environmentswitcher.listener;

import com.xiaomai.environmentswitcher.bean.ModuleBean;

import java.util.List;

public interface OnTypeChangeListener {

    void onTypeChanged(List<ModuleBean> moduleBeans);
}
