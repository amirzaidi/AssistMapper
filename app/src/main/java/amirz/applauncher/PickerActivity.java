package amirz.applauncher;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class PickerActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static PickerList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new PickerList(this, new ArrayList<PickerInfo>());
        ListView list = (ListView)findViewById(R.id.listView);
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
        new InfoAsyncTask().execute(this);

        SearchView search = (SearchView) findViewById(R.id.search);
        search.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(this);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) search.findViewById(R.id.search_edit_frame).getLayoutParams();
        lp.leftMargin = 0;

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    class InfoAsyncTask extends AsyncTask<Context, Object, Object> {

        protected List<PickerInfo> doInBackground(Context... context) {
            PackageManager pm = context[0].getPackageManager();

            List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            int i = 0;
            for (PackageInfo packageInfo : packages) {
                ArrayList<PickerInfo> list = new ArrayList<>();
                if (packageInfo.activities != null) {
                    for (ActivityInfo activityInfo : packageInfo.activities) {
                        PickerInfo info = new PickerInfo();
                        info.packageName = activityInfo.packageName;
                        info.activityName = activityInfo.name;
                        info.icon = activityInfo.loadIcon(pm);
                        info.labelName = activityInfo.loadLabel(pm).toString();
                        list.add(info);
                    }
                }
                publishProgress(packages.size(), ++i, list);
            }

            return null;
        }

        protected void onProgressUpdate(Object... progress) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setMax((Integer)progress[0]);
            progressBar.setProgress((Integer)progress[1]);
            adapter.addAndFilter((List<PickerInfo>)progress[2]);
        }

        protected void onPostExecute(Object result) {
            FrameLayout view = (FrameLayout) findViewById(R.id.progressWrapper);
            view.startAnimation(new CollapseHeightAnimation(view, 200L));
        }
    }

    public class CollapseHeightAnimation extends Animation {
        protected final int originalHeight;
        protected final View view;
        protected float perValue;

        public CollapseHeightAnimation(View view, long durationMillis) {
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
