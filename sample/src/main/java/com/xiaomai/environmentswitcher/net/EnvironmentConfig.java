package com.xiaomai.environmentswitcher.net;

import com.xiaomai.environmentswitcher.annotation.Environment;
import com.xiaomai.environmentswitcher.annotation.Module;

/**
 * 环境配置类
 */
public class EnvironmentConfig {

    /**
     * 整个 App 的环境
     */
    @Module
    class App {
        @Environment(url = "https://gank.io/api/", isRelease = true, alias = "正式")
        private String online;
    }

    /**
     * 特殊模块 Music 的环境
     */
    @Module(alias = "音乐")
    class Music {
        @Environment(url = "https://www.codexiaomai.top/api/", isRelease = true, alias = "正式")
        private String online;

        @Environment(url = "http://test.codexiaomai.top/api/", alias = "测试")
        private String test;
    }

    /**
     * 特殊模块 News 的环境
     */
    @Module(alias = "新闻")
    class News {
        @Environment(url = "http://news/release/", isRelease = true, alias = "正式")
        private String release;

        @Environment(url = "http://news/test/", alias = "测试")
        private String test;

        @Environment(url = "http://news/test1/")
        private String test1;

        @Environment(url = "http://news/sandbox/", alias = "沙箱")
        private String sandbox;
    }
}
