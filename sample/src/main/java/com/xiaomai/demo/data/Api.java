package com.xiaomai.demo.data;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface Api {

    @GET("music/musicDetail")
    Observable<MusicResponse> getMusic();

    @GET("today")
    Observable<GankResponse> getGank();
}
