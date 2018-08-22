package com.xiaomai.environmentswitcher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomai.environmentswitcher.bean.EnvironmentConfigBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentSwitchActivity extends AppCompatActivity {

    private static final int TYPE_MODULE = 0;

    private static final int TYPE_ENVIRONMENT = 1;

    public static void launch(Context context) {
        Intent intent = new Intent(context, EnvironmentSwitchActivity.class);
        context.startActivity(intent);
    }

    private List<EnvironmentConfigBean.ModuleBean.EnvironmentBean> environmentBeans = new ArrayList<>();
    private Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (getItemViewType(i) == TYPE_MODULE) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.environment_switcher_item_module, viewGroup, false);
                return new ModuleHolder(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.environment_switcher_item_environment, viewGroup, false);
                return new EnvironmentHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = getItemViewType(viewHolder.getAdapterPosition());
            final EnvironmentConfigBean.ModuleBean.EnvironmentBean environmentBean = getItem(viewHolder.getAdapterPosition());

            if (itemViewType == TYPE_MODULE) {
                ModuleHolder holder = (ModuleHolder) viewHolder;
                String moduleName = environmentBean.getModuleName();
                String alias = environmentBean.getAlias();
                holder.tvName.setText(alias.isEmpty() ? moduleName : alias);
            } else {
                EnvironmentHolder holder = (EnvironmentHolder) viewHolder;
                holder.tvUrl.setText(environmentBean.getUrl());
                String alias = environmentBean.getAlias();
                holder.tvName.setText(alias.isEmpty() ? environmentBean.getName() : alias);
                holder.ivMark.setVisibility(environmentBean.isChecked() ? View.VISIBLE : View.INVISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        }

        @Override
        public int getItemCount() {
            return environmentBeans.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (getItem(position).getName().isEmpty()) {
                return TYPE_MODULE;
            } else {
                return TYPE_ENVIRONMENT;
            }
        }

        EnvironmentConfigBean.ModuleBean.EnvironmentBean getItem(int position) {
            return environmentBeans.get(position);
        }
    }

    static class EnvironmentHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvUrl;
        ImageView ivMark;

        private EnvironmentHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUrl = itemView.findViewById(R.id.tv_url);
            ivMark = itemView.findViewById(R.id.iv_mark);
        }
    }

    static class ModuleHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        private ModuleHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
