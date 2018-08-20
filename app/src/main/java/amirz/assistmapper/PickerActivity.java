package amirz.assistmapper;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import amirz.applauncher.R;

public class PickerActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static PickerList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new PickerList(this, new ArrayList<PickerInfo>());
        ListView list = findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final SharedPreferences.Editor edit = getSharedPreferences("launch", 0).edit();
                edit.putString("package", ((TextView) view.findViewById(R.id.textView2)).getText().toString());
                edit.putString("activity", ((TextView) view.findViewById(R.id.textView3)).getText().toString());
                edit.apply();

                Snackbar.make(findViewById(R.id.picker_page), ((TextView) view.findViewById(R.id.textView)).getText() + " will now open by default", Snackbar.LENGTH_LONG)
                        .setAction("Reset", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                edit.clear();
                                edit.apply();
                            }
                        })
                        .show();
            }
        });

        new InfoAsyncTask(this).execute();

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView search = findViewById(R.id.search);
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(this);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) search.findViewById(R.id.search_edit_frame).getLayoutParams();
        lp.leftMargin = 0;

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String assistant = Settings.Secure.getString(getContentResolver(), "voice_interaction_service");
        if (assistant != null) {
            ComponentName cn = ComponentName.unflattenFromString(assistant);
            if (cn.getPackageName().equals(getPackageName())) {
                return;
            }
        }

        Toast.makeText(this, R.string.set_assist, Toast.LENGTH_LONG).show();
        startActivity(new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.setFilter(newText);
        return true;
    }

    private static class InfoAsyncTask extends AsyncTask<Object, Object, Object> {
        private final Activity mActivity;

        private InfoAsyncTask(Activity activity) {
            mActivity = activity;
        }

        protected List<PickerInfo> doInBackground(Object... obj) {
            PackageManager pm = mActivity.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);

            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<String> launcher = new ArrayList<>();
            for (ResolveInfo ri : pm.queryIntentActivities(mainIntent, 0)) {
                if (ri.activityInfo != null) {
                    launcher.add(ri.activityInfo.name);
                }
            }

            int i = 0;
            for (PackageInfo packageInfo : packages) {
                ArrayList<PickerInfo> list = new ArrayList<>();
                Resources r = null;
                try {
                    r = mActivity.createPackageContext(packageInfo.packageName, Context.CONTEXT_IGNORE_SECURITY).getResources();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                if (packageInfo.activities != null) {
                    for (ActivityInfo activityInfo : packageInfo.activities) {
                        PickerInfo info = new PickerInfo();
                        info.packageName = activityInfo.packageName;
                        info.activityName = activityInfo.name;
                        info.resources = r;
                        info.iconRes = activityInfo.icon == 0 ? packageInfo.applicationInfo.icon : activityInfo.icon;
                        info.labelName = activityInfo.loadLabel(pm).toString();
                        info.launcher = launcher.contains(activityInfo.name);
                        list.add(info);
                    }
                }
                publishProgress(packages.size(), ++i, list);
            }

            return null;
        }

        protected void onProgressUpdate(Object... progress) {
            ProgressBar progressBar = mActivity.findViewById(R.id.progressBar);
            progressBar.setMax((Integer) progress[0]);
            progressBar.setProgress((Integer) progress[1]);
            adapter.addAndFilter((List<PickerInfo>) progress[2]);
        }

        protected void onPostExecute(Object result) {
            FrameLayout view = mActivity.findViewById(R.id.progressWrapper);
            view.startAnimation(new CollapseHeightAnimation(view, 200L));
        }
    }

    public static class CollapseHeightAnimation extends Animation {
        private final int originalHeight;
        private final View view;
        private float perValue;

        private CollapseHeightAnimation(View view, long durationMillis) {
            this.view = view;
            this.originalHeight = view.getHeight();
            this.perValue = -originalHeight;
            setDuration(durationMillis);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            view.getLayoutParams().height = (int) (originalHeight + perValue * interpolatedTime);
            view.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
