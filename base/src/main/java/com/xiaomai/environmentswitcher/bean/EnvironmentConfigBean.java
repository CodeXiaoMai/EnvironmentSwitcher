package com.xiaomai.environmentswitcher.bean;

import java.util.List;

public class EnvironmentConfigBean {

    private List<ModuleBean> modules;

    public List<ModuleBean> getModules() {
        return modules;
    }

    public void setModules(List<ModuleBean> modules) {
        this.modules = modules;
    }

    public static class ModuleBean {
        private List<EnvironmentBean> environments;

        public List<EnvironmentBean> getEnvironments() {
            return environments;
        }

        public void setEnvironments(List<EnvironmentBean> environments) {
            this.environments = environments;
        }

        public static class EnvironmentBean {
            /**
             * name : test
             * url : http://www.test.com
             */

            private String name;
            private String url;

            public EnvironmentBean() {
            }

            public EnvironmentBean(String name, String url) {
                this.name = name;
                this.url = url;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
