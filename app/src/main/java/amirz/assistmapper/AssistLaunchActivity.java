package amirz.assistmapper;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AssistLaunchActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String DEFAULT_PKG = "com.google.android.googlequicksearchbox";
    private static final String DEFAULT_ACT = "com.google.android.googlequicksearchbox.SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Launching mapped activity");
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("launch", 0);
        Intent intent = new Intent()
                .setComponent(new ComponentName(sharedPreferences.getString("package", DEFAULT_PKG),
                        sharedPreferences.getString("activity", DEFAULT_ACT)))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
