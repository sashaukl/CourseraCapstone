package com.example.percival.coursera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SettingsActivity extends Activity {
    private final String CHANGE_CITY = "com.example.percival.coursera.CHANGE_CITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SettingsActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    private void createIntent(String city){
        Log.d("SettingsActivity", "londonButtonClicked");
        Intent intent = new Intent();
        intent.setAction(CHANGE_CITY);
        intent.putExtra("city",city);
        sendBroadcast(intent);
        finish();
    }

    public void londonButtonClicked(View v ){
        createIntent("London");
    }
    public void washingtonButtonClicked(View v ){
        createIntent("Washington");
    }
    public void moscowButtonClicked(View v ){
        createIntent("Moscow");
    }
    public void beijingButtonClicked(View v ){
        createIntent("Beijing");
    }
    public void berlinButtonClicked(View v ){
        createIntent("Berlin");
    }
}
