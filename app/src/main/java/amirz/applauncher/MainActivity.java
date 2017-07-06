package amirz.applauncher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate");
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("launch", 0);
        startActivity(new Intent()
                .setComponent(new ComponentName(sharedPreferences.getString("package", "com.google.android.googlequicksearchbox"), sharedPreferences.getString("activity", "com.google.android.googlequicksearchbox.SearchActivity")))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));

    }
}
