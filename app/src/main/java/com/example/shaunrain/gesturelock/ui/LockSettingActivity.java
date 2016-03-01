package com.example.shaunrain.gesturelock.ui;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.shaunrain.gesturelock.model.AppInfo;
import com.example.shaunrain.gesturelock.MyApplication;
import com.example.shaunrain.gesturelock.R;
import com.example.shaunrain.gesturelock.service.RunningTaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockSettingActivity extends AppCompatActivity {

    private MyApplication myApplication = null;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private FragmentManager mFragmentManager;
    private AppFragment app_frag = new AppFragment();
    private MediaFragment media_frag = new MediaFragment();
    private List<AppInfo> appList = new ArrayList<>();
    private PackageManager packageManager;
    private Switch switcher;
    public static FloatingActionButton fab;
    private Intent taskService;
    private SharedPreferences sp;
    private Set<String> lockApps;
    public static AppInfoAdapter adapter;
    //private ListView appListView;
    private HashMap<String, Integer> appDict;
    private HashMap<Integer, Boolean> checkList;

    public static boolean openMedia = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lock Settings");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        startActivity(new Intent(LockSettingActivity.this, CrystalActivity.class));
                        myApplication.change = true;
                        break;
                }
                return true;
            }
        });

        taskService = new Intent(this, RunningTaskService.class);
        switcher = (Switch) findViewById(R.id.switcher);
        sp = getSharedPreferences("lock_data", Context.MODE_PRIVATE);
        switcher.setChecked(sp.getBoolean("islock", false));
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(taskService);

                    Snackbar.make(buttonView, "服务已开启!", Snackbar.LENGTH_SHORT).show();
                } else {
                    stopService(taskService);
                    Snackbar.make(buttonView, "服务已关闭!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LayoutInflater inflater = LayoutInflater.from(LockSettingActivity.this);
                View alertView = inflater.inflate(R.layout.alert_jump, null);
                final EditText jumpapp = (EditText) alertView.findViewById(R.id.jumpname);
                new AlertDialog.Builder(LockSettingActivity.this).setTitle("查找应用").setView(alertView)
                        .setPositiveButton("跳转", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String appname = jumpapp.getText().toString().trim();
                                if (appDict.containsKey(appname)) {
                                    app_frag.appListView.setSelection(appDict.get(appname));
                                    adapter.notifyDataSetInvalidated();
                                } else {
                                    Snackbar.make(view, "没这个应用", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
            }
        });

        initContent();

        myApplication = (MyApplication) getApplication();
        if (sp.getString("password", null) == null) {
            myApplication.first = true;
            Intent toLock = new Intent(LockSettingActivity.this, CrystalActivity.class);
            startActivity(toLock);
        } else if (myApplication.common) {
            Intent toLock = new Intent(LockSettingActivity.this, CrystalActivity.class);
            toLock.putExtra("needback", true);
            startActivity(toLock);
        }

    }

    private void initContent() {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        mViewPager.setAdapter(mViewPagerAdapter);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        fab.measure(w, h);
        final int leave = fab.getMeasuredWidth() + fab.getPaddingRight();
        final int back = fab.getPaddingRight();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private boolean isOut = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if (isOut) {
                            ObjectAnimator.ofFloat(fab, "translationX", -back).setDuration(500).start();
                            isOut = false;
                        }
                        break;
                    case 1:
                        if (!isOut) {
                            ObjectAnimator.ofFloat(fab, "translationX", leave).setDuration(500).start();
                            isOut = true;
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        appDict = new HashMap<>();
        int position = 0;
        lockApps = sp.getStringSet("lockapps", new HashSet<String>());
        packageManager = getPackageManager();
        List<ApplicationInfo> infos = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo info : infos) {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                AppInfo appInfo = new AppInfo(info.loadLabel(packageManager).toString(),
                        info.packageName, info.loadIcon(packageManager));
                if (lockApps.contains(info.packageName)) {
                    appInfo.setIsLock(true);
                }
                appList.add(appInfo);
                appDict.put(appInfo.getLabel(), position++);
            }
        }
        adapter = new AppInfoAdapter(LockSettingActivity.this, R.layout.app_item, appList);
        //appListView = (ListView) getLayoutInflater().inflate(R.layout.fragment_app, null).findViewById(R.id.applist);
        //appListView = (ListView) findViewById(R.id.applist);
        //appListView.setAdapter(adapter);
        //appListView = app_frag.appListView;
        checkList = new HashMap<>();
    }

    private class AppInfoAdapter extends ArrayAdapter<AppInfo> {

        private class ViewHolder {
            ImageView icon;
            TextView label;
            CheckBox islock;
        }

        private int resourceId;

        public AppInfoAdapter(Context context, int resource, List<AppInfo> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder = null;
            final AppInfo appInfo = getItem(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
                viewHolder.label = (TextView) view.findViewById(R.id.name);
                viewHolder.islock = (CheckBox) view.findViewById(R.id.islock);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.islock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String packageName = appInfo.getPackagename();
                    SharedPreferences.Editor editor = sp.edit();
                    Set<String> temp = sp.getStringSet("lockapps", new HashSet<String>());
                    if (isChecked && !temp.contains(packageName)) {
                        temp.add(packageName);
                        editor.putStringSet("lockapps", temp);
                        editor.commit();
                        checkList.put(position, true);
                    } else if (!isChecked && temp.contains(packageName)) {
                        temp.remove(packageName);
                        editor.putStringSet("lockapps", temp);
                        editor.commit();
                        checkList.remove(position);
                    }
                }
            });
            viewHolder.icon.setImageDrawable(appInfo.getIcon());
            viewHolder.label.setText(appInfo.getLabel());
            viewHolder.islock.setChecked(appInfo.isLock());
            if (checkList.containsKey(position))
                viewHolder.islock.setChecked(true);
            return view;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myApplication.common = true;
        finish();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (!openMedia) {
            myApplication.common = true;
            finish();
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return app_frag;
                case 1:
                    return media_frag;
            }
            throw new IllegalStateException("No fragment at position " + position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "应用";
                case 1:
                    return "媒体";
                default:
                    return null;
            }
        }
    }
}
