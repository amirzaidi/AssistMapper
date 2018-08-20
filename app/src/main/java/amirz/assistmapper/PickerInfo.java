package amirz.assistmapper;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class PickerInfo {
    public String packageName;
    public String activityName;
    public Resources resources;
    public int iconRes;
    public String labelName;
    public boolean launcher;

    public boolean contains(String search) {
        return activityName.toLowerCase().contains(search) ||
                labelName.toLowerCase().contains(search) ||
                packageName.toLowerCase().contains(search);
    }
}