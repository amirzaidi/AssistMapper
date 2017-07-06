package amirz.applauncher;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;

public class AssistLoggerSession extends VoiceInteractionSession {

    public AssistLoggerSession(Context context) {
        super(context);
    }

    @Override
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content) {
        Log.d("AssistLoggerSession", "onHandleAssist");
        startVoiceActivity(new Intent(getContext(), MainActivity.class).setAction("amirz.applauncher.MAIN").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }
}