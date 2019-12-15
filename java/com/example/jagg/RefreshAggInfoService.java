package com.example.jagg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RefreshAggInfoService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("debug_ Alarm Bell","Alarm just fired");

        //FileTool fileTool = new FileTool();
        //fileTool.writeRefreshTime();
    }
}
