package amirz.assistmapper;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.android.launcher3.plugin.button.IButtonPlugin;
import com.android.launcher3.plugin.button.IButtonPluginCallback;

public class ButtonService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new IButtonPlugin.Stub() {
            @SuppressLint({"PrivateApi", "WrongConstant"})
            @Override
            public boolean onHomeIntent(IButtonPluginCallback cb) throws RemoteException {
                SharedPreferences sharedPreferences = getSharedPreferences("launch", 0);
                String pkg = sharedPreferences.getString("package", "");
                String act = sharedPreferences.getString("activity", "");
                if (TextUtils.isEmpty(pkg) || TextUtils.isEmpty(act)) {
                    try {
                        Class<?> cls = Class.forName("android.app.StatusBarManager");
                        Object srv = getSystemService("statusbar");
                        cls.getMethod("expandNotificationsPanel").invoke(srv);
                        return true;
                    } catch (Exception ignored) {
                        return false;
                    }
                }
                Intent intent = new Intent()
                        .setComponent(new ComponentName(pkg, act))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return cb.startActivity(intent.toUri(Intent.URI_INTENT_SCHEME));
            }
        };
    }
}
