package com.xiaomai.environmentswitcher.listener;

import com.xiaomai.environmentswitcher.bean.EnvironmentBean;
import com.xiaomai.environmentswitcher.bean.ModuleBean;

/**
 * 当某个模块的环境更改时要调用的回调的接口定义。
 * <p>
 * Interface definition for a callback to be invoked when a Environment of a module is changed.
 */
public interface OnEnvironmentChangeListener {

    /**
     * 在模块的环境发生变化时调用。
     * <p>
     * Called when a environment of a module has been changed.
     *
     * @param module         环境发生变化的模块
     *                       <p>
     *                       Environment changing module
     * @param oldEnvironment 该模块的旧环境
     *                       <p>
     *                       The old environment of the module
     * @param newEnvironment 该模块的最新环境
     *                       <p>
     *                       The latest environment for this module
     */
    void onEnvironmentChanged(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment);
}