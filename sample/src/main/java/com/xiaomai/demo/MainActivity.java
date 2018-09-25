package com.xiaomai.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.xiaomai.demo.fragment.HomeFragment;
import com.xiaomai.demo.fragment.MusicFragment;
import com.xiaomai.demo.fragment.SettingsFragment;
import com.xiaomai.environmentswitcher.EnvironmentSwitcher;
import com.xiaomai.environmentswitcher.bean.EnvironmentBean;
import com.xiaomai.environmentswitcher.bean.ModuleBean;
import com.xiaomai.environmentswitcher.listener.OnEnvironmentChangeListener;


public class MainActivity extends AppCompatActivity implements OnEnvironmentChangeListener{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setTitle(getString(R.string.app_name));

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

        EnvironmentSwitcher.addOnEnvironmentChangeListener(this);
    }

    @Override
    public void onEnvironmentChanged(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
        Log.e(TAG, "Module=" + module.getName() + ",\nOldEnvironment=" + oldEnvironment.getName() + ",\noldUrl=" + oldEnvironment.getUrl()
                + ",\nnewEnvironment=" + newEnvironment.getName() + ",\nnewUrl=" + newEnvironment.getUrl());

        Toast.makeText(this, "Module=" + module.getName() + ",\nOldEnvironment=" + oldEnvironment.getName() + ",\noldUrl=" + oldEnvironment.getUrl()
                + ",\nnewEnvironment=" + newEnvironment.getName() + ",\nnewUrl=" + newEnvironment.getUrl(), Toast.LENGTH_SHORT).show();

        if (module.equals(EnvironmentSwitcher.MODULE_MUSIC)) {
            // 如果环境切换后重新请求的接口需要 token，可以通过 postDelay 在延迟一定时间后再请求
            // if the request need token, you can send in postDelay.
            long delayTime = 1500;
            findViewById(R.id.frame_layout).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 发送需要 token 参数的接口请求
                    // send the request need token
                    Log.e(TAG, "run: send request");

                    Toast.makeText(MainActivity.this, "send request", Toast.LENGTH_SHORT).show();
                }
            }, delayTime);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EnvironmentSwitcher.removeOnEnvironmentChangeListener(this);
    }
}
