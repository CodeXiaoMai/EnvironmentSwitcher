
[![API](https://img.shields.io/badge/API-7%2B-brightgreen.svg)](https://android-arsenal.com/api?level=7)

### [中文文档](https://github.com/CodeXiaoMai/EnvironmentSwitcher/blob/master/README_CN.md)

# Environment Switcher

Environment Switcher is a tool for switching environments in one-click process using the principles of Java annotation, APT, Reflection, and Proguard during the development and testing of Android.

### Feature

- Easy to configure
- Safe, does not leak test environment url
- Switch environments with one click without repackaging
- Support configuration and switching environment by module
- Support for callbacks when the environment switches
- Automatically generate `toggle` `save` `get` logic for the environment
- Decoupling from the project
- ......

### Environment Switcher VS Gradle

Now you might think: "These functions I can do with Gradle, why use Environment Switcher?" Let's compare Environment Switcher and Gradle.

|Compare content|Environment Switcher|Gradle with different Application Id| Gradle with same Application Id|
|:-:|:--:|:--:|:--:|
|Switch environment at runtime|✔️|✖️|✖️|
|Callback when switching environment|✔️|✖️|✖️|
|Switching environment logic|Automatic generated|Need to implement by yourself|Need to implement by yourself|
|Number of n environment packages| 1 | n | n|
|Install multiple sets of environments at the same time|✔️|✔️|✖️|
|Payment and other SDK package name verification|✔️|✖️|✔️|
|Multi-module environment configuration|✔️|✔️|✔️|
|Do not leak test environment url|✔️|✔️|✔️|
|……|——|——|——|

### Instruction manual

1. Configuring the project's build.gradle

 Lastest Version：

 module|environmentswitcher|environmentswitcher-compiler|environmentswitcher-compiler-release
:---:|:---:|:---:|:---:
version|[ ![Download](https://api.bintray.com/packages/xiaomai/maven/environmentswitcher/images/download.svg) ](https://bintray.com/xiaomai/maven/environmentswitcher/_latestVersion) | [ ![Download](https://api.bintray.com/packages/xiaomai/maven/environmentswitcher-compiler/images/download.svg) ](https://bintray.com/xiaomai/maven/environmentswitcher-compiler/_latestVersion) | [ ![Download](https://api.bintray.com/packages/xiaomai/maven/environmentswitcher-compiler-release/images/download.svg) ](https://bintray.com/xiaomai/maven/environmentswitcher-compiler-release/_latestVersion)

 - java project

 ```
 dependencies {
         ...
         implementation "com.xiaomai.environmentswitcher:environmentswitcher:$version"
         debugAnnotationProcessor "com.xiaomai.environmentswitcher:environmentswitcher-compiler:$version"
         releaseAnnotationProcessor "com.xiaomai.environmentswitcher:environmentswitcher-compiler-release:$version"
 }
 ```

 - kotlin project

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

2. Write the EnvironmentConfig file

    **This class is the core code that the Environment Switcher relies on. All the logic that gets and modifies the environment will be automatically generated based on the classes and properties of the class marked with the `@Module` and `@Environment` annotations.**
    
    > Note: If you are using Kotlin in your project, write EnvironmentConfig in the Java language, just as you must write the Entity class in the Java language in GreenDao.

    ```
    /**
     * Environment configuration class</br>
     *
     * ⚠ It is not recommended to reference any subclasses and member variables in the class. 
     * Once the properties of the informal environment are referenced, 
     * the Proguard tool will not remove the class when it is packaged, 
     * causing the test url to leak.</br>
     * 
     * Environment Switcher will automatically hide the test environment url when compiling the Release version.</br></br>
     *
     * It is recommended that all classes or member variables in this class that are decorated with {@link Module} and {@link Environment} be privately decorated.</br>
     * The Environment Switcher automatically generates the corresponding Module_XX and Environment_XX static constants during compilation.</br>
     * For example: EnvironmentSwitcher.MODULE_APP can get all the corresponding environments under the App module.</br>
     */
    public class EnvironmentConfig {
    
        /**
         * The entire app environment
         */
        @Module
        class App {
            @Environment(url = "https://gank.io/api/", isRelease = true)
            private String online;
        }
    
        /**
         * Special module Music environment
         */
        @Module
        class Music {
            @Environment(url = "https://www.codexiaomai.top/api/", isRelease = true)
            private String online;
    
            @Environment(url = "http://test.codexiaomai.top/api/")
            private String test;
        }
    
        /**
         * Special module News environment
         */
        @Module
        class News {
            @Environment(url = "http://news/release/", isRelease = true)
            private String release;
    
            @Environment(url = "http://news/test/")
            private String test;
    
            @Environment(url = "http://news/test1/")
            private String test1;
    
            @Environment(url = "http://news/sandbox/")
            private String sandbox;
        }
    }
    ```

    - @Module

		A class or interface decorated with {@link Module} represents a module that automatically generates the getXXEnvironment() and setXXEnvironment() methods of the corresponding module at compile time. A class or interface decorated with {@link Module} can have n (n>0) attributes modified by {@link Environment}, indicating that there are n environments in the module.

      For example, in the above code, there are three classes modified by `@Module`, which means there are three modules, of which only one attribute in the App module is decorated with `@Environment`, indicating that the module has only one environment; and Music and News There are 2 and 4 environments in the module.

      In addition, `@Module` also has an optional attribute `alias` that specifies the alias of the module. This value defaults to an empty string. The main purpose of this property is to display the Chinese name on the Switch Environment UI page. For example, the Music and News modules will display “音乐” and “新闻” respectively in the Switch Environment page.

      > Note: If all the modules in your project share the same Host url, you only need to configure one Module.

    - @Environment

		An attribute marked by {@link Environment} represents an environment，Specific address of the current environment, you must specify a specific value. There are also two optional attributes: `isRelease` and `alias`.

       - isRelease 

         By default, false is returned. When true is returned, the current {@link Environment} is the default environment for the {@link Module} and the environment when the app was officially released. **There must be one and only one {@link Environment} of isRelease in a {@link Module} with a value of true,
otherwise the compilation will fail.** 

         For example, there are two environments in the Music module: online (formal) and test (test), because online isRelease = true, so it is the default environment and the environment when the app is officially released.

       - alias

         Similar to alias in `@Module`, it is used to display the name of the environment in the UI page of the switching environment. The value defaults to an empty string. If a non-empty string is specified for it, the name of the environment is specified as the value of alias.

     > **Re-emphasize**: There must be one and only one Environment's isRelease value in a Module, otherwise the compilation will fail.

3. Click "Build" -> "Rebuild Project" in the menu bar and wait for the compilation to complete.

    Now that the configuration is complete, you can enjoy it！
    
### Add the entry for EnvrionmentSwtichActivity

The manual switching environment must have an interactive page. The Environment Switcher has been automatically integrated. You only need to add an entry (this entry is recommended only for internal versions such as Debug tests).

For example: on the "My" page.

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

You can use the `EnvironmentSwitchActivity.launch(getContext())` method already provided by Environment Switcher to start it; of course you can also start it with `startActivity(newContext(getContext(), EnvironmentSwitchActivity.class))`.It depends on you.

### Get the environment url of the corresponding module：

```
String appEnvironment = EnvironmentSwitcher.getAppEnvironment(this, BuildConfig.DEBUG);
String musicEnvironment = EnvironmentSwitcher.getMusicEnvironment(this, BuildConfig.DEBUG);
String newsEnvironment = EnvironmentSwitcher.getNewsEnvironment(this, BuildConfig.DEBUG);
```

### Get the environment entity class of the corresponding module(since 1.4)：

```
EnvironmentBean appEnvironmentBean = EnvironmentSwitcher.getAppEnvironmentBean(this, BuildConfig.DEBUG);
EnvironmentBean musicEnvironmentBean = EnvironmentSwitcher.getMusicEnvironmentBean(this, BuildConfig.DEBUG);
EnvironmentBean newsEnvironmentBean = EnvironmentSwitcher.getNewsEnvironmentBean(this, BuildConfig.DEBUG);
```

It should be noted here that the environment of the corresponding module requires two parameters. The first one is just a `Context`. There is no need to explain more, because Environment Switcher uses SharedPreferences to store data. The second parameter is a boolean value. If it is true, it is currently the internal version used for Debug or test. The url obtained at this time is the url saved after we manually switch the environment. If it is false, it is currently released. The version used for the user. The url obtained at this time is the address we specified `isRelease = true` in `@Environment`, and the environment url manually switched is no longer valid.

### AddOnEnvironmentChangeListener

Environment Switcher supports switching environment callbacks. You can add them by the following methods. **Note that you should not forget to remove the listener event when you don't need to listen for environment switching events**.

```
public class MainActivity extends AppCompatActivity implements OnEnvironmentChangeListener{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EnvironmentSwitcher.addOnEnvironmentChangeListener(this);
    }

    @Override
    public void onEnvironmentChanged(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
        Log.e(TAG, module.getName() + "oleEnvironment=" + oldEnvironment.getName() + "，oldUrl=" + oldEnvironment.getUrl()
         + ",newNevironment=" + newEnvironment.getName() + "，newUrl=" + newEnvironment.getUrl());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EnvironmentSwitcher.removeOnEnvironmentChangeListener(this);
    }
}
```

### Switch the SDK Environment

We generally rely on SDKs provided by third parties in the project, and these SDKs also provide a test environment. If you want to switch environments in the app, you can't use the above method. What should I do?

For example, our "live" module is a referenced SDK, we can do like this:

1. First configure the "live" module in EnvironmentConfig.java

   ```
   public class EnvironmentConfig {
        private class Live {
            @Environment(url = "online", isRelease = true)
            private String online;

            @Environment(url = "test")
            private String test;
        }
   }
   ```
   > The url is only used to distinguish the environment here, not the real url, but to ensure that the url of each environment in the same module is different.

2. Add a listener to the Application

   ```
   EnvironmentSwitcher.addOnEnvironmentChangeListener(new OnEnvironmentChangeListener() {
        @Override
        public void onEnvironmentChanged(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
           if (module.equals(EnvironmentSwitcher.MODULE_LIVE)) {
               if (newEnvironment.equals(EnvironmentSwitcher.LIVE_ONLINE_ENVIRONMENT)) {
                 // Call the SDK to switch the environment, the formal environment
               } else if (newEnvironment.equals(EnvironmentSwitcher.LIVE_TEST_ENVIRONMENT)) {
                 // Call the SDK to switch the environment, the test environment
               }
           }
        }
   });
   ```

	> Switch the SDK environment with Environment Switcher's environment switch callback.

## Additional

In addition to being used as an environment switching tool, Environment Switcher can also be used to make other configurable switches, such as the switch to print logs. (ps: This is not the target function of the Environment Switcher design, it is a small egg!)

```
@Module
private class Log {
    @Environment(url = "false", isRelease = true)
    private String closeLog;
    @Environment(url = "true")
    private String openLog;
}

public void loge(Context context, String tag, String msg) {
    if (EnvironmentSwitcher.getLogEnvironmentBean(context, BuildConfig.DEBUG)
            .equals(EnvironmentSwitcher.LOG_OPENLOG_ENVIRONMENT)) {
        android.util.Log.e(tag, msg);
    }
}
```

Of course, just to give a simple example, the Environment Switcher can do much more than that. More features are welcome to try.

For more information, please refer to the Sample Project, three has a detailed implementation of Environment Switcher combined with Retrofit.

## Update log

### 2018.9.6

**release v1.5**

- [bugfix] [#3 fix bug Can't get the right environment in the callback](https://github.com/CodeXiaoMai/EnvironmentSwitcher/issues/3)
- [update] The "onEnvironmentChange" method name is changed to "onEnvironmentChange**<font color=#ff0000>d</font>**"

### 2018.9.2 

**release v1.4**

- [new] Add ModuleBean and EnvironmentBean static constants in EnvironmentSwitcher
- [update] Callback method in the OnEnvironmentChangeListener interface
	- before 1.3：

		```
		EnvironmentSwitcher.addOnEnvironmentChangeListener(new OnEnvironmentChangeListener() {
			@Override
			public void onEnvironmentChange(String module, String oldUrl, String newUrl) {
				Log.e(TAG, module + "oldUrl=" + oldUrl + ",newUrl=" + newUrl);
			}
		});
		```

	- 1.4 changed：

		```
		EnvironmentSwitcher.addOnEnvironmentChangeListener(new OnEnvironmentChangeListener() {
			@Override
			public void onEnvironmentChange(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
				Log.e(TAG, module.getName() + "oleEnvironment=" + oldEnvironment.getName() + "，oldUrl=" + oldEnvironment.getUrl() + ",newNevironment=" + newEnvironment.getName() + "，newUrl=" + newEnvironment.getUrl());
			}
		});
		```

# Principle analysis

[Click to view Environment Switcher principle analysis](https://www.jianshu.com/p/190710a846b9)

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