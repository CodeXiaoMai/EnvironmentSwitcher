### Environment Switcher

**Environment Switcher** 是一个在 Android 的开发和测试阶段，用来一键切换环境的工具。

![demo.gif](https://upload-images.jianshu.io/upload_images/5275145-b3529a5f32884ab9.gif?imageMogr2/auto-orient/strip)

### 为什么要做这个工具

做这个工具是为了方便开发和测试人员在不重新打包的情况下快速切换环境。

相信大家都遇到过下面的某些场景。

- App 在开发、测试、上线等阶段需要频繁切换环境。
- 同一个App中的不同模块，在同一阶段需要配置不同的环境。
- 某些功能只能在指定环境下使用。
- 正在测试环境开发【课程评价】模块，这个模块要通过点击“首页->我的课程->课程详情->课程评价”进入，但是这时测试环境的【首页】挂了，导致后面的页面都无法进入，于是@Server端的同事修复，要一直等他们改好了，才能进入目标页面进行调试。
- 由于环境地址，在代码中写死，导致每次修改环境之后代码管理工具都会提示这行代码有改动，一旦疏忽就提交了，这对于代码管理是不严谨的。

### Environment Switcher 应运而生

Environment Switcher 就是为了解决以上问题而设计的，它具有以下几个特点：

- 安全，测试环境地址不泄漏
- 一键切换环境，使用方便
- 支持按模块配置与切换环境
- 与项目解偶
- 自动生成 `切换` `保存` `获取` 环境的逻辑代码
- 只需简单几步配置即可使用
- ......

### 使用方法

1. 配置项目的 build.gradle

	java 版
	
    ```
    dependencies {
        ...
        implementation 'com.xiaomai.environmentswitcher:environmentswitcher:1.1'
        debugAnnotationProcessor 'com.xiaomai.environmentswitcher:environmentswitcher-compiler:1.0'
		releaseAnnotationProcessor 'com.xiaomai.environmentswitcher:environmentswitcher-compiler-release:1.0'
    }
    ```
    
    kotlin 版

    ```
    apply plugin: 'kotlin-kapt'
    ...
    dependencies {
        ...
        implementation 'com.xiaomai.environmentswitcher:environmentswitcher:1.1'
		kaptDebug 'com.xiaomai.environmentswitcher:environmentswitcher-compiler:1.0'
        kaptRelease 'com.xiaomai.environmentswitcher:environmentswitcher-compiler-release:1.0'
    }
    ```

2. 编写 EnvironmentConfig 文件
    
    例如：项目中包含两个模块，分别是 Music、News，而且每个模块的地址都不同。
    
    ```
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
    ```

3. 点击菜单栏中的 “Build” -> “Rebuild Project”，等待编译完成。

4. 在你的 App 中添加一个切换环境的入口，这个入口只在 debug 版显示。例如：在“我的”页面中。
    
    ```
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ...
        if (!BuildConfig.DEBUG) {
            // only show in debug
            findViewById(R.id.bt_switch_environment).setVisibility(View.GONE);
            return;
        }
        
        findViewById(R.id.bt_switch_environment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // entrance of switch environment
                EnvironmentSwitchActivity.launch(getContext());
            }
        });
    }
    ```

5. 获取当前模块的地址：
    
    ```
    EnvironmentSwitcher.getAppEnvironment(getApplication(), BuildConfig.DEBUG);
    EnvironmentSwitcher.getMusicEnvironment(getApplication(), BuildConfig.DEBUG);
    EnvironmentSwitcher.getNewsEnvironment(getApplication(), BuildConfig.DEBUG);
    ```