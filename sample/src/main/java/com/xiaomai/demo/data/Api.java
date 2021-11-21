package com.xiaomai.demo.data;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface Api {

    @GET("music/musicDetail")
    Single<MusicResponse> getMusic();

    @GET("wxarticle/chapters/json")
    Single<GankResponse> getGank();
}
