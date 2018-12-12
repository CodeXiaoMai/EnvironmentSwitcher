package com.xiaomai.environmentswitcher.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaomai.environmentswitcher.Constants;
import com.xiaomai.environmentswitcher.annotation.Environment;
import com.xiaomai.environmentswitcher.annotation.Module;
import com.xiaomai.environmentswitcher.bean.EnvironmentBean;
import com.xiaomai.environmentswitcher.bean.ModuleBean;
import com.xiaomai.environmentswitcher.listener.OnEnvironmentChangeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class EnvironmentSwitcherCompilerDebug extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Module.class);

        TypeSpec.Builder environmentSwitcherClassBuilder = TypeSpec
                .classBuilder(Constants.ENVIRONMENT_SWITCHER_FILE_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        FieldSpec onEnvironmentChangeListenersFiled = FieldSpec
                .builder(ArrayList.class, VAR_ON_ENVIRONMENT_CHANGE_LISTENERS)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(String.format("new %s<%s>()", ArrayList.class.getSimpleName(), OnEnvironmentChangeListener.class.getSimpleName()))
                .build();
        environmentSwitcherClassBuilder.addField(onEnvironmentChangeListenersFiled);

        MethodSpec addOnEnvironmentChangeListenerMethod = MethodSpec
                .methodBuilder(METHOD_NAME_ADD_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(OnEnvironmentChangeListener.class, VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addStatement(String.format("%s.add(%s)", VAR_ON_ENVIRONMENT_CHANGE_LISTENERS, VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER))
                .build();
        environmentSwitcherClassBuilder.addMethod(addOnEnvironmentChangeListenerMethod);

        MethodSpec removeOnEnvironmentChangeListenerMethod = MethodSpec
                .methodBuilder(METHOD_NAME_REMOVE_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(OnEnvironmentChangeListener.class, VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addStatement(String.format("%s.remove(%s)", VAR_ON_ENVIRONMENT_CHANGE_LISTENERS, VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER))
                .build();
        environmentSwitcherClassBuilder.addMethod(removeOnEnvironmentChangeListenerMethod);

        MethodSpec removeAllOnEnvironmentChangeListenerMethod = MethodSpec
                .methodBuilder(METHOD_NAME_REMOVE_ALL_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addStatement(String.format("%s.clear()", VAR_ON_ENVIRONMENT_CHANGE_LISTENERS))
                .build();
        environmentSwitcherClassBuilder.addMethod(removeAllOnEnvironmentChangeListenerMethod);

        MethodSpec onEnvironmentChangeMethod = MethodSpec
                .methodBuilder(METHOD_NAME_ON_ENVIRONMENT_CHANGE)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(void.class)
                .addParameter(ModuleBean.class, VAR_PARAMETER_MODULE)
                .addParameter(EnvironmentBean.class, VAR_PARAMETER_OLD_ENVIRONMENT)
                .addParameter(EnvironmentBean.class, VAR_PARAMETER_NEW_ENVIRONMENT)
                .addCode(String.format(
                            "for (Object onEnvironmentChangeListener : %s) {\n" +
                                "   if (onEnvironmentChangeListener instanceof %s) {\n" +
                                "       ((%s) onEnvironmentChangeListener).onEnvironmentChanged(%s, %s, %s);\n" +
                                "   }\n" +
                                "}\n", VAR_ON_ENVIRONMENT_CHANGE_LISTENERS,
                        OnEnvironmentChangeListener.class.getSimpleName(),
                        OnEnvironmentChangeListener.class.getSimpleName(), VAR_PARAMETER_MODULE, VAR_PARAMETER_OLD_ENVIRONMENT, VAR_PARAMETER_NEW_ENVIRONMENT))
                .build();
        environmentSwitcherClassBuilder.addMethod(onEnvironmentChangeMethod);

        FieldSpec moduleListField = FieldSpec
                .builder(ArrayList.class, VAR_MODULE_LIST, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(String.format("new %s<%s>()", ArrayList.class.getSimpleName(), ModuleBean.class.getSimpleName()))
                .build();
        environmentSwitcherClassBuilder.addField(moduleListField);

        MethodSpec.Builder getModuleListMethodBuilder = MethodSpec
                .methodBuilder(Constants.METHOD_NAME_GET_MODULE_LIST)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ArrayList.class);

        CodeBlock.Builder staticCodeBlockBuilder = CodeBlock.builder();

        for (Element element : elements) {
            Module moduleAnnotation = element.getAnnotation(Module.class);
            if (moduleAnnotation == null) {
                continue;
            }
            String moduleName = element.getSimpleName().toString();
            String moduleUpperCaseName = moduleName.toUpperCase();
            String moduleLowerCaseName = moduleName.toLowerCase();
            String moduleAliasName = moduleAnnotation.alias();

            FieldSpec moduleXXField = FieldSpec
                    .builder(ModuleBean.class, String.format("%s%s", VAR_MODULE_PREFIX, moduleUpperCaseName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer(String.format("new %s(\"%s\", \"%s\")", ModuleBean.class.getSimpleName(), moduleName, moduleAliasName))
                    .build();
            environmentSwitcherClassBuilder.addField(moduleXXField);

            staticCodeBlockBuilder
                    .add("\n")
                    .addStatement(String.format("%s.add(%s%s)", VAR_MODULE_LIST, VAR_MODULE_PREFIX, moduleUpperCaseName));

            FieldSpec xxModuleCurrentEnvironmentField = FieldSpec
                    .builder(EnvironmentBean.class, String.format(VAR_CURRENT_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                    .build();
            environmentSwitcherClassBuilder.addField(xxModuleCurrentEnvironmentField);

            MethodSpec getXXEnvironmentMethod = MethodSpec
                    .methodBuilder(String.format(METHOD_NAME_GET_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(String.class)
                    .addParameter(CONTEXT_TYPE_NAME, VAR_CONTEXT)
                    .addParameter(boolean.class, VAR_PARAMETER_IS_DEBUG)
                    .addStatement(String.format("return get%sEnvironmentBean(%s, %s).getUrl()", moduleName, VAR_CONTEXT, VAR_PARAMETER_IS_DEBUG))
                    .build();
            environmentSwitcherClassBuilder.addMethod(getXXEnvironmentMethod);

            MethodSpec getXXEnvironmentBeanMethod = MethodSpec
                    .methodBuilder(String.format(METHOD_NAME_GET_XX_ENVIRONMENT_BEAN, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(EnvironmentBean.class)
                    .addParameter(CONTEXT_TYPE_NAME, VAR_CONTEXT)
                    .addParameter(boolean.class, VAR_PARAMETER_IS_DEBUG)
                    .addCode(String.format(
                                 "if (!%s) {\n" +
                                    "    return %s%s%s;\n" +
                                    "}\n",
                            VAR_PARAMETER_IS_DEBUG,
                            VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX))
                    .addCode(String.format(
                                "if (%s == null) {\n" +
                                   "    android.content.SharedPreferences sharedPreferences = %s.getSharedPreferences(%s.getPackageName() + \".%s\", %s);\n" +
                                   "    String url = sharedPreferences.getString(\"%s%s%s\", %s%s%s.getUrl());\n" +
                                   "    String environmentName = sharedPreferences.getString(\"%s%s%s\", %s%s%s.getName());\n" +
                                   "    String alias = sharedPreferences.getString(\"%s%s%s\", %s%s%s.getAlias());\n" +
                                   "    for (EnvironmentBean environmentBean : MODULE_%s.getEnvironments()) {\n" +
                                   "        if (android.text.TextUtils.equals(environmentBean.getUrl(), url)\n" +
                                   "                && android.text.TextUtils.equals(environmentBean.getName(), environmentName)\n" +
                                   "                && android.text.TextUtils.equals(environmentBean.getAlias(), alias)) {\n" +
                                   "            %s = environmentBean;\n" +
                                   "            break;\n" +
                                   "        }\n" +
                                   "    }\n" +
                                   "    if (%s == null) {\n" +
                                   "        %s = %s%s%s;\n" +
                                   "    }\n" +
                                    "}\n",
                            String.format(VAR_CURRENT_XX_ENVIRONMENT, moduleName),
                            VAR_CONTEXT, VAR_CONTEXT, Constants.ENVIRONMENT_SWITCHER_FILE_NAME.toLowerCase(), MODE_PRIVATE,
                            moduleLowerCaseName, ENVIRONMENT, VAR_ENVIRONMENT_URL_SUFFIX, VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX,
                            moduleLowerCaseName, ENVIRONMENT, VAR_ENVIRONMENT_NAME_SUFFIX, VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX,
                            moduleLowerCaseName, ENVIRONMENT, VAR_ENVIRONMENT_ALIAS_SUFFIX, VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX,
                            moduleUpperCaseName,
                            String.format(VAR_CURRENT_XX_ENVIRONMENT, moduleName),
                            String.format(VAR_CURRENT_XX_ENVIRONMENT, moduleName),
                            String.format(VAR_CURRENT_XX_ENVIRONMENT, moduleName),
                            VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX))
                    .addStatement(String.format("return " + VAR_CURRENT_XX_ENVIRONMENT, moduleName))
                    .build();
            environmentSwitcherClassBuilder.addMethod(getXXEnvironmentBeanMethod);

            MethodSpec setXXEnvironmentMethod = MethodSpec.methodBuilder(String.format(METHOD_NAME_SET_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(void.class)
                    .addParameter(CONTEXT_TYPE_NAME, VAR_CONTEXT)
                    .addParameter(EnvironmentBean.class, VAR_PARAMETER_NEW_ENVIRONMENT)
                    .addStatement(String.format(
                                "%s.getSharedPreferences(%s.getPackageName() + \".%s\", %s).edit()\n" +
                                   ".putString(\"%s%s%s\", %s.getUrl())\n" +
                                   ".putString(\"%s%s%s\", %s.getName())\n" +
                                   ".putString(\"%s%s%s\", %s.getAlias())\n" +
                                   ".commit()",
                            VAR_CONTEXT, VAR_CONTEXT, Constants.ENVIRONMENT_SWITCHER_FILE_NAME.toLowerCase(), MODE_PRIVATE,
                            moduleLowerCaseName, ENVIRONMENT, VAR_ENVIRONMENT_URL_SUFFIX, VAR_PARAMETER_NEW_ENVIRONMENT,
                            moduleLowerCaseName, ENVIRONMENT, VAR_ENVIRONMENT_NAME_SUFFIX, VAR_PARAMETER_NEW_ENVIRONMENT,
                            moduleLowerCaseName, ENVIRONMENT, VAR_ENVIRONMENT_ALIAS_SUFFIX, VAR_PARAMETER_NEW_ENVIRONMENT
                    ))
                    .addCode(String.format(
                                "if (!%s.equals(%s)) {\n" +
                                   "    EnvironmentBean oldEnvironment = %s;\n" +
                                   "    %s = %s;\n" +
                                   "    onEnvironmentChange(%s%s, oldEnvironment, %s);\n" +
                                   "}\n", VAR_PARAMETER_NEW_ENVIRONMENT, String.format(VAR_CURRENT_XX_ENVIRONMENT, moduleName),
                            String.format(VAR_CURRENT_XX_ENVIRONMENT, moduleName),
                            String.format(VAR_CURRENT_XX_ENVIRONMENT, moduleName), VAR_PARAMETER_NEW_ENVIRONMENT,
                            VAR_MODULE_PREFIX, moduleUpperCaseName, VAR_PARAMETER_NEW_ENVIRONMENT))
                    .build();
            environmentSwitcherClassBuilder.addMethod(setXXEnvironmentMethod);

            FieldSpec.Builder defaultXXEnvironmentFiledBuilder = FieldSpec
                    .builder(EnvironmentBean.class, String.format("%s%s%s", VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX),
                    Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            List<? extends Element> allMembers = processingEnv.getElementUtils().getAllMembers((TypeElement) element);

            for (Element member : allMembers) {
                Environment environmentAnnotation = member.getAnnotation(Environment.class);
                if (environmentAnnotation == null) {
                    continue;
                }

                String environmentName = member.getSimpleName().toString();
                String environmentUpperCaseName = environmentName.toUpperCase();
                String url = environmentAnnotation.url();
                String alias = environmentAnnotation.alias();

                FieldSpec environmentField = generateEnvironmentField(environmentAnnotation, defaultXXEnvironmentFiledBuilder,
                        moduleUpperCaseName, environmentName, environmentUpperCaseName, url, alias);

                environmentSwitcherClassBuilder.addField(environmentField);

                staticCodeBlockBuilder
                        .addStatement(String.format("%s%s.getEnvironments().add(%s)", VAR_MODULE_PREFIX, moduleUpperCaseName, String.format("%s_%s%s", moduleUpperCaseName, environmentUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX)));
            }

            environmentSwitcherClassBuilder.addField(defaultXXEnvironmentFiledBuilder.build()).build();
        }

        getModuleListMethodBuilder.addStatement(String.format("return %s", VAR_MODULE_LIST));

        environmentSwitcherClassBuilder.addMethod(getModuleListMethodBuilder.build());

        environmentSwitcherClassBuilder.addStaticBlock(staticCodeBlockBuilder.build());

        JavaFile switchEnvironmentJavaFile = JavaFile.builder(Constants.PACKAGE_NAME, environmentSwitcherClassBuilder.build()).build();

        try {
            switchEnvironmentJavaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected FieldSpec generateEnvironmentField(Environment environmentAnnotation,
                                                 FieldSpec.Builder defaultXXEnvironmentFiledBuilder,
                                                 String moduleUpperCaseName,
                                                 String environmentName,
                                                 String environmentUpperCaseName,
                                                 String url,
                                                 String alias) {
        if (environmentAnnotation.isRelease()) {
            defaultXXEnvironmentFiledBuilder.initializer(String.format("%s_%s%s", moduleUpperCaseName, environmentUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX));
        }

        return FieldSpec
                .builder(EnvironmentBean.class,
                        String.format("%s_%s%s", moduleUpperCaseName, environmentUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX),
                        Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(String.format("new %s(\"%s\", \"%s\", \"%s\", %s%s)",
                        EnvironmentBean.class.getSimpleName(), environmentName, url, alias, VAR_MODULE_PREFIX, moduleUpperCaseName))
                .build();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Module.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    public static final String ENVIRONMENT = "Environment";

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
    public static final String VAR_ON_ENVIRONMENT_CHANGE_LISTENERS = "ON_ENVIRONMENT_CHANGE_LISTENERS";

    public static final String VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER = "onEnvironmentChangeListener";
    public static final String VAR_PARAMETER_IS_DEBUG = "isDebug";
    public static final String VAR_PARAMETER_MODULE = "module";
    public static final String VAR_PARAMETER_OLD_ENVIRONMENT = "oldEnvironment";
    public static final String VAR_PARAMETER_NEW_ENVIRONMENT = "newEnvironment";

    public static final TypeName CONTEXT_TYPE_NAME = ClassName.get("android.content", "Context");
}