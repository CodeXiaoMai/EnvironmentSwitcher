### Environment Switcher

**Environment Switcher** 是一个在 Android 的开发和测试阶段，用来一键切换环境的工具。

> **如果你觉得这个工具对你有用，随手给个 Star，让我知道它是对你有帮助的，我会继续更新和维护它。**

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

- 只需简单几步配置即可使用
- 安全，测试环境地址不泄漏
- 一键切换环境，使用方便
- 支持按模块配置与切换环境
- 支持环境切换通知回调
- 自动生成 `切换` `保存` `获取` 环境的逻辑代码
- 与项目解耦
- ......

### 使用方法

1. 配置项目的 build.gradle

	java 版
	
    ```
    dependencies {
        ...
        implementation 'com.xiaomai.environmentswitcher:environmentswitcher:1.3'
        debugAnnotationProcessor 'com.xiaomai.environmentswitcher:environmentswitcher-compiler:1.3'
        releaseAnnotationProcessor 'com.xiaomai.environmentswitcher:environmentswitcher-compiler-release:1.3'
    }
    ```
    
    kotlin 版

    ```
    apply plugin: 'kotlin-kapt'
    ...
    dependencies {
        ...
        implementation 'com.xiaomai.environmentswitcher:environmentswitcher:1.3'
        kaptDebug 'com.xiaomai.environmentswitcher:environmentswitcher-compiler:1.3'
        kaptRelease 'com.xiaomai.environmentswitcher:environmentswitcher-compiler-release:1.3'
    }
    ```

2. 编写 EnvironmentConfig 文件

    **这个类是 Environment Switcher 依赖的核心代码，所有获取、修改环境的逻辑代码都会依赖这个类中被 `@Module` 和 `@Environment` 两个注解标记的类和属性自动生成。**

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

    - @Module
被它修饰的类或接口表示一个模块，编译时会自动生成相应模块的 `getXXEnvironment()` 和 `setXXEnvironment()` 方法。一个被 `@Module` 修饰的类中，可以有 n (n>0) 个被 `@Environment` 修饰的属性，表示该模块中有 n 种环境。

      例如：上面的代码中，有三个类被 `@Module` 修饰，意味着有三个模块，其中 App 模块中，只有一个属性被 `@Environment` 修饰，表示该模块只有一种环境；而 Music 和 News 模块分别有 2 种和 4 种环境。

      此外 `@Module` 还有一个可选属性 `alias` ，用来指定该模块的别名。该值默认为空字符串。这个属性的主要目的是在切换环境 UI 页面显示中文名称。例如：Music 和 News 模块在切换环境页面中就会分别显示 “音乐” 和 “新闻”。

      > 注：如果你的项目中所有模块共用同一个 Host 地址，那么只需配置一个 Module 就可以了。

    - @Environment
被它修饰的属性表示一个环境，必须指定 `url` 的值，此外还有两个可选属性：`isRelease` 和 `alias`。
       - isRelease 是一个 boolean 型的属性，默认为 false，当值为 true 时，它就是所在 Module 的默认环境，以及 App 正式发布时的环境。**一个 Module 中必须有且只有一个 Environment 的 isRelease 的值为 true，否则编译会失败。** 
      - alias 和 `@Module` 中的 alias 相似，用于在切换环境的UI页面展示该环境的名字，该值默认为空字符串，如果给它指定非空字符串，则环境的名字就被指定为 `alias` 的值。

       例如：Music 模块中有两种环境分别是 online（正式）和 test （测试），因为 online 的 isRelease = true，所以它就是默认环境和App 正式发布时的环境。

      > **再次强调**：一个 Module 中必须有且只有一个 Environment 的 isRelease 的值为 true，否则编译会失败。 
3. 点击菜单栏中的 “Build” -> “Rebuild Project”，等待编译完成。

    到这里整个配置就算完成了，接下来就可以在项目中愉快的获取相应模块的环境地址了。

### 添加入口

手动切换环境当然要有一个页面，这个页面 Environment Switcher 已经自动集成了，只需要添加一个入口跳转即可（这个入口只在 Debug 测试等内部版显示）。

例如：在“我的”页面中。
    
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

你可以使用 Environment Switcher 已经提供的 `EnvironmentSwitchActivity.launch(getContext())` 方法启动；当然你也可以通过 `startActivity(new Intent(getContext(), EnvironmentSwitchActivity.class))` 启动，看个人喜好了。

### 获取相应模块的环境地址：


```
EnvironmentSwitcher.getAppEnvironment(getApplication(), BuildConfig.DEBUG);
EnvironmentSwitcher.getMusicEnvironment(getApplication(), BuildConfig.DEBUG);
EnvironmentSwitcher.getNewsEnvironment(getApplication(), BuildConfig.DEBUG);
```

这里需要注意的是获取相应模块的地址需要两个参数，第一个就是一个 Context 不用解释，因为 Environment Switcher 是用 SharedPreferences 进行存储数据的。第二个参数是一个 boolean 型的值，如果为 true 表示当前为 Debug 或测试等内部使用版本，此时获取到的地址是我们手动切换保存的地址；而如果为 false 表示当前为要发布给用户使用的版本，此时获取到的地址为我们在 @Environment 中指定 isRelease = true 的地址，手动切换的环境地址不再生效。

### [更多使用介绍可参考Demo](https://github.com/CodeXiaoMai/EnvironmentSwitcher) 。

Demo 中有 Environment Switcher 结合 Retrofit 使用的详细实现过程。

![Environment Switcher](https://upload-images.jianshu.io/upload_images/5275145-b3529a5f32884ab9.gif?imageMogr2/auto-orient/strip)