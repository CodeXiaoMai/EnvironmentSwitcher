package com.xiaomai.environmentswitcher.bean;

import java.io.Serializable;

public class EnvironmentBean implements Serializable {
    private String name;
    private String alias;
    private String url;
    private ModuleBean module;
    private boolean checked;

    public EnvironmentBean() {
    }

    public EnvironmentBean(String name, String url, String alias, ModuleBean module) {
        this.name = name;
        this.url = url;
        this.alias = alias;
        this.module = module;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlias() {
        return alias == null ? "" : alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ModuleBean getModule() {
        return module;
    }

    public void setModule(ModuleBean module) {
        this.module = module;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

