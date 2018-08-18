package com.xiaomai.environmentswitcher.bean;

import java.io.Serializable;
import java.util.List;

public class EnvironmentConfigBean implements Serializable {

    private List<ModuleBean> modules;

    public List<ModuleBean> getModules() {
        return modules;
    }

    public void setModules(List<ModuleBean> modules) {
        this.modules = modules;
    }

    public static class ModuleBean implements Serializable {
        private String name;

        private String alias;

        private List<EnvironmentBean> environments;

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

        public static class EnvironmentBean implements Serializable {
            private String name;
            private String alias;
            private String url;
            private String moduleName;
            private boolean checked;

            public EnvironmentBean() {
            }

            public EnvironmentBean(String name, String url, String alias, String moduleName) {
                this.name = name;
                this.url = url;
                this.alias = alias;
                this.moduleName = moduleName;
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

            public String getModuleName() {
                return moduleName == null ? "" : moduleName;
            }

            public void setModuleName(String moduleName) {
                this.moduleName = moduleName;
            }

            public boolean isChecked() {
                return checked;
            }

            public void setChecked(boolean checked) {
                this.checked = checked;
            }
        }
    }
}
