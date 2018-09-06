
[![API](https://img.shields.io/badge/API-7%2B-brightgreen.svg)](https://android-arsenal.com/api?level=7) 

### [English Document](https://github.com/CodeXiaoMai/EnvironmentSwitcher/blob/master/README-EN.md)

## Environment Switcher 介绍

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

## Environment Switcher 原理解析

> 以下内容建议在使用 Environment Switcher 之后再看。

用过 Environment Switcher 的人都知道，只需按应用中的模块配置环境，Environment Switcher 就会自动生成一系列方法。例如，下面的代码就是配置 Music 模块的环境：


```
public class EnvironmentConfig {
    @Module(alias = "音乐")
    private class Music {
        @Environment(url = "https://www.codexiaomai.top/api/", isRelease = true, alias = "正式")
        private String online;

        @Environment(url = "http://test.codexiaomai.top/api/", alias = "测试")
        private String test;
    }
}
```

只需要写这 10 行代码（包括括号和空行）编译之后，Environment Switcher 就会自动生成下面包含`切换／获取环境`、`添加／移除环境切换监听事件`、`获取所有模块／环境` 等功能在内的不到 100 行代码。

```
public final class EnvironmentSwitcher {
    private static final ArrayList ON_ENVIRONMENT_CHANGE_LISTENERS = new ArrayList<OnEnvironmentChangeListener>();

    private static final ArrayList MODULE_LIST = new ArrayList<ModuleBean>();

    public static final ModuleBean MODULE_MUSIC = new ModuleBean("Music", "音乐");

    private static EnvironmentBean sCurrentMusicEnvironment;

    public static final EnvironmentBean MUSIC_ONLINE_ENVIRONMENT = new EnvironmentBean("online", "https://www.codexiaomai.top/api/", "正式", MODULE_MUSIC);

    public static final EnvironmentBean MUSIC_TEST_ENVIRONMENT = new EnvironmentBean("test", "http://test.codexiaomai.top/api/", "测试", MODULE_MUSIC);

    private static final EnvironmentBean DEFAULT_MUSIC_ENVIRONMENT = MUSIC_ONLINE_ENVIRONMENT;

    static {

        MODULE_LIST.add(MODULE_MUSIC);
        MODULE_MUSIC.getEnvironments().add(MUSIC_ONLINE_ENVIRONMENT);
        MODULE_MUSIC.getEnvironments().add(MUSIC_TEST_ENVIRONMENT);
    }

    public static void addOnEnvironmentChangeListener(OnEnvironmentChangeListener onEnvironmentChangeListener) {
        ON_ENVIRONMENT_CHANGE_LISTENERS.add(onEnvironmentChangeListener);
    }

    public static void removeOnEnvironmentChangeListener(OnEnvironmentChangeListener onEnvironmentChangeListener) {
        ON_ENVIRONMENT_CHANGE_LISTENERS.remove(onEnvironmentChangeListener);
    }

    public static void removeAllOnEnvironmentChangeListener() {
        ON_ENVIRONMENT_CHANGE_LISTENERS.clear();
    }

    private static void onEnvironmentChange(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
        for (Object onEnvironmentChangeListener : ON_ENVIRONMENT_CHANGE_LISTENERS) {
            if (onEnvironmentChangeListener instanceof OnEnvironmentChangeListener) {
                ((OnEnvironmentChangeListener) onEnvironmentChangeListener).onEnvironmentChanged(module, oldEnvironment, newEnvironment);
            }
        }
    }

    public static final String getMusicEnvironment(Context context, boolean isDebug) {
        return getMusicEnvironmentBean(context, isDebug).getUrl();
    }

    public static final EnvironmentBean getMusicEnvironmentBean(Context context, boolean isDebug) {
        if (!isDebug) {
            return DEFAULT_MUSIC_ENVIRONMENT;
        }
        if (sCurrentMusicEnvironment == null) {
            android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + ".environmentswitcher", android.content.Context.MODE_PRIVATE);
            String url = sharedPreferences.getString("musicEnvironmentUrl", DEFAULT_MUSIC_ENVIRONMENT.getUrl());
            String environmentName = sharedPreferences.getString("musicEnvironmentName", DEFAULT_MUSIC_ENVIRONMENT.getName());
            String appAlias = sharedPreferences.getString("musicEnvironmentAlias", DEFAULT_MUSIC_ENVIRONMENT.getAlias());
            for (EnvironmentBean environmentBean : MODULE_MUSIC.getEnvironments()) {
                if (android.text.TextUtils.equals(environmentBean.getUrl(), url)) {
                    sCurrentMusicEnvironment = environmentBean;
                    break;
                }
            }
        }
        return sCurrentMusicEnvironment;
    }

    public static final void setMusicEnvironment(Context context, EnvironmentBean newEnvironment) {
        context.getSharedPreferences(context.getPackageName() + ".environmentswitcher", android.content.Context.MODE_PRIVATE).edit()
                .putString("musicEnvironmentUrl", newEnvironment.getUrl())
                .putString("musicEnvironmentName", newEnvironment.getName())
                .putString("musicEnvironmentAlias", newEnvironment.getAlias())
                .apply();
        if (!newEnvironment.equals(sCurrentMusicEnvironment)) {
            EnvironmentBean oldEnvironment = sCurrentMusicEnvironment;
            sCurrentMusicEnvironment = newEnvironment;
            onEnvironmentChange(MODULE_MUSIC, oldEnvironment, newEnvironment);
        }
    }

    public static ArrayList getModuleList() {
        return MODULE_LIST;
    }
}
```

除了自动生成上面的代码外，Environment Switcher 还提供了展示和切换环境列表的 Activity 页面。Environment Switcher 为何如此强大？

![](https://upload-images.jianshu.io/upload_images/5275145-86ec65de992f4acd.jpeg?imageMogr2/auto-orient/strip)![](https://upload-images.jianshu.io/upload_images/5275145-31829b6b1d589e61.jpeg?imageMogr2/auto-orient/strip)![](https://upload-images.jianshu.io/upload_images/5275145-cdc106e03833259b.gif?imageMogr2/auto-orient/strip)

这是因为它站在四大巨人的肩膀上，这四大巨人分别是 `Java 注解` `APT` `反射` 和 `混淆`。相信大家对它们都有所耳闻，现在非常流行的 `Retrofit`、`Butter Knife` `GreenDao` 等开源库都使用了它们，这里就不做过多介绍了。

## Environment Switcher 的组成与原理

打开 [Environment Switcher](https://github.com/CodeXiaoMai/EnvironmentSwitcher) 的项目目录，我们会看到 Environment Switcher 由`base` ` compiler` `compiler-release` `environmentswitcher` 和 `sample` 五个模块构成。

- base：包含所有的注解 `@Moduel` 和 `@Environment` ，以及 Java Bean 类：`ModuleBean`、`EnvironmentBean` ，监听事件： `OnEnvironmentChangeListener` 和一个存储公共静态常量的类：`Constants`。其他几个模块都要依赖这个模块。
- compiler：只包含一个类 `EnvironmentSwitcherCompiler`，在编译 Debug 版本时利用 APT 处理被注解标记的类和属性生成 `EnvironmentSwitcher.java` 文件。
- compiler-release:  和 `compiler` 模块一样只包含一个类 `EnvironmentSwitcherCompiler`，在编译 Release 版本时利用 APT 处理被注解标记的类和属性生成 `EnvironmentSwitcher.java` 文件。
- environmentswitcher：通过反射原理获取`EnvironmentSwitcher.java` 中生成的所有模块的环境，并提供列表展示以及切换环境功能的 Activity 页面。
- sample：`Environment Switcher` 标准使用方法的示例工程。

### 为什么 Debug 版和 Release 版要用不同的注解处理工具

因为测试环境只在 Debug 和测试阶段使用，在 Release 版本中就只使用正式环境了，而如果 Release 版本中测试环境不隐藏就会打包到 apk 中，一旦被他人获取可能会带来不必要的麻烦或损失。

### 如何自动隐藏测试环境

我们先比较一下 compiler 和 compiler-release 生成的 `EnvironmentSwitcher.java` 文件主要有什么区别。其实主要区别就是生成的 EnvironmentBean 静态常量，具体区别如下：

- Debug 版的 EnvironmentSwitcher.java
 
  ```
  public static final EnvironmentBean MUSIC_ONLINE_ENVIRONMENT = new  EnvironmentBean("online", "https://www.codexiaomai.top/api/", "正式", MODULE_MUSIC);

  public static final EnvironmentBean MUSIC_TEST_ENVIRONMENT = new EnvironmentBean("test", "http://test.codexiaomai.top/api/", "测试", MODULE_MUSIC);
  ```
  
- Release 版的 EnvironmentSwitcher.java
  
  ```
  public static final EnvironmentBean MUSIC_ONLINE_ENVIRONMENT = new EnvironmentBean("online", "https://www.codexiaomai.top/api/", "正式", MODULE_MUSIC);

  public static final EnvironmentBean MUSIC_TEST_ENVIRONMENT = new EnvironmentBean("test", "", "测试", MODULE_MUSIC);
  ```

通过比较可以发现只有一个地方不同，那就是 Release 版中的非正式环境的具体地址为空字符串，这样就达到了隐藏测试环境具体地址的效果，进而解决了测试环境泄露的问题。

你可能又要说了，不要骗我啊，我在环境配置类 `EnvironmentConfig.java` 文件中还写了测试环境的地址呢，你看：

```
@Environment(url = "https://www.codexiaomai.top/api/", isRelease = true, alias = "正式")
private String online;

@Environment(url = "http://test.codexiaomai.top/api/", alias = "测试")
private String test;
```

先不要急，我慢慢来给大家解释。虽然通过 compiler-release 生成的类中把测试环境地址隐藏了，但在 EnvironmentConfig.java 中的确还活生生的包含测试地址的代码。那这个地方的测试环境怎么隐藏呢？

这就到了一直还没有出场的混淆工具上场了。

### 混淆助我一臂之力

先来简单回顾一下混淆的作用吧：

1. 压缩（Shrink）：**检测并移除无用的类、字段、方法和属性**。
2. 优化（Optimize）：对字节码进行优化，**移除无用指令**。
3. 混淆（obfuscate）：对类、方法、变量、属性进行重命名。
4. 预检（preverify）：对Java代码进行预检，以确保代码可以执行。

看到我用粗体标记的关键字了吧，Environment Switcher 就是利用 compiler-release 配合混淆工具的移除功能来实现隐藏测试环境的。

真的有这么神奇吗？是不是真的我们用事实说话。（这里以sample工程为例）

首先通过 Gradle 生成 Release 包，再对生成的 apk 文件进行反编译。下图是反编译后工程的目录结构：

![反编译包结构](https://upload-images.jianshu.io/upload_images/5275145-59504ce2e58dc2df.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/480)

上面的图片中已经很清楚的展示了项目被混淆后的结构，至于为什么 EnvironmentSwitcher 包中所有子包和类都没有混淆，后面会介绍。

那么 com.xiaomai.demo 包中被混淆的类都分别对应于原工程中哪个文件呢？我们通过查看 EnvironmentSwitcher/sample/build/outputs/mapping/release 目录下找到 mapping.txt 文件，从中提取主要的信息如下：

```
com.xiaomai.demo.data.Api -> com.xiaomai.demo.a.a:
com.xiaomai.demo.data.GankResponse -> com.xiaomai.demo.a.b:
com.xiaomai.demo.data.MusicResponse -> com.xiaomai.demo.a.c:
com.xiaomai.demo.fragment.HomeFragment -> com.xiaomai.demo.b.a:
com.xiaomai.demo.fragment.MusicFragment -> com.xiaomai.demo.b.b:
com.xiaomai.demo.fragment.SettingsFragment -> com.xiaomai.demo.b.c:
com.xiaomai.demo.net.AppRetrofit -> com.xiaomai.demo.c.a:
com.xiaomai.demo.MainActivity -> com.xiaomai.demo.MainActivity:

com.xiaomai.environmentswitcher.Constants -> com.xiaomai.environmentswitcher.Constants:
com.xiaomai.environmentswitcher.EnvironmentSwitchActivity -> com.xiaomai.environmentswitcher.EnvironmentSwitchActivity:
com.xiaomai.environmentswitcher.EnvironmentSwitcher -> com.xiaomai.environmentswitcher.EnvironmentSwitcher:
com.xiaomai.environmentswitcher.R -> com.xiaomai.environmentswitcher.R:
com.xiaomai.environmentswitcher.annotation.Environment -> com.xiaomai.environmentswitcher.annotation.Environment:
com.xiaomai.environmentswitcher.annotation.Module -> com.xiaomai.environmentswitcher.annotation.Module:
com.xiaomai.environmentswitcher.bean.EnvironmentBean -> com.xiaomai.environmentswitcher.bean.EnvironmentBean:
com.xiaomai.environmentswitcher.bean.ModuleBean -> com.xiaomai.environmentswitcher.bean.ModuleBean:
com.xiaomai.environmentswitcher.listener.OnEnvironmentChangeListener -> com.xiaomai.environmentswitcher.listener.OnEnvironmentChangeListener:
```

按照上面的映射关系，得到下图结果：

![](https://upload-images.jianshu.io/upload_images/5275145-8cc66bb1025c29d5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/480)

为了证明我没有在 mapping.txt  中遗漏 EnvironmentConfig 类的相关信息，再贴张图片：

![](https://upload-images.jianshu.io/upload_images/5275145-e183f1100d70ea0b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

当我借助搜索工具搜索 `EnvironmentConfig` 关键字时，提示找不到该关键字；而在同目录下的 usage.txt 文件（被移除的代码）中找到了 EnvironmentConfig 类，这再次证明了 EnvironmentConfig 被混淆工具移除了。

> EnvironmentConfig 能被混淆工具移除的前提是不被其他任何类引用，这也是为什么建议将所有被 `@Module` 和 `@Environment` 标注的类或属性用 `private` 修饰的原因。这样能在编写代码的阶段从根本上杜绝因测试环境被引用导致无法在混淆时被移除进而导致泄露。

### 为什么 EnvironmentSwitcher 中的类没被混淆

用过开源库或其他第三方非开源SDK的大家都知道，这些库或SDK有些会要求我配置混淆规则，否则会因混淆导致运行时异常。那么 EnvironmentSwitcer 为什么没有配置混淆规则，也没有被混淆呢？

这是因为 Environment Switcher 已经帮大家做了这一步，是不是很贴心？！Environment Switcher 设计的目标是：“在保证正常功能的前提下，让使用者少配置哪怕一行代码”。

那么 Environment Switcher 是怎么做到的呢？主要就是同过 Gradle 配置的。

- build.gradle
    
    ```
    android {
        defaultConfig {
            ...
            consumerProguardFiles 'consumer-proguard-rules.pro'
        }
    }
     ```
     
- consumer-proguard-rules.pro
     
   ```
    -dontwarn java.nio.**
    -dontwarn javax.annotation.**
    -dontwarn javax.lang.**
    -dontwarn javax.tools.**
    -dontwarn com.squareup.javapoet.**
    -keep class com.xiaomai.environmentswitcher.** { *; }
    ```

其实 Environment Switcher 除了帮大家做了混淆规则配置，还有很多地方。例如添加依赖配置方面：最初版本的 Environment Switcher 中 Activity 是继承于 AppCompatActivity，展示环境列表用的是 RecyclerView，这样就需要添加 support-v7 包和 recyclerview-v7 包，依赖方式如下：

```
implementation "com.android.support:appcompat-v7:$version"
implementation "com.android.support:recyclerview-v7:$version"
```

为什么这里不指定具体版本而要用 version 代替呢？

因为这个 version 是个 "TroubleMaker"。如果项目中依赖的 support-v7 包和 recyclerview-v7 包与Environment Switcher 中的版本不一致，Android Studio 在编译时会自动选择高版本的依赖，这样就可能产生兼容性错误，导致原本正常的项目因提示错误而编译失败。举个最简单的例子，在Api 26 中 Fragment 的 onCreateView方法的 LayoutInflater 参数是可空的，如下所示：

```
override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return super.onCreateView(inflater, container, savedInstanceState)
}
```

而在 Api 27 中却强制不能为空，如下所示：

```
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return super.onCreateView(inflater, container, savedInstanceState)
}
```

这就导致在编译时出现错误提示 `'onCreateView' overrides nothing`。

其实这种错误是有方法解决的，具体方法如下：

```
implementation ("com.xiaomai.environmentswitcher:environmentswitcher:$version"){
    exclude group: 'com.android.support'
}
```

这样在引入 Environment Switcher 时就会移除 Environment Switcher 中的 support 包，但是总觉得这种方式不够优雅，违背了Environment Switcher 的设计目标。

于是我把 AppCampatActivity 替换为 Activity，RecyclerView 替换为 ListView。这两个类都是原生 Sdk 提供的，不需要引入任何依赖，又完美解决了问题。

为了方便开发者，Environment Switcher 还做了很多努力与尝试，在这里就不一一列举了。

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

好了，关于Environment Switcher 的原理解析就到此为止吧，[更多使用介绍可参考Demo](https://github.com/CodeXiaoMai/EnvironmentSwitcher) ,Demo 中有 Environment Switcher 结合 Retrofit 使用的详细实现过程。

![Environment Switcher](https://upload-images.jianshu.io/upload_images/5275145-b3529a5f32884ab9.gif?imageMogr2/auto-orient/strip)

## 划重点

嘿嘿，第一次做开源工具，如果喜欢 Environment Switcher 欢迎随意打赏或 [Star](https://github.com/CodeXiaoMai/EnvironmentSwitcher) 。

- 支付宝

![支付宝](https://upload-images.jianshu.io/upload_images/5275145-db3ad0d5c0ea1b4c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 微信
 
![微信](https://upload-images.jianshu.io/upload_images/5275145-8bd8dff563306741.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)

## 更新日志

### 2018.9.6

**发布v1.5版**

- [bugfix] [#3 fix bug Can't get the right environment in the callback](https://github.com/CodeXiaoMai/EnvironmentSwitcher/issues/3)
- [update] “onEnvironmentChange” 方法名改为 “onEnvironmentChange**d**”

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