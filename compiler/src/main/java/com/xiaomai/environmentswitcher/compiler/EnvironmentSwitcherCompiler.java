package com.xiaomai.environmentswitcher.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
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

import static com.xiaomai.environmentswitcher.Constants.ARRAY_LIST;
import static com.xiaomai.environmentswitcher.Constants.ENVIRONMENT_BEAN;
import static com.xiaomai.environmentswitcher.Constants.ENVIRONMENT_CONFIG_BEAN;
import static com.xiaomai.environmentswitcher.Constants.ENVIRONMENT_MODULE_BEAN;
import static com.xiaomai.environmentswitcher.Constants.ENVIRONMENT_SWITCHER_FILE_NAME;
import static com.xiaomai.environmentswitcher.Constants.METHOD_NAME_GET_ENVIRONMENT_CONFIG;
import static com.xiaomai.environmentswitcher.Constants.METHOD_NAME_GET_XX_ENVIRONMENT;
import static com.xiaomai.environmentswitcher.Constants.METHOD_NAME_SET_XX_ENVIRONMENT;
import static com.xiaomai.environmentswitcher.Constants.MODE_PRIVATE;
import static com.xiaomai.environmentswitcher.Constants.PACKAGE_NAME;
import static com.xiaomai.environmentswitcher.Constants.VAR_CONFIG_BEAN;
import static com.xiaomai.environmentswitcher.Constants.VAR_CONTEXT;
import static com.xiaomai.environmentswitcher.Constants.VAR_CURRENT_URL;
import static com.xiaomai.environmentswitcher.Constants.VAR_DEFAULT_URL_PREFIX;
import static com.xiaomai.environmentswitcher.Constants.VAR_DEFAULT_URL_SUFFIX;
import static com.xiaomai.environmentswitcher.Constants.VAR_ENVIRONMENTS;
import static com.xiaomai.environmentswitcher.Constants.VAR_ENVIRONMENT_BEAN;
import static com.xiaomai.environmentswitcher.Constants.VAR_MODULES;
import static com.xiaomai.environmentswitcher.Constants.VAR_MODULE_BEAN;
import static com.xiaomai.environmentswitcher.Constants.VAR_PARAMETER_IS_DEBUG;
import static com.xiaomai.environmentswitcher.Constants.VAR_PARAMETER_URL;
import static com.xiaomai.environmentswitcher.Constants.VAR_URL_SUFFIX;
import static com.xiaomai.environmentswitcher.Constants.CONTEXT_TYPE_NAME;
import static com.xiaomai.environmentswitcher.Constants.ENVIRONMENT_CONFIG_TYPE_NAME;
import static com.xiaomai.environmentswitcher.Constants.STRING_TYPE_NAME;

