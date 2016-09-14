package com.chornyiya.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MyService.class);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            intent1.putExtra("pkg","boot");
        }
        Log.d("TAG","Start receiver");
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
            intent1.putExtra("pkg",intent.getData().getEncodedSchemeSpecificPart());
            Log.d("TAG","Send intent from receiver");
        }
        context.startService(intent1);

    }
}
