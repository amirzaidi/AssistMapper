package amirz.assistmapper;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PickerList extends ArrayAdapter<PickerInfo> {
    private List<PickerInfo> mFiltered;
    private List<PickerInfo> mFull;
    private String mFilter = "";

    public PickerList(@NonNull Context context, List<PickerInfo> filtered) {
        super(context, R.layout.item_picker_info, filtered);
        mFiltered = filtered;
        mFull = new ArrayList<>();
    }

    public void setFilter(String filter) {
        mFilter = filter.toLowerCase();
        doFilter();
    }

    public void addAndFilter(List<PickerInfo> newInfos) {
        mFull.addAll(newInfos);
        doFilter();
    }

    public void doFilter() {
        mFiltered.clear();
        if (mFilter.isEmpty()) {
            mFiltered.addAll(mFull);
        } else {
            for (PickerInfo info : mFull) {
                if (info.contains(mFilter)) {
                    mFiltered.add(info);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.item_picker_info, null);
        }

        PickerInfo pickerInfo = getItem(position);
        if (pickerInfo != null) {
            new ImageAsyncTask((ImageView)convertView.findViewById(R.id.imageView)).execute(pickerInfo);

            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText(pickerInfo.labelName);
            textView.setTypeface(null, pickerInfo.launcher ? Typeface.BOLD : Typeface.NORMAL);
            textView = convertView.findViewById(R.id.textView2);
            textView.setText(pickerInfo.packageName);
            textView = convertView.findViewById(R.id.textView3);
            textView.setText(pickerInfo.activityName);
        }

        return convertView;
    }

    private static class ImageAsyncTask extends AsyncTask<PickerInfo, Void, Object> {
        private final ImageView mImageView;

        private ImageAsyncTask(ImageView imageView) {
            mImageView = imageView;
        }

        protected Object doInBackground(PickerInfo... pickerInfos) {
            PickerInfo pi = pickerInfos[0];
            if (pi.iconRes != 0 && pi.resources != null) {
                try {
                    return pi.resources.getDrawableForDensity(pi.iconRes, DisplayMetrics.DENSITY_XHIGH, null);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(Object result) {
            mImageView.setImageDrawable((Drawable)result);
        }
    }
}
