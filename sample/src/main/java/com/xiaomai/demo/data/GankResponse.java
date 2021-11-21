package com.xiaomai.demo.data;

import android.support.annotation.Keep;

import java.util.List;

@Keep
public class GankResponse {

    private List<Data> data;
    private int errorCode;
    private String errorMsg;

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Keep
    public static class Data {

        private List<String> children;
        private int courseId;
        private int id;
        private String name;
        private long order;
        private int parentChapterId;
        private boolean userControlSetTop;
        private int visible;

        public void setChildren(List<String> children) {
            this.children = children;
        }

        public List<String> getChildren() {
            return children;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setOrder(long order) {
            this.order = order;
        }

        public long getOrder() {
            return order;
        }

        public void setParentChapterId(int parentChapterId) {
            this.parentChapterId = parentChapterId;
        }

        public int getParentChapterId() {
            return parentChapterId;
        }

        public void setUserControlSetTop(boolean userControlSetTop) {
            this.userControlSetTop = userControlSetTop;
        }

        public boolean getUserControlSetTop() {
            return userControlSetTop;
        }

        public void setVisible(int visible) {
            this.visible = visible;
        }

        public int getVisible() {
            return visible;
        }

    }
}