@AutoService(Processor.class)
public class EnvironmentSwitcherCompiler extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Module.class);

        TypeSpec.Builder switchEnvironmentClassBuilder = TypeSpec.classBuilder(ENVIRONMENT_SWITCHER_FILE_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        MethodSpec.Builder getEnvironmentBuilder = MethodSpec.methodBuilder(METHOD_NAME_GET_ENVIRONMENT_CONFIG)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ENVIRONMENT_CONFIG_TYPE_NAME)
                .addStatement(String.format("%s %s = new %s()", ENVIRONMENT_CONFIG_BEAN, VAR_CONFIG_BEAN, ENVIRONMENT_CONFIG_BEAN))
                .addStatement(String.format("%s<%s> %s = new %s<>()", ARRAY_LIST, ENVIRONMENT_MODULE_BEAN, VAR_MODULES, ARRAY_LIST))
                .addStatement(String.format("%s.setModules(%s)", VAR_CONFIG_BEAN, VAR_MODULES))
                .addStatement(String.format("%s %s", ENVIRONMENT_MODULE_BEAN, VAR_MODULE_BEAN))
                .addStatement(String.format("%s<%s> %s", ARRAY_LIST, ENVIRONMENT_BEAN, VAR_ENVIRONMENTS))
                .addStatement(String.format("%s %s", ENVIRONMENT_BEAN, VAR_ENVIRONMENT_BEAN));

        for (Element element : elements) {
            Module moduleAnnotation = element.getAnnotation(Module.class);
            if (moduleAnnotation == null) {
                continue;
            }
            String moduleName = element.getSimpleName().toString();
            String moduleUpperCaseName = moduleName.toUpperCase();
            String moduleLowerCaseName = moduleName.toLowerCase();
            String moduleAliasName = moduleAnnotation.alias();

            getEnvironmentBuilder
                    .addStatement(String.format("%s = new %s()", VAR_MODULE_BEAN, ENVIRONMENT_MODULE_BEAN))
                    .addStatement(String.format("%s.setName(\"%s\")", VAR_MODULE_BEAN, moduleName))
                    .addStatement(String.format("%s.setAlias(\"%s\")", VAR_MODULE_BEAN, moduleAliasName))
                    .addStatement(String.format("%s.add(%s)", VAR_MODULES, VAR_MODULE_BEAN))
                    .addStatement(String.format("%s = new %s<>()", VAR_ENVIRONMENTS, ARRAY_LIST))
                    .addStatement(String.format("%s.setEnvironments(%s)", VAR_MODULE_BEAN, VAR_ENVIRONMENTS));

            FieldSpec currentUrlField = FieldSpec.builder(STRING_TYPE_NAME, String.format(VAR_CURRENT_URL, moduleUpperCaseName))
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                    .build();
            switchEnvironmentClassBuilder.addField(currentUrlField);

            MethodSpec getMethod = MethodSpec.methodBuilder(String.format(METHOD_NAME_GET_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(String.class)
                    .addParameter(CONTEXT_TYPE_NAME, VAR_CONTEXT)
                    .addParameter(TypeName.BOOLEAN, VAR_PARAMETER_IS_DEBUG)
                    .addCode(String.format(
                            "if (!%s) {\n" +
                                    "return %s%s%s;\n" +
                                    "}\n",
                            VAR_PARAMETER_IS_DEBUG, VAR_DEFAULT_URL_PREFIX, moduleUpperCaseName, VAR_DEFAULT_URL_SUFFIX))
                    .addCode(String.format(
                            "if (" + VAR_CURRENT_URL + " == null) {\n" +
                                    VAR_CURRENT_URL + " = %s.getSharedPreferences(%s.getPackageName() + \".%s\", %s).getString(\"%s%s\", %s%s%s);\n" +
                                    "}\n",
                            moduleUpperCaseName, moduleUpperCaseName,
                            VAR_CONTEXT, VAR_CONTEXT, ENVIRONMENT_SWITCHER_FILE_NAME.toLowerCase(), MODE_PRIVATE, moduleLowerCaseName, VAR_URL_SUFFIX,
                            VAR_DEFAULT_URL_PREFIX, moduleUpperCaseName, VAR_DEFAULT_URL_SUFFIX))
                    .addStatement(String.format("return " + VAR_CURRENT_URL, moduleUpperCaseName))
                    .build();

            switchEnvironmentClassBuilder.addMethod(getMethod);

            MethodSpec setMethod = MethodSpec.methodBuilder(String.format(METHOD_NAME_SET_XX_ENVIRONMENT, moduleName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(void.class)
                    .addParameter(CONTEXT_TYPE_NAME, VAR_CONTEXT)
                    .addParameter(String.class, VAR_PARAMETER_URL)
                    .addStatement(String.format("%s.getSharedPreferences(%s.getPackageName() + \".%s\", %s).edit().putString(\"%s%s\", %s).apply()",
                            VAR_CONTEXT, VAR_CONTEXT, ENVIRONMENT_SWITCHER_FILE_NAME.toLowerCase(), MODE_PRIVATE,
                            moduleLowerCaseName, VAR_URL_SUFFIX, VAR_PARAMETER_URL))
                    .addStatement(String.format(VAR_CURRENT_URL + " = " + VAR_PARAMETER_URL, moduleUpperCaseName))
                    .build();

            switchEnvironmentClassBuilder.addMethod(setMethod);

            FieldSpec.Builder defaultSpecBuilder = FieldSpec.builder(STRING_TYPE_NAME, String.format("%s%s%s", VAR_DEFAULT_URL_PREFIX,
                    moduleUpperCaseName, VAR_DEFAULT_URL_SUFFIX),
                    Modifier.PRIVATE, Modifier.STATIC);

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
                    defaultSpecBuilder.initializer(String.format("%s_%s", moduleUpperCaseName, urlUpperCaseName));
                }

                FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(member.asType()),
                        String.format("%s_%s", moduleUpperCaseName, urlUpperCaseName),
                        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer(String.format("\"%s\"", url))
                        .build();

                switchEnvironmentClassBuilder.addField(fieldSpec);


                getEnvironmentBuilder
                        .addStatement(String.format("%s = new %s(\"%s\", \"%s\", \"%s\", \"%s\")", VAR_ENVIRONMENT_BEAN, ENVIRONMENT_BEAN, environmentName, url, alias, moduleName))
                        .addStatement(String.format("%s.add(%s)", VAR_ENVIRONMENTS, VAR_ENVIRONMENT_BEAN));

            }

            switchEnvironmentClassBuilder.addField(defaultSpecBuilder.build()).build();
        }

        getEnvironmentBuilder.addStatement(String.format("return %s", VAR_CONFIG_BEAN));
        switchEnvironmentClassBuilder.addMethod(getEnvironmentBuilder.build());

        JavaFile switchEnvironmentJavaFile = JavaFile.builder(PACKAGE_NAME, switchEnvironmentClassBuilder.build()).build();

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
