package com.xiaomai.environmentswitcher;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

public class Constants {

    public static final String ENVIRONMENT_SWITCHER_FILE_NAME = "EnvironmentSwitcher";
    public static final String PACKAGE_NAME = "com.xiaomai.environmentswitcher";
    public static final String ENVIRONMENT = "Environment";

    public static final String METHOD_NAME_GET_MODULE_LIST = "getModuleList";
    public static final String METHOD_NAME_GET_XX_ENVIRONMENT = "get%sEnvironment";
    public static final String METHOD_NAME_GET_XX_ENVIRONMENT_BEAN = "get%sEnvironmentBean";
    public static final String METHOD_NAME_SET_XX_ENVIRONMENT = "set%sEnvironment";
    public static final String METHOD_NAME_ADD_ON_ENVIRONMENT_CHANGE_LISTENER = "addOnEnvironmentChangeListener";
    public static final String METHOD_NAME_REMOVE_ON_ENVIRONMENT_CHANGE_LISTENER = "removeOnEnvironmentChangeListener";
    public static final String METHOD_NAME_REMOVE_ALL_ON_ENVIRONMENT_CHANGE_LISTENER = "removeAllOnEnvironmentChangeListener";
    public static final String METHOD_NAME_ON_ENVIRONMENT_CHANGE = "onEnvironmentChange";

    public static final String MODE_PRIVATE = "android.content.Context.MODE_PRIVATE";

    public static final String VAR_CONTEXT = "context";
    public static final String VAR_ENVIRONMENT_URL_SUFFIX = "Url";
    public static final String VAR_ENVIRONMENT_NAME_SUFFIX = "Name";
    public static final String VAR_ENVIRONMENT_ALIAS_SUFFIX = "Alias";
    public static final String VAR_MODULE_PREFIX = "MODULE_";
    public static final String VAR_DEFAULT_ENVIRONMENT_PREFIX = "DEFAULT_";
    public static final String VAR_DEFAULT_ENVIRONMENT_SUFFIX = "_ENVIRONMENT";
    public static final String VAR_CURRENT_XX_ENVIRONMENT = "sCurrent%sEnvironment";
    public static final String VAR_MODULE_LIST = "MODULE_LIST";
    public static final String VAR_ENVIRONMENTS = "environments";
    public static final String VAR_ON_ENVIRONMENT_CHANGE_LISTENERS = "ON_ENVIRONMENT_CHANGE_LISTENERS";

    public static final String VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER = "onEnvironmentChangeListener";
    public static final String VAR_PARAMETER_ENVIRONMENT = "environment";
    public static final String VAR_PARAMETER_IS_DEBUG = "isDebug";
    public static final String VAR_PARAMETER_MODULE = "module";
    public static final String VAR_PARAMETER_OLD_ENVIRONMENT = "oldEnvironment";
    public static final String VAR_PARAMETER_NEW_ENVIRONMENT = "newEnvironment";

    public static final TypeName CONTEXT_TYPE_NAME = ClassName.get("android.content", "Context");
}