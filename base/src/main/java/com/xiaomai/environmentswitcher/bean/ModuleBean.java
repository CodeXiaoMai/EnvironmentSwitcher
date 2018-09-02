package com.xiaomai.environmentswitcher.bean;

import java.io.Serializable;
import java.util.List;

public class ModuleBean implements Serializable {
    private String name;

    private String alias;

    private List<EnvironmentBean> environments;

    public ModuleBean() {
    }

    public ModuleBean(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias == null ? "" : alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<EnvironmentBean> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<EnvironmentBean> environments) {
        this.environments = environments;
    }
}