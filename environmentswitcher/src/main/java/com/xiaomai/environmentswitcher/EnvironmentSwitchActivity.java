package com.xiaomai.environmentswitcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaomai.environmentswitcher.bean.EnvironmentConfigBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentSwitchActivity extends Activity {

    private static final int TYPE_MODULE = 0;

    private static final int TYPE_ENVIRONMENT = 1;

    public static void launch(Context context) {
        Intent intent = new Intent(context, EnvironmentSwitchActivity.class);
        context.startActivity(intent);
    }

    private List<EnvironmentConfigBean.ModuleBean.EnvironmentBean> environmentBeans = new ArrayList<>();
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.environment_switcher_activity);
        findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            Class<?> environmentSwitcherClass = Class.forName(Constants.PACKAGE_NAME + "." + Constants.ENVIRONMENT_SWITCHER_FILE_NAME);
            Method getEnvironmentConfigMethod = environmentSwitcherClass.getMethod("getEnvironmentConfig");
            EnvironmentConfigBean environmentConfigBean = (EnvironmentConfigBean) getEnvironmentConfigMethod.invoke(environmentSwitcherClass.newInstance());
            if (environmentConfigBean == null) {
                return;
            }
            List<EnvironmentConfigBean.ModuleBean> modules = environmentConfigBean.getModules();
            ArrayList<EnvironmentConfigBean.ModuleBean.EnvironmentBean> environmentBeans = new ArrayList<>();
            for (EnvironmentConfigBean.ModuleBean module : modules) {
                EnvironmentConfigBean.ModuleBean.EnvironmentBean environmentModule = new EnvironmentConfigBean.ModuleBean.EnvironmentBean();
                environmentModule.setAlias(module.getAlias());
                environmentModule.setModuleName(module.getName());
                environmentBeans.add(environmentModule);
                environmentBeans.addAll(module.getEnvironments());
            }
            this.environmentBeans = environmentBeans;
            for (EnvironmentConfigBean.ModuleBean.EnvironmentBean environmentBean : this.environmentBeans) {
                Method getXXEnvironmentMethod = environmentSwitcherClass.getMethod("get" + environmentBean.getModuleName() + "Environment", Context.class, boolean.class);
                String environment = (String) getXXEnvironmentMethod.invoke(environmentSwitcherClass.newInstance(), this, true);
                environmentBean.setChecked(environment.equals(environmentBean.getUrl()));
            }
            ListView recyclerView = findViewById(R.id.list_view);
            adapter = new Adapter();
            recyclerView.setAdapter(adapter);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    class Adapter extends BaseAdapter {
        @Override
        public int getCount() {
            return environmentBeans.size();
        }

        @Override
        public EnvironmentConfigBean.ModuleBean.EnvironmentBean getItem(int position) {
            return environmentBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final EnvironmentConfigBean.ModuleBean.EnvironmentBean environmentBean = getItem(position);

            if (getItemViewType(position) == TYPE_MODULE) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.environment_switcher_item_module, parent, false);
                TextView tvName = convertView.findViewById(R.id.tv_name);

                String moduleName = environmentBean.getModuleName();
                String alias = environmentBean.getAlias();
                tvName.setText(TextUtils.isEmpty(alias) ? moduleName : alias);
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.environment_switcher_item_environment, parent, false);
                TextView tvName = convertView.findViewById(R.id.tv_name);
                TextView tvUrl = convertView.findViewById(R.id.tv_url);
                ImageView ivMark = convertView.findViewById(R.id.iv_mark);

                tvUrl.setText(environmentBean.getUrl());
                String alias = environmentBean.getAlias();
                tvName.setText(TextUtils.isEmpty(alias) ? environmentBean.getName() : alias);
                ivMark.setVisibility(environmentBean.isChecked() ? View.VISIBLE : View.INVISIBLE);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Class<?> environmentSwitcher = Class.forName(Constants.PACKAGE_NAME + "." + Constants.ENVIRONMENT_SWITCHER_FILE_NAME);
                            Method method = environmentSwitcher.getMethod("set" + environmentBean.getModuleName() + "Environment", Context.class, String.class);
                            method.invoke(environmentSwitcher.newInstance(), EnvironmentSwitchActivity.this, environmentBean.getUrl());
                            for (EnvironmentConfigBean.ModuleBean.EnvironmentBean bean : environmentBeans) {
                                if (bean.getModuleName().equals(environmentBean.getModuleName())) {
                                    bean.setChecked(bean.getUrl().equals(environmentBean.getUrl()));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            if (TextUtils.isEmpty(getItem(position).getName())) {
                return TYPE_MODULE;
            } else {
                return TYPE_ENVIRONMENT;
            }
        }
    }
}
