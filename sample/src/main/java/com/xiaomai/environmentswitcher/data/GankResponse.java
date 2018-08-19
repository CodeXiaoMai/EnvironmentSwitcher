package com.xiaomai.environmentswitcher.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GankResponse {

    /**
     * category : ["Android","拓展资源","福利","休息视频","iOS"]
     * error : false
     * results : {"Android":[{"_id":"5b6c53359d21226f45755583","createdAt":"2018-08-09T22:44:05.597Z","desc":"深入理解Android大图片处理","publishedAt":"2018-08-13T00:00:00.0Z","source":"web","type":"Android","url":"https://www.jianshu.com/p/1f008671fa44","used":true,"who":"罗占伟"},{"_id":"5b6d1a659d21226f45755588","createdAt":"2018-08-10T12:53:57.618Z","desc":" 精简版ZXing库，快速实现扫码功能，让集成更简单","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkjh5crg30990fg4qv"],"publishedAt":"2018-08-13T00:00:00.0Z","source":"web","type":"Android","url":"https://github.com/jenly1314/ZXingLite","used":true,"who":"Jenly"},{"_id":"5b7038229d212234189c24c9","createdAt":"2018-08-13T12:03:25.907Z","desc":"史上最强大图查看器 BigImageViewer 终于迎来 Gif 支持 ,代码可能会迟到，但从不会缺席。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkln1otg30dc0npkjt","https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zknnxakg30dc0np7wo"],"publishedAt":"2018-08-13T00:00:00.0Z","source":"web","type":"Android","url":"https://github.com/Piasy/BigImageViewer#animated-image-support","used":true,"who":"Piasy"},{"_id":"5b70d7cd9d21223415f53433","createdAt":"2018-08-13T08:58:53.212Z","desc":"Android酷炫实用的开源框架（UI框架）,助你快速开发Android","publishedAt":"2018-08-13T00:00:00.0Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s/VZ0FAm68wkPqiHnL2Ht1Ng","used":true,"who":"codeGoogler"},{"_id":"5b7106169d21223415f53439","createdAt":"2018-08-13T12:16:22.877Z","desc":"导航标签栏与丰富多彩的互动。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zko3o2tg30b80j87q8","https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkom1vsg30b80j2qv5","https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkp8rp9g30b10j7npd","https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkq69m9g30b60j4kjn","https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkrslggg30b50j64qt"],"publishedAt":"2018-08-13T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/Devlight/NavigationTabBar","used":true,"who":"lijinshanmx"},{"_id":"5b710bd59d212275a78c26eb","createdAt":"2018-08-13T12:40:53.490Z","desc":"Native App over HTTP, on Android。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkt3f1tg30rq0flu10"],"publishedAt":"2018-08-13T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/Jasonette/JASONETTE-Android","used":true,"who":"lijinshanmx"}],"iOS":[{"_id":"5b710ace9d212275a78c26ea","createdAt":"2018-08-13T12:36:30.277Z","desc":"适用于iOS和tvOS的优雅过渡库。","publishedAt":"2018-08-13T00:00:00.0Z","source":"chrome","type":"iOS","url":"https://github.com/HeroTransitions/Hero","used":true,"who":"lijinshanmx"},{"_id":"5b710b159d212275a12274df","createdAt":"2018-08-13T12:37:41.486Z","desc":"一个数据驱动的UICollectionView框架，用于构建快速灵活的列表。","publishedAt":"2018-08-13T00:00:00.0Z","source":"chrome","type":"iOS","url":"https://github.com/Instagram/IGListKit","used":true,"who":"lijinshanmx"},{"_id":"5b710be09d212275a78c26ec","createdAt":"2018-08-13T12:41:04.855Z","desc":"Native App over HTTP, on iOS 。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zku7mk1g30rq0flu10"],"publishedAt":"2018-08-13T00:00:00.0Z","source":"chrome","type":"iOS","url":"https://github.com/Jasonette/JASONETTE-iOS","used":true,"who":"lijinshanmx"},{"_id":"5b710c5f9d212275a78c26ee","createdAt":"2018-08-13T12:43:11.229Z","desc":"适用于iOS，watchOS，tvOS和macOS的Keychain的简单Swift包装器。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkudo4fj30yi1pc0wn","https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkv7fwkj30yi1pc446","https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkvcsqzj30hs0vk75h"],"publishedAt":"2018-08-13T00:00:00.0Z","source":"chrome","type":"iOS","url":"https://github.com/kishikawakatsumi/KeychainAccess","used":true,"who":"lijinshanmx"},{"_id":"5b710cb39d212275a00706ba","createdAt":"2018-08-13T12:44:35.886Z","desc":"简单且可自定义的拉动以刷新实现。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkvpo9gg30m80gonpd"],"publishedAt":"2018-08-13T00:00:00.0Z","source":"chrome","type":"iOS","url":"https://github.com/Yalantis/Pull-to-Refresh.Rentals-iOS","used":true,"who":"lijinshanmx"},{"_id":"5b710d369d212275a12274e2","createdAt":"2018-08-13T12:46:46.169Z","desc":"一个炫酷的卡片滑动库。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkvzoz9g308t0fodk8","https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkw4rdfg308t0fo7cr"],"publishedAt":"2018-08-13T00:00:00.0Z","source":"chrome","type":"iOS","url":"https://github.com/cwRichardKim/RKSwipeCards","used":true,"who":"lijinshanmx"}],"休息视频":[{"_id":"5b71028e9d212234189c24cc","createdAt":"2018-08-13T12:01:18.744Z","desc":"这波操作我给满分！！！！！！","publishedAt":"2018-08-13T00:00:00.0Z","source":"api","type":"休息视频","url":"http://v.douyin.com/eGtWgv/","used":true,"who":"lijinshan"}],"拓展资源":[{"_id":"5b70e66c9d21223415f53434","createdAt":"2018-08-13T12:02:23.434Z","desc":"Flutter开发过程的打包流程、APP包对比、细节技巧与问题处理。","publishedAt":"2018-08-13T00:00:00.0Z","source":"web","type":"拓展资源","url":"https://juejin.im/entry/5b6fd5ee6fb9a009d36a4104","used":true,"who":"Shuyu Guo"}],"福利":[{"_id":"5b7102749d2122341d563844","createdAt":"2018-08-13T12:00:52.458Z","desc":"2018-08-13","publishedAt":"2018-08-13T00:00:00.0Z","source":"api","type":"福利","url":"https://ww1.sinaimg.cn/large/0065oQSqly1fu7xueh1gbj30hs0uwtgb.jpg","used":true,"who":"lijinshan"}]}
     */

    @SerializedName("results")
    private ResultsBean results;

    public ResultsBean getResults() {
        return results;
    }

    public void setResults(ResultsBean results) {
        this.results = results;
    }

    public static class ResultsBean {
        @SerializedName("Android")
        private List<AndroidBean> Android;

        public List<AndroidBean> getAndroid() {
            return Android;
        }

        public void setAndroid(List<AndroidBean> Android) {
            this.Android = Android;
        }

        public static class AndroidBean {
            /**
             * _id : 5b6c53359d21226f45755583
             * createdAt : 2018-08-09T22:44:05.597Z
             * desc : 深入理解Android大图片处理
             * publishedAt : 2018-08-13T00:00:00.0Z
             * source : web
             * type : Android
             * url : https://www.jianshu.com/p/1f008671fa44
             * used : true
             * who : 罗占伟
             * images : ["https://ww1.sinaimg.cn/large/0073sXn7ly1fu7zkjh5crg30990fg4qv"]
             */

            @SerializedName("desc")
            private String desc;
            @SerializedName("who")
            private String who;
            @SerializedName("images")
            private List<String> images;

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getWho() {
                return who;
            }

            public void setWho(String who) {
                this.who = who;
            }

            public List<String> getImages() {
                return images;
            }

            public void setImages(List<String> images) {
                this.images = images;
            }

            @Override
            public String toString() {
                return "AndroidBean{" +
                        "desc='" + desc + '\'' +
                        ", who='" + who + '\'' +
                        ", images=" + images +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ResultsBean{" +
                    "Android=" + Android +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GankResponse{" +
                "results=" + results +
                '}';
    }
}
