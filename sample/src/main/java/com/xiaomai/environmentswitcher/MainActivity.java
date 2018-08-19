package com.xiaomai.environmentswitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.xiaomai.environmentswitcher.fragment.HomeFragment;
import com.xiaomai.environmentswitcher.fragment.MusicFragment;
import com.xiaomai.environmentswitcher.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final HomeFragment homeFragment = new HomeFragment();
        final MusicFragment musicFragment = new MusicFragment();
        final SettingsFragment settingsFragment = new SettingsFragment();

        final FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, homeFragment, HomeFragment.class.getSimpleName());
        transaction.commit();

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (checkedId == R.id.radio_home) {
                    transaction.replace(R.id.frame_layout, homeFragment, HomeFragment.class.getSimpleName());
                } else if (checkedId == R.id.radio_music) {
                    transaction.replace(R.id.frame_layout, musicFragment, MusicFragment.class.getSimpleName());
                } else {
                    transaction.replace(R.id.frame_layout, settingsFragment, SettingsFragment.class.getSimpleName());
                }

                transaction.commit();
            }
        });
    }
}
