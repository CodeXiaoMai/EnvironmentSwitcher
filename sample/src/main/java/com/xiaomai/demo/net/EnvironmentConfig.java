package com.xiaomai.demo.net;


import com.xiaomai.environmentswitcher.annotation.Environment;
import com.xiaomai.environmentswitcher.annotation.Module;

/**
 * 环境配置类</br>
 *
 * ⚠ 建议不要引用该类中的任何子类和成员变量️，一但引用了非正式环境的属性，打包时混淆工具就不会移除该类，导致测试地址泄漏。</br>
 * 而 Environment Switcher 在编译 Release 版本时，会自动隐藏测试环境地址。</br></br>
 *
 * 建议将该类中所有被 {@link Module} 和 {@link Environment} 修饰的类或成员变量用 private 修饰，</br>
 * Environment Switcher 会在编译期间自动生成相应的 Module_XX 和 Environment_XX 静态常量。</br>
 * 例如：通过 EnvironmentSwitcher.MODULE_APP 就可以获取到 App 模块下相应的所有环境</br>
 */
public class EnvironmentConfig {

    /**
     * 整个 App 的环境
     */
    @Module
    private class App {
        @Environment(url = "https://wanandroid.com/", isRelease = true, alias = "正式")
        private String online;
    }

    /**
     * 特殊模块 Music 的环境
     */
    @Module(alias = "音乐")
    private class Music {
        @Environment(url = "https://www.xiaomai.com/api/", isRelease = true, alias = "正式")
        private String online;

        @Environment(url = "https://test.xiaomai.com/api/", alias = "测试")
        private String test;
    }

    /**
     * 特殊模块 News 的环境
     */
    @Module(alias = "新闻")
    private class News {
        @Environment(url = "http://news/release/", isRelease = true, alias = "正式")
        private String release;

        @Environment(url = "http://news/test/", alias = "测试")
        private String test;

        @Environment(url = "http://news/test1/")
        private String test1;

        @Environment(url = "http://news/sandbox/", alias = "沙箱")
        private String sandbox;
    }

    /**
     * 分享模块
     */
    @Module(alias = "分享")
    private class Share {
        @Environment(url = "http://www.share.com", isRelease = true, alias = "正式")
        private String online;

        @Environment(url = "http://test.share.com", alias = "测试")
        private String test;
    }
}
