package com.xiaomai.demo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomai.demo.BuildConfig;
import com.xiaomai.demo.R;
import com.xiaomai.environmentswitcher.EnvironmentSwitchActivity;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!BuildConfig.DEBUG) {
            view.findViewById(R.id.bt_switch_environment).setVisibility(View.GONE);
            return;
        }

        view.findViewById(R.id.bt_switch_environment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnvironmentSwitchActivity.launch(getContext());
            }
        });
    }
}
