package com.xiaomai.demo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaomai.demo.R;
import com.xiaomai.demo.data.Api;
import com.xiaomai.demo.data.GankResponse;
import com.xiaomai.demo.net.AppRetrofit;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private ListView listView;
    private TextView tvError;
    private MyAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<GankResponse.ResultsBean.AndroidBean> ganks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvError = view.findViewById(R.id.tv_error);
        listView = view.findViewById(R.id.list_view);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGanks();
            }
        });
    }

    private void getGanks() {
        AppRetrofit.getAppRetrofit(getActivity().getApplication())
                .create(Api.class)
                .getGank()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(GankResponse value) {
                        Log.e(TAG, "onNext: " + value.toString());
                        ganks = value.getResults().getAndroid();
                        adapter.notifyDataSetChanged();
                        tvError.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        swipeRefreshLayout.setRefreshing(false);
                        tvError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        getGanks();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ganks.size();
        }

        @Override
        public GankResponse.ResultsBean.AndroidBean getItem(int position) {
            return ganks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gank, parent, false);
                holder = new Holder();
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                holder.tvWho = convertView.findViewById(R.id.tv_who);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvTitle.setText(getItem(position).getDesc());
            holder.tvWho.setText(getItem(position).getWho());
            return convertView;
        }

        private class Holder {
            TextView tvTitle;
            TextView tvWho;
        }
    }
}
