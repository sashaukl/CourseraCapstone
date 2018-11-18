package com.example.percival.coursera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyReceiver",intent.getAction());
        if ("com.example.percival.coursera.CHANGE_CITY".equals(intent.getAction())){
            Log.d("SettingsActivity", "londonButtonClicked");
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("city",intent.getStringExtra("city"));
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else if ("com.example.percival.coursera.CHANGE_WEATHER".equals(intent.getAction())){
            Float f = new Float(intent.getStringExtra("temp"));
            String city =  intent.getStringExtra("city");
            if (MainActivity.instance!=null) {
                ((TextView) MainActivity.instance.findViewById(R.id.temperature)).setText(Math.round(f) + " C");
                if (city!=null){
                    ((TextView) MainActivity.instance.findViewById(R.id.city)).setText(city);
                }
            }
        }
    }
}
