[![API](https://img.shields.io/badge/API-7%2B-brightgreen.svg)](https://android-arsenal.com/api?level=7)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

### [English Document](https://github.com/CodeXiaoMai/EnvironmentSwitcher/blob/master/README.md)

# Environment Switcher

Environment Switcher 是一个在 Android 的开发和测试阶段，运用 Java 注解、APT、反射、混淆等原理来一键切换环境的工具。

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

- 配置简单
- 安全，不泄漏测试环境地址
- 不用重新打包即可一键切换环境
- 支持按模块配置与切换环境
- 支持环境切换通知回调
- 自动生成 `切换` `保存` `获取` 环境的逻辑代码
- 与项目解耦
- ......

### 为什么不用 Gradle

看到这里你可能会想，这些功能我用 Gradle 就能搞定了，为什么要用 Environment Switcher 呢？别着急，下面我们来比较一下 Environment Switcher 和 Gradle。

|比较内容|Environment Switcher|Gradle  Application Id 不同| Gradle Application Id 相同 |
|:-:|:--:|:--:|:--:|
|运行时切换环境|✔️|✖️|✖️|
|切换环境回调|✔️|✖️|✖️|
|切换环境逻辑|自动生成|需要自己实现|需要自己实现|
|n 套环境打包数量| 1个 | n个 | n个|
|多套环境同时安装|✔️|✔️|✖️|
|支付等SDK包名校验|✔️|✖️|✔️|
|多模块环境配置|✔️|✔️|✔️|
|测试环境不泄露|✔️|✔️|✔️|
|……|——|——|——|

这里就先列举这么多，仅 `运行时切换环境` 、`打包数量`、`切换环境回调` 这几个特点就比 Gradle 方便很多，而且 Environment Switcher 的接入成本也很低。是不是想试一试了？

### 使用方法

最新版本：

module|environmentswitcher|environmentswitcher-compiler|environmentswitcher-compiler-release
:---:|:---:|:---:|:---:
version|[ ![Download](https://api.bintray.com/packages/xiaomai/maven/environmentswitcher/images/download.svg) ](https://bintray.com/xiaomai/maven/environmentswitcher/_latestVersion) | [ ![Download](https://api.bintray.com/packages/xiaomai/maven/environmentswitcher-compiler/images/download.svg) ](https://bintray.com/xiaomai/maven/environmentswitcher-compiler/_latestVersion) | [ ![Download](https://api.bintray.com/packages/xiaomai/maven/environmentswitcher-compiler-release/images/download.svg) ](https://bintray.com/xiaomai/maven/environmentswitcher-compiler-release/_latestVersion)


1. 配置项目的 build.gradle

	- java 版
	
	    ```
	    dependencies {
	        ...
	        implementation "com.xiaomai.environmentswitcher:environmentswitcher:$version"
	        debugAnnotationProcessor "com.xiaomai.environmentswitcher:environmentswitcher-compiler:$version"
	        releaseAnnotationProcessor "com.xiaomai.environmentswitcher:environmentswitcher-compiler-release:$version"
	    }
	    ```
    
    - kotlin 版

	    ```
	    apply plugin: 'kotlin-kapt'
	    ...
	    dependencies {
	        ...
	        implementation "com.xiaomai.environmentswitcher:environmentswitcher:$version"
	        kaptDebug "com.xiaomai.environmentswitcher:environmentswitcher-compiler:$version"
	        kaptRelease "com.xiaomai.environmentswitcher:environmentswitcher-compiler-release:$version"
	    }
	    ```

2. 编写 EnvironmentConfig 文件

    **这个类是 Environment Switcher 依赖的核心代码，所有获取、修改环境的逻辑代码都会依赖这个类中被 `@Module` 和 `@Environment` 两个注解标记的类和属性自动生成。**
    
    > 注意：如果你的项目中使用了 Kotlin，请使用 Java 语言编写 EnvironmentConfig，就像在 GreenDao 中必须使用 Java 语言编写 Entity 类一样。

    ```
    /**
     * 环境配置类</br>
     *
     * ⚠ 建议不要引用该类中的任何子类和成员变量，一但引用了非正式环境的属性，打包时混淆工具就不会移除该类，导致测试地址泄漏。</br>
     * Environment Switcher 在编译 Release 版本时，会自动隐藏测试环境地址。</br></br>
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
            @Environment(url = "https://gank.io/api/", isRelease = true, alias = "正式")
            private String online;
        }
    
        /**
         * 特殊模块 Music 的环境
         */
        @Module(alias = "音乐")
        private class Music {
            @Environment(url = "https://www.codexiaomai.top/api/", isRelease = true, alias = "正式")
            private String online;
    
            @Environment(url = "http://test.codexiaomai.top/api/", alias = "测试")
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

			例如：Music 模块中有两种环境分别是 online（正式）和 test （测试），因为 online 的 isRelease = true，所以它就是默认环境和App 正式发布时的环境。

      - alias 和 `@Module` 中的 alias 相似，用于在切换环境的UI页面展示该环境的名字，该值默认为空字符串，如果给它指定非空字符串，则环境的名字就被指定为 `alias` 的值。

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
String appEnvironment = EnvironmentSwitcher.getAppEnvironment(this, BuildConfig.DEBUG);
String musicEnvironment = EnvironmentSwitcher.getMusicEnvironment(this, BuildConfig.DEBUG);
String newsEnvironment = EnvironmentSwitcher.getNewsEnvironment(this, BuildConfig.DEBUG);
```

### 获取相应模块的环境实体类(since 1.4)：

```
EnvironmentBean appEnvironmentBean = EnvironmentSwitcher.getAppEnvironmentBean(this, BuildConfig.DEBUG);
EnvironmentBean musicEnvironmentBean = EnvironmentSwitcher.getMusicEnvironmentBean(this, BuildConfig.DEBUG);
EnvironmentBean newsEnvironmentBean = EnvironmentSwitcher.getNewsEnvironmentBean(this, BuildConfig.DEBUG);
```

这里需要注意的是获取相应模块的地址需要两个参数，第一个就是一个 Context 不用解释，因为 Environment Switcher 是用 SharedPreferences 进行存储数据的。第二个参数是一个 boolean 型的值，如果为 true 表示当前为 Debug 或测试等内部使用版本，此时获取到的地址是我们手动切换保存的地址；而如果为 false 表示当前为要发布给用户使用的版本，此时获取到的地址为我们在 @Environment 中指定 isRelease = true 的地址，手动切换的环境地址不再生效。

### 添加监听事件

Environment Switcher 支持切换环境回调，你可以通过以下方法添加，需要注意的是不要忘记**在不需要监听环境切换事件时移除监听事件**。

```
public class MainActivity extends AppCompatActivity implements OnEnvironmentChangeListener{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加监听事件
        EnvironmentSwitcher.addOnEnvironmentChangeListener(this);
    }

    @Override
    public void onEnvironmentChanged(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
        Log.e(TAG, module.getName() + "由" + oldEnvironment.getName() + "环境，Url=" + oldEnvironment.getUrl()
                + ",切换为" + newEnvironment.getName() + "环境，Url=" + newEnvironment.getUrl());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除监听事件
        EnvironmentSwitcher.removeOnEnvironmentChangeListener(this);
    }
}
```

### 切换SDK开发环境

我们在项目中一般会依赖第三方提供的SDK，而且这些SDK也会提供测试环境，如果要在App内切换环境，使用上面的方法就不行了。那该怎么办呢？

例如我们的“直播”模块是引用的SDK，我们可以这样做：

1. 首先在 EnvironmentConfig.java 中配置"直播"模块

   ```
   public class EnvironmentConfig {
        @Module(alias = "直播")
        private class Live {
            @Environment(url = "online", isRelease = true, alias = "正式")
            private String online;

            @Environment(url = "test", alias = "测试")
            private String test;
        }
   }
   ```
   > url 在这里只是用来区分环境，不用为真实的 url，但要保证同一模块中每个环境的 url 不同。

2. 在 Application 中添加监听

   ```
   EnvironmentSwitcher.addOnEnvironmentChangeListener(new OnEnvironmentChangeListener() {
        @Override
        public void onEnvironmentChanged(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
           if (module.equals(EnvironmentSwitcher.MODULE_LIVE)) {
               if (newEnvironment.equals(EnvironmentSwitcher.LIVE_ONLINE_ENVIRONMENT)) {
                 // 调用 SDK 切换环境的方法，正式环境
               } else if (newEnvironment.equals(EnvironmentSwitcher.LIVE_TEST_ENVIRONMENT)) {
                 // 调用 SDK 切换环境的方法，测试环境
               }
           }
        }
   });
   ```

	> 利用 Environment Switcher 的环境切换回调，实现切换 SDK 环境。

## 附

Environment Switcher 除了可以用来做环境切换工具，还可以做其他的可配置开关，例如：打印日志的开关。（ps：这不是 Environment Switcher 设计时的目标功能，算是一个小彩蛋吧！）

```
@Module(alias = "日志")
private class Log {
    @Environment(url = "false", isRelease = true, alias = "关闭日志")
    private String closeLog;
    @Environment(url = "true", alias = "开启日志")
    private String openLog;
}

public void loge(Context context, String tag, String msg) {
    if (EnvironmentSwitcher.getLogEnvironmentBean(context, BuildConfig.DEBUG)
            .equals(EnvironmentSwitcher.LOG_OPENLOG_ENVIRONMENT)) {
        android.util.Log.e(tag, msg);
    }
}
```

当然这里只是举一个简单的例子，Environment Switcher 能做的远不止这些，更多功能欢迎大家动手尝试。

好了，关于Environment Switcher 的介绍就到此为止吧，[更多使用介绍可参考Demo](https://github.com/CodeXiaoMai/EnvironmentSwitcher) ,Demo 中有 Environment Switcher 结合 Retrofit 使用的详细实现过程。

![Environment Switcher](https://upload-images.jianshu.io/upload_images/5275145-b3529a5f32884ab9.gif?imageMogr2/auto-orient/strip)

## 划重点

嘿嘿，第一次做开源工具，如果喜欢 Environment Switcher 欢迎随意打赏或 [Star](https://github.com/CodeXiaoMai/EnvironmentSwitcher) 。

- 支付宝

![支付宝](https://upload-images.jianshu.io/upload_images/5275145-db3ad0d5c0ea1b4c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 微信
 
![微信](https://upload-images.jianshu.io/upload_images/5275145-8bd8dff563306741.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)

## 更新日志

### 2018.11.9

**发布v1.5.2版**

- [bugfix] [#8 当前正在使用的环境地址发生变化时，获取环境时会发生NPE](https://github.com/CodeXiaoMai/EnvironmentSwitcher/issues/8)

**发布v1.5.1版**

- [bugfix] [#7 当环境的URL相同时，在切换环境页面选择的时候，会认为是同一个实体，一起被选中](https://github.com/CodeXiaoMai/EnvironmentSwitcher/issues/7)

### 2018.9.6

**发布v1.5版**

- [bugfix] [#3 fix bug Can't get the right environment in the callback](https://github.com/CodeXiaoMai/EnvironmentSwitcher/issues/3)
- [update] “onEnvironmentChange” 方法名改为 “onEnvironmentChange`d`”

### 2018.9.2 

**发布v1.4版**

- [new] EnvironmentSwitcher 中增加 ModuleBean 和 EnvironmentBean 静态常量
- [update] OnEnvironmentChangeListener 接口中的回调方法
	- 1.3 之前：

		```
		EnvironmentSwitcher.addOnEnvironmentChangeListener(new OnEnvironmentChangeListener() {
			@Override
			public void onEnvironmentChange(String module, String oldUrl, String newUrl) {
				Log.e(TAG, module + "由Url=" + oldUrl + ",切换为" + "Url=" + newUrl);
			}
		});
		```

	- 1.4 改为：

		```
		EnvironmentSwitcher.addOnEnvironmentChangeListener(new OnEnvironmentChangeListener() {
			@Override
			public void onEnvironmentChange(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
				Log.e(TAG, module.getName() + "由" + oldEnvironment.getName() + "环境，Url=" + oldEnvironment.getUrl() + ",切换为" + newEnvironment.getName() + "环境，Url=" + newEnvironment.getUrl());
			}
		});
		```

# 原理解析

[点击查看 Environment Switcher 原理解析](https://www.jianshu.com/p/190710a846b9)

# LICENSE

> Copyright (c) 2018 XiaoMai
>
> Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.