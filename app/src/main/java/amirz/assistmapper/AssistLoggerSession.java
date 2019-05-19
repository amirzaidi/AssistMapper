package amirz.assistmapper;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;

public class AssistLoggerSession extends VoiceInteractionSession {
    private static final String TAG = "AssistLoggerSession";

    public AssistLoggerSession(Context context) {
        super(context);
    }

    @Override
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content) {
        Log.d(TAG, "onHandleAssist");
        try {
            startVoiceActivity(new Intent(getContext(), AssistLaunchActivity.class)
                    .setAction("amirz.assistmapper.MAIN")
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
