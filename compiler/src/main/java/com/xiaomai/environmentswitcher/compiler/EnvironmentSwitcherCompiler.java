package com.xiaomai.environmentswitcher.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaomai.environmentswitcher.Constants;
import com.xiaomai.environmentswitcher.annotation.Environment;
import com.xiaomai.environmentswitcher.annotation.Module;

import java.io.IOException;
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

        TypeSpec.Builder environmentSwitcherClassBuilder = TypeSpec.classBuilder(Constants.ENVIRONMENT_SWITCHER_FILE_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        FieldSpec onEnvironmentChangeListenersFiled = FieldSpec.builder(Constants.ARRAY_LIST_TYPE_NAME, Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(String.format("new %s<%s>()", Constants.ARRAY_LIST, Constants.ON_ENVIRONMENT_CHANGE_LISTENER))
                .build();
        environmentSwitcherClassBuilder.addField(onEnvironmentChangeListenersFiled);

        MethodSpec addOnEnvironmentChangeListenerMethod = MethodSpec.methodBuilder(Constants.METHOD_NAME_ADD_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(Constants.ON_ENVIRONMENT_CHANGE_LISTENER_TYPE_NAME, Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addStatement(String.format("%s.add(%s)", Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS, Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENER))
                .build();
        environmentSwitcherClassBuilder.addMethod(addOnEnvironmentChangeListenerMethod);

        MethodSpec removeOnEnvironmentChangeListenerMethod = MethodSpec.methodBuilder(Constants.METHOD_NAME_REMOVE_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(Constants.ON_ENVIRONMENT_CHANGE_LISTENER_TYPE_NAME, Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addStatement(String.format("%s.remove(%s)", Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS, Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENER))
                .build();
        environmentSwitcherClassBuilder.addMethod(removeOnEnvironmentChangeListenerMethod);

        MethodSpec removeAllOnEnvironmentChangeListenerMethod = MethodSpec.methodBuilder(Constants.METHOD_NAME_REMOVE_ALL_ON_ENVIRONMENT_CHANGE_LISTENER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addStatement(String.format("%s.clear()", Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS))
                .build();
        environmentSwitcherClassBuilder.addMethod(removeAllOnEnvironmentChangeListenerMethod);

        MethodSpec onEnvironmentChangeMethod = MethodSpec.methodBuilder(Constants.METHOD_NAME_ON_ENVIRONMENT_CHANGE)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(String.class, Constants.VAR_PARAMETER_MODULE_NAME)
                .addParameter(String.class, Constants.VAR_PARAMETER_OLD_URL)
                .addParameter(String.class, Constants.VAR_PARAMETER_NEW_URL)
                .addCode(String.format("for (Object onEnvironmentChangeListener : %s) {\n" +
                        "   if (onEnvironmentChangeListener instanceof OnEnvironmentChangeListener) {\n" +
                        "       ((OnEnvironmentChangeListener) onEnvironmentChangeListener).onEnvironmentChange(%s, %s, %s);\n" +
                        "   }\n" +
                        "}\n", Constants.VAR_ON_ENVIRONMENT_CHANGE_LISTENERS, Constants.VAR_PARAMETER_MODULE_NAME, Constants.VAR_PARAMETER_OLD_URL, Constants.VAR_PARAMETER_NEW_URL))
                .build();
        environmentSwitcherClassBuilder.addMethod(onEnvironmentChangeMethod);

        MethodSpec.Builder getEnvironmentConfigMethodBuilder = MethodSpec.methodBuilder(Constants.METHOD_NAME_GET_ENVIRONMENT_CONFIG)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Constants.ENVIRONMENT_CONFIG_TYPE_NAME)
                .addStatement(String.format("%s %s = new %s()", Constants.ENVIRONMENT_CONFIG_BEAN, Constants.VAR_CONFIG_BEAN, Constants.ENVIRONMENT_CONFIG_BEAN))
                .addStatement(String.format("%s<%s> %s = new %s<>()", Constants.ARRAY_LIST, Constants.ENVIRONMENT_MODULE_BEAN, Constants.VAR_MODULES, Constants.ARRAY_LIST))
                .addStatement(String.format("%s.setModules(%s)", Constants.VAR_CONFIG_BEAN, Constants.VAR_MODULES))
                .addStatement(String.format("%s %s", Constants.ENVIRONMENT_MODULE_BEAN, Constants.VAR_MODULE_BEAN))
                .addStatement(String.format("%s<%s> %s", Constants.ARRAY_LIST, Constants.ENVIRONMENT_BEAN, Constants.VAR_ENVIRONMENTS))
                .addStatement(String.format("%s %s", Constants.ENVIRONMENT_BEAN, Constants.VAR_ENVIRONMENT_BEAN));

        for (Element element : elements) {
            Module moduleAnnotation = element.getAnnotation(Module.class);
            if (moduleAnnotation == null) {
                continue;
            }
            String moduleName = element.getSimpleName().toString();
            String moduleUpperCaseName = moduleName.toUpperCase();
            String moduleLowerCaseName = moduleName.toLowerCase();
            String moduleAliasName = moduleAnnotation.alias();

            getEnvironmentConfigMethodBuilder
                    .addStatement(String.format("%s = new %s()", Constants.VAR_MODULE_BEAN, Constants.ENVIRONMENT_MODULE_BEAN))
                    .addStatement(String.format("%s.setName(\"%s\")", Constants.VAR_MODULE_BEAN, moduleName))
                    .addStatement(String.format("%s.setAlias(\"%s\")", Constants.VAR_MODULE_BEAN, moduleAliasName))
                    .addStatement(String.format("%s.add(%s)", Constants.VAR_MODULES, Constants.VAR_MODULE_BEAN))
                    .addStatement(String.format("%s = new %s<>()", Constants.VAR_ENVIRONMENTS, Constants.ARRAY_LIST))
                    .addStatement(String.format("%s.setEnvironments(%s)", Constants.VAR_MODULE_BEAN, Constants.VAR_ENVIRONMENTS));

            FieldSpec currentUrlField = FieldSpec.builder(Constants.STRING_TYPE_NAME, String.format(Constants.VAR_CURRENT_URL, moduleUpperCaseName))
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                    .build();
            environmentSwitcherClassBuilder.addField(currentUrlField);

            MethodSpec getXXEnvironmentMethod = MethodSpec.methodBuilder(String.format(Constants.METHOD_NAME_GET_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(String.class)
                    .addParameter(Constants.CONTEXT_TYPE_NAME, Constants.VAR_CONTEXT)
                    .addParameter(TypeName.BOOLEAN, Constants.VAR_PARAMETER_IS_DEBUG)
                    .addCode(String.format(
                            "if (!%s) {\n" +
                                    "return %s%s%s;\n" +
                                    "}\n",
                            Constants.VAR_PARAMETER_IS_DEBUG, Constants.VAR_DEFAULT_URL_PREFIX, moduleUpperCaseName, Constants.VAR_DEFAULT_URL_SUFFIX))
                    .addCode(String.format(
                            "if (" + Constants.VAR_CURRENT_URL + " == null) {\n" +
                                    Constants.VAR_CURRENT_URL + " = %s.getSharedPreferences(%s.getPackageName() + \".%s\", %s).getString(\"%s%s\", %s%s%s);\n" +
                                    "}\n",
                            moduleUpperCaseName, moduleUpperCaseName,
                            Constants.VAR_CONTEXT, Constants.VAR_CONTEXT, Constants.ENVIRONMENT_SWITCHER_FILE_NAME.toLowerCase(), Constants.MODE_PRIVATE, moduleLowerCaseName, Constants.VAR_URL_SUFFIX,
                            Constants.VAR_DEFAULT_URL_PREFIX, moduleUpperCaseName, Constants.VAR_DEFAULT_URL_SUFFIX))
                    .addStatement(String.format("return " + Constants.VAR_CURRENT_URL, moduleUpperCaseName))
                    .build();

            environmentSwitcherClassBuilder.addMethod(getXXEnvironmentMethod);

            MethodSpec setXXEnvironmentMethod = MethodSpec.methodBuilder(String.format(Constants.METHOD_NAME_SET_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(void.class)
                    .addParameter(Constants.CONTEXT_TYPE_NAME, Constants.VAR_CONTEXT)
                    .addParameter(String.class, Constants.VAR_PARAMETER_URL)
                    .addStatement(String.format("%s.getSharedPreferences(%s.getPackageName() + \".%s\", %s).edit().putString(\"%s%s\", %s).apply()",
                            Constants.VAR_CONTEXT, Constants.VAR_CONTEXT, Constants.ENVIRONMENT_SWITCHER_FILE_NAME.toLowerCase(), Constants.MODE_PRIVATE,
                            moduleLowerCaseName, Constants.VAR_URL_SUFFIX, Constants.VAR_PARAMETER_URL))
                    .addCode(String.format(
                            "if (!%s.equals(%s)) {\n" +
                                    "   onEnvironmentChange(\"%s\", %s, %s);\n" +
                                    "}\n", Constants.VAR_PARAMETER_URL, String.format(Constants.VAR_CURRENT_URL, moduleUpperCaseName), moduleName, String.format(Constants.VAR_CURRENT_URL, moduleUpperCaseName), Constants.VAR_PARAMETER_URL))
                    .addStatement(String.format(Constants.VAR_CURRENT_URL + " = " + Constants.VAR_PARAMETER_URL, moduleUpperCaseName))
                    .build();

            environmentSwitcherClassBuilder.addMethod(setXXEnvironmentMethod);

            FieldSpec.Builder defaultXXUrlFiledBuilder = FieldSpec.builder(Constants.STRING_TYPE_NAME, String.format("%s%s%s", Constants.VAR_DEFAULT_URL_PREFIX,
                    moduleUpperCaseName, Constants.VAR_DEFAULT_URL_SUFFIX),
                    Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            List<? extends Element> allMembers = processingEnv.getElementUtils().getAllMembers((TypeElement) element);

            for (Element member : allMembers) {
                Environment environmentAnnotation = member.getAnnotation(Environment.class);
                if (environmentAnnotation == null) {
                    continue;
                }

                String environmentName = member.getSimpleName().toString();
                String urlUpperCaseName = environmentName.toUpperCase();
                String url = environmentAnnotation.url();
                String alias = environmentAnnotation.alias();

                if (environmentAnnotation.isRelease()) {
                    defaultXXUrlFiledBuilder.initializer(String.format("%s_%s", moduleUpperCaseName, urlUpperCaseName));
                }

                FieldSpec urlField = FieldSpec.builder(TypeName.get(member.asType()),
                        String.format("%s_%s", moduleUpperCaseName, urlUpperCaseName),
                        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer(String.format("\"%s\"", url))
                        .build();

                environmentSwitcherClassBuilder.addField(urlField);

                getEnvironmentConfigMethodBuilder
                        .addStatement(String.format("%s = new %s(\"%s\", \"%s\", \"%s\", \"%s\")", Constants.VAR_ENVIRONMENT_BEAN, Constants.ENVIRONMENT_BEAN, environmentName, url, alias, moduleName))
                        .addStatement(String.format("%s.add(%s)", Constants.VAR_ENVIRONMENTS, Constants.VAR_ENVIRONMENT_BEAN));
            }

            environmentSwitcherClassBuilder.addField(defaultXXUrlFiledBuilder.build()).build();
        }

        getEnvironmentConfigMethodBuilder.addStatement(String.format("return %s", Constants.VAR_CONFIG_BEAN));
        environmentSwitcherClassBuilder.addMethod(getEnvironmentConfigMethodBuilder.build());

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
