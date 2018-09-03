package com.xiaomai.environmentswitcher.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.xiaomai.environmentswitcher.annotation.Environment;
import com.xiaomai.environmentswitcher.bean.EnvironmentBean;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Modifier;

@AutoService(Processor.class)
public class EnvironmentSwitcherCompilerRelease extends EnvironmentSwitcherCompilerDebug {

    @Override
    protected FieldSpec generateEnvironmentField(Environment environmentAnnotation,
                                                 FieldSpec.Builder defaultXXEnvironmentFiledBuilder,
                                                 String moduleUpperCaseName,
                                                 String environmentName,
                                                 String environmentUpperCaseName,
                                                 String url,
                                                 String alias) {
        if (environmentAnnotation.isRelease()) {
            return super.generateEnvironmentField(environmentAnnotation, defaultXXEnvironmentFiledBuilder, moduleUpperCaseName, environmentName, environmentUpperCaseName, url, alias);
        }

        return FieldSpec.builder(EnvironmentBean.class,
                String.format("%s_%s%s", moduleUpperCaseName, environmentUpperCaseName, VAR_DEFAULT_ENVIRONMENT_SUFFIX),
                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(String.format("new %s(\"%s\", \"%s\", \"%s\", %s%s)",
                        EnvironmentBean.class.getSimpleName(), environmentName, "", alias, VAR_MODULE_PREFIX, moduleUpperCaseName))
                .build();
    }
}