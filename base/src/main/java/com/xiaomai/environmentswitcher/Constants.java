package com.xiaomai.environmentswitcher;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

public class Constants {

    public static final String ENVIRONMENT_SWITCHER_FILE_NAME = "EnvironmentSwitcher";
    public static final String PACKAGE_NAME = "com.xiaomai.environmentswitcher";

    public static final String MODE_PRIVATE = "android.content.Context.MODE_PRIVATE";
    public static final String ENVIRONMENT_CONFIG_BEAN = PACKAGE_NAME + ".bean.EnvironmentConfigBean";
    public static final String ENVIRONMENT_MODULE_BEAN = ENVIRONMENT_CONFIG_BEAN + ".ModuleBean";
    public static final String ENVIRONMENT_BEAN = ENVIRONMENT_MODULE_BEAN + ".EnvironmentBean";
    public static final String ARRAY_LIST = "java.util.ArrayList";

    public static final String VAR_CONTEXT = "context";
    public static final String VAR_URL_SUFFIX = "url";
    public static final String VAR_DEFAULT_URL_SUFFIX = "_URL";
    public static final String VAR_DEFAULT_URL_PREFIX = "DEFAULT_";
    public static final String VAR_PARAMETER_URL = "url";
    public static final String VAR_PARAMETER_IS_DEBUG = "isDebug";
    public static final String VAR_CURRENT_URL = "CURRENT_%s_URL";
    public static final String VAR_CONFIG_BEAN = "configBean";
    public static final String VAR_MODULES = "modules";
    public static final String VAR_MODULE_BEAN = "moduleBean";
    public static final String VAR_ENVIRONMENTS = "environments";
    public static final String VAR_ENVIRONMENT_BEAN = "environmentBean";

    public static final TypeName STRING_TYPE_NAME = ClassName.get("java.lang", "String");
    public static final TypeName CONTEXT_TYPE_NAME = ClassName.get("android.content", "Context");
    public static final TypeName ENVIRONMENT_CONFIG_TYPE_NAME = ClassName.get("com.xiaomai.environmentswitcher.bean", "EnvironmentConfigBean");
}
