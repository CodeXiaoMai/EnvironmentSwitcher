package com.xiaomai.environmentswitcher.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
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
public class EnvironmentSwitcherCompiler extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Module.class);

        TypeSpec.Builder environmentSwitcherClassBuilder = TypeSpec
                .classBuilder(Constants.ENVIRONMENT_SWITCHER_FILE_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        FieldSpec onEnvironmentChangeListenersFiled = FieldSpec
                .builder(ArrayList.class, Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(String.format("new %s<%s>()", ArrayList.class.getSimpleName(), OnEnvironmentChangeListener.class.getSimpleName()))
                .build();
        environmentSwitcherClassBuilder.addField(onEnvironmentChangeListenersFiled);

        MethodSpec addOnEnvironmentChangeListenerMethod = MethodSpec
                .methodBuilder(Constants.METHOD_NAME_ADD_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(OnEnvironmentChangeListener.class, Constants.VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addStatement(String.format("%s.add(%s)", Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS, Constants.VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER))
                .build();
        environmentSwitcherClassBuilder.addMethod(addOnEnvironmentChangeListenerMethod);

        MethodSpec removeOnEnvironmentChangeListenerMethod = MethodSpec
                .methodBuilder(Constants.METHOD_NAME_REMOVE_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(OnEnvironmentChangeListener.class, Constants.VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addStatement(String.format("%s.remove(%s)", Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS, Constants.VAR_PARAMETER_ON_ENVIRONMENT_CHANGE_LISTENER))
                .build();
        environmentSwitcherClassBuilder.addMethod(removeOnEnvironmentChangeListenerMethod);

        MethodSpec removeAllOnEnvironmentChangeListenerMethod = MethodSpec
                .methodBuilder(Constants.METHOD_NAME_REMOVE_ALL_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addStatement(String.format("%s.clear()", Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS))
                .build();
        environmentSwitcherClassBuilder.addMethod(removeAllOnEnvironmentChangeListenerMethod);

        MethodSpec onEnvironmentChangeMethod = MethodSpec
                .methodBuilder(Constants.METHOD_NAME_ON_ENVIRONMENT_CHANGE)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(void.class)
                .addParameter(ModuleBean.class, Constants.VAR_PARAMETER_MODULE)
                .addParameter(EnvironmentBean.class, Constants.VAR_PARAMETER_OLD_ENVIRONMENT)
                .addParameter(EnvironmentBean.class, Constants.VAR_PARAMETER_NEW_ENVIRONMENT)
                .addCode(String.format(
                             "for (Object onEnvironmentChangeListener : %s) {\n" +
                                "   if (onEnvironmentChangeListener instanceof %s) {\n" +
                                "       ((%s) onEnvironmentChangeListener).onEnvironmentChange(%s, %s, %s);\n" +
                                "   }\n" +
                                "}\n", Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS,
                        OnEnvironmentChangeListener.class.getSimpleName(),
                        OnEnvironmentChangeListener.class.getSimpleName(), Constants.VAR_PARAMETER_MODULE, Constants.VAR_PARAMETER_OLD_ENVIRONMENT, Constants.VAR_PARAMETER_NEW_ENVIRONMENT))
                .build();
        environmentSwitcherClassBuilder.addMethod(onEnvironmentChangeMethod);

        FieldSpec moduleListField = FieldSpec
                .builder(ArrayList.class, Constants.VAR_MODULE_LIST, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(String.format("new %s<%s>()", ArrayList.class.getSimpleName(), ModuleBean.class.getSimpleName()))
                .build();
        environmentSwitcherClassBuilder.addField(moduleListField);

        MethodSpec.Builder getModuleListMethodBuilder = MethodSpec
                .methodBuilder(Constants.METHOD_NAME_GET_MODULE_LIST)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ArrayList.class);

        CodeBlock.Builder staticCodeBlockBuilder = CodeBlock.builder()
                .addStatement(String.format("%s<%s> %s", ArrayList.class.getSimpleName(), EnvironmentBean.class.getSimpleName(), Constants.VAR_ENVIRONMENTS));

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
                    .builder(ModuleBean.class, String.format("%s%s", Constants.VAR_MODULE_PREFIX, moduleUpperCaseName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer(String.format("new %s(\"%s\", \"%s\")", ModuleBean.class.getSimpleName(), moduleName, moduleAliasName))
                    .build();
            environmentSwitcherClassBuilder.addField(moduleXXField);

            staticCodeBlockBuilder
                    .add("\n")
                    .addStatement(String.format("%s.add(%s%s)", Constants.VAR_MODULE_LIST, Constants.VAR_MODULE_PREFIX, moduleUpperCaseName))
                    .addStatement(String.format("%s = new %s<>()", Constants.VAR_ENVIRONMENTS, ArrayList.class.getSimpleName()))
                    .addStatement(String.format("%s%s.setEnvironments(%s)", Constants.VAR_MODULE_PREFIX, moduleUpperCaseName, Constants.VAR_ENVIRONMENTS));

            FieldSpec xxModuleCurrentEnvironmentField = FieldSpec
                    .builder(EnvironmentBean.class, String.format(Constants.VAR_CURRENT_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                    .build();
            environmentSwitcherClassBuilder.addField(xxModuleCurrentEnvironmentField);

            MethodSpec getXXEnvironmentMethod = MethodSpec
                    .methodBuilder(String.format(Constants.METHOD_NAME_GET_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(String.class)
                    .addParameter(Constants.CONTEXT_TYPE_NAME, Constants.VAR_CONTEXT)
                    .addParameter(boolean.class, Constants.VAR_PARAMETER_IS_DEBUG)
                    .addStatement(String.format("return get%sEnvironmentBean(%s, %s).getUrl()", moduleName, Constants.VAR_CONTEXT, Constants.VAR_PARAMETER_IS_DEBUG))
                    .build();
            environmentSwitcherClassBuilder.addMethod(getXXEnvironmentMethod);

            MethodSpec getXXEnvironmentBeanMethod = MethodSpec
                    .methodBuilder(String.format(Constants.METHOD_NAME_GET_XX_ENVIRONMENT_BEAN, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(EnvironmentBean.class)
                    .addParameter(Constants.CONTEXT_TYPE_NAME, Constants.VAR_CONTEXT)
                    .addParameter(boolean.class, Constants.VAR_PARAMETER_IS_DEBUG)
                    .addCode(String.format(
                                 "if (!%s) {\n" +
                                    "    return %s%s%s;\n" +
                                    "}\n",
                            Constants.VAR_PARAMETER_IS_DEBUG,
                            Constants.VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX))
                    .addCode(String.format(
                                "if (%s == null) {\n" +
                                   "    android.content.SharedPreferences sharedPreferences = %s.getSharedPreferences(%s.getPackageName() + \".%s\", %s);\n" +
                                   "    String url = sharedPreferences.getString(\"%s%s%s\", %s%s%s.getUrl());\n" +
                                   "    String environmentName = sharedPreferences.getString(\"%s%s%s\", %s%s%s.getName());\n" +
                                   "    String appAlias = sharedPreferences.getString(\"%s%s%s\", %s%s%s.getAlias());\n" +
                                   "    for (EnvironmentBean environmentBean : MODULE_%s.getEnvironments()) {\n" +
                                   "        if (android.text.TextUtils.equals(environmentBean.getUrl(), url)) {\n" +
                                   "            %s = environmentBean;\n" +
                                   "            break;\n" +
                                   "        }\n" +
                                   "    }\n" +
                                    "}\n",
                            String.format(Constants.VAR_CURRENT_XX_ENVIRONMENT, moduleName),
                            Constants.VAR_CONTEXT, Constants.VAR_CONTEXT, Constants.ENVIRONMENT_SWITCHER_FILE_NAME.toLowerCase(), Constants.MODE_PRIVATE,
                            moduleLowerCaseName, Constants.ENVIRONMENT, Constants.VAR_ENVIRONMENT_URL_SUFFIX, Constants.VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX,
                            moduleLowerCaseName, Constants.ENVIRONMENT, Constants.VAR_ENVIRONMENT_NAME_SUFFIX, Constants.VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX,
                            moduleLowerCaseName, Constants.ENVIRONMENT, Constants.VAR_ENVIRONMENT_ALIAS_SUFFIX, Constants.VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX,
                            moduleUpperCaseName,
                            String.format(Constants.VAR_CURRENT_XX_ENVIRONMENT, moduleName)))
                    .addStatement(String.format("return " + Constants.VAR_CURRENT_XX_ENVIRONMENT, moduleName))
                    .build();
            environmentSwitcherClassBuilder.addMethod(getXXEnvironmentBeanMethod);

            MethodSpec setXXEnvironmentMethod = MethodSpec.methodBuilder(String.format(Constants.METHOD_NAME_SET_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(void.class)
                    .addParameter(Constants.CONTEXT_TYPE_NAME, Constants.VAR_CONTEXT)
                    .addParameter(EnvironmentBean.class, Constants.VAR_PARAMETER_ENVIRONMENT)
                    .addStatement(String.format(
                                "%s.getSharedPreferences(%s.getPackageName() + \".%s\", %s).edit()\n" +
                                   ".putString(\"%s%s%s\", %s.getUrl())\n" +
                                   ".putString(\"%s%s%s\", %s.getName())\n" +
                                   ".putString(\"%s%s%s\", %s.getAlias())\n" +
                                   ".apply()",
                            Constants.VAR_CONTEXT, Constants.VAR_CONTEXT, Constants.ENVIRONMENT_SWITCHER_FILE_NAME.toLowerCase(), Constants.MODE_PRIVATE,
                            moduleLowerCaseName, Constants.ENVIRONMENT, Constants.VAR_ENVIRONMENT_URL_SUFFIX, Constants.VAR_PARAMETER_ENVIRONMENT,
                            moduleLowerCaseName, Constants.ENVIRONMENT, Constants.VAR_ENVIRONMENT_NAME_SUFFIX, Constants.VAR_PARAMETER_ENVIRONMENT,
                            moduleLowerCaseName, Constants.ENVIRONMENT, Constants.VAR_ENVIRONMENT_ALIAS_SUFFIX, Constants.VAR_PARAMETER_ENVIRONMENT
                    ))
                    .addCode(String.format(
                                "if (!%s.equals(%s)) {\n" +
                                   "   onEnvironmentChange(%s%s, %s, %s);\n" +
                                   "}\n", Constants.VAR_PARAMETER_ENVIRONMENT, String.format(Constants.VAR_CURRENT_XX_ENVIRONMENT, moduleName),
                            Constants.VAR_MODULE_PREFIX, moduleUpperCaseName, String.format(Constants.VAR_CURRENT_XX_ENVIRONMENT, moduleName), Constants.VAR_PARAMETER_ENVIRONMENT))
                    .addStatement(String.format(Constants.VAR_CURRENT_XX_ENVIRONMENT + " = " + Constants.VAR_PARAMETER_ENVIRONMENT, moduleName))
                    .build();
            environmentSwitcherClassBuilder.addMethod(setXXEnvironmentMethod);

            FieldSpec.Builder defaultXXEnvironmentFiledBuilder = FieldSpec
                    .builder(EnvironmentBean.class, String.format("%s%s%s", Constants.VAR_DEFAULT_ENVIRONMENT_PREFIX, moduleUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX),
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

                FieldSpec environmentField;

                if (environmentAnnotation.isRelease()) {
                    environmentField = FieldSpec
                            .builder(EnvironmentBean.class, String.format("%s_%s%s", moduleUpperCaseName, environmentUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX),
                            Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer(String.format("new %s(\"%s\", \"%s\", \"%s\", %s%s)",
                                    EnvironmentBean.class.getSimpleName(), environmentName, url, alias, Constants.VAR_MODULE_PREFIX, moduleUpperCaseName))
                            .build();
                    defaultXXEnvironmentFiledBuilder.initializer(String.format("%s_%s%s", moduleUpperCaseName, environmentUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX));
                } else {
                    environmentField = FieldSpec.builder(EnvironmentBean.class,
                            String.format("%s_%s%s", moduleUpperCaseName, environmentUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX),
                            Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer(String.format("new %s(\"%s\", \"%s\", \"%s\", %s%s)",
                                    EnvironmentBean.class.getSimpleName(), environmentName, "", alias, Constants.VAR_MODULE_PREFIX, moduleUpperCaseName))
                            .build();
                }
                environmentSwitcherClassBuilder.addField(environmentField);

                staticCodeBlockBuilder
                        .addStatement(String.format("%s.add(%s)", Constants.VAR_ENVIRONMENTS, String.format("%s_%s%s", moduleUpperCaseName, environmentUpperCaseName, Constants.VAR_DEFAULT_ENVIRONMENT_SUFFIX)));
            }

            environmentSwitcherClassBuilder.addField(defaultXXEnvironmentFiledBuilder.build()).build();
        }

        getModuleListMethodBuilder.addStatement(String.format("return %s", Constants.VAR_MODULE_LIST));

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

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Module.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }
}
