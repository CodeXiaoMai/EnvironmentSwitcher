package com.xiaomai.environmentswitcher.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 每个被 {@link com.xiaomai.environmentswitcher.annotation.Module} 标记的属性，在编译时都会在
 * EnvironmentSwitcher.java 文件中生成与之一一对应的 {@link ModuleBean}。
 * <p>
 * Each attribute marked by {@link com.xiaomai.environmentswitcher.annotation.Module}
 * will generate a one-to-one correspondence with {@link ModuleBean} in the  EnvironmentSwitcher.java file at compile time.
 */
public class ModuleBean implements Serializable {
    private String name;

    private String type;

    private String alias;

    private List<EnvironmentBean> environments;

    public ModuleBean() {
        this("", "", "");
    }

    public ModuleBean(String name, String type) {
        this(name, "", type);
    }

    public ModuleBean(String name, String alias, String type) {
        this(name, alias, type, new ArrayList<EnvironmentBean>());
    }

    public ModuleBean(String name, String alias, String type, List<EnvironmentBean> environments) {
        this.name = name;
        this.alias = alias;
        this.type = type;
        this.environments = environments;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleBean that = (ModuleBean) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;
        return environments != null ? environments.equals(that.environments) : that.environments == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (environments != null ? environments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModuleBean{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", alias='" + alias + '\'' +
                ", environments=" + environments +
                '}';
    }
}