package amirz.applauncher;

import android.content.Context;
import android.support.annotation.NonNull;
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
                if (info.activityName.toLowerCase().contains(mFilter) || info.labelName.toLowerCase().contains(mFilter) || info.packageName.toLowerCase().contains(mFilter)) {
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
            ImageView imageView = convertView.findViewById(R.id.imageView);
            imageView.setImageDrawable(pickerInfo.icon);
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText(pickerInfo.labelName);
            textView = convertView.findViewById(R.id.textView2);
            textView.setText(pickerInfo.packageName);
            textView = convertView.findViewById(R.id.textView3);
            textView.setText(pickerInfo.activityName);
        }

        return convertView;
    }
}
