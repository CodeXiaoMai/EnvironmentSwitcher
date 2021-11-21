package com.xiaomai.demo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaomai.demo.BuildConfig;
import com.xiaomai.demo.R;
import com.xiaomai.demo.data.Api;
import com.xiaomai.demo.data.MusicResponse;
import com.xiaomai.demo.net.AppRetrofit;
import com.xiaomai.environmentswitcher.EnvironmentSwitcher;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MusicFragment extends Fragment {

    private static final String TAG = "MusicFragment";

    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.music_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getMusicList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        textView.setText(EnvironmentSwitcher.getMusicEnvironment(getContext(), BuildConfig.DEBUG));
//        getMusicList();
    }

    private void getMusicList() {
        AppRetrofit.getMusicRetrofit(getActivity()).create(Api.class)
                .getMusic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<MusicResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                        textView.setText(R.string.loading);
                    }

                    @Override
                    public void onSuccess(@NonNull MusicResponse musicResponse) {
                        Log.e(TAG, "onSuccess: " + musicResponse);
                        textView.setText(musicResponse.getValue());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        textView.setText(R.string.load_fail_click_reload);
                    }
                });
    }
}
