package com.example.percival.coursera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.net.URI;
import java.text.DecimalFormat;

public class MainActivity extends Activity{

    public static Activity instance = null;
    public static String currentCity = "London";
    final String TEMPERATURE_URI_STRING = "content://com.example.percival/temperature";
    private boolean loop = false;
    public static final String CHANGE_WEATHER= "com.example.percival.coursera.CHANGE_WEATHER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        String cityFromIntent = getIntent().getStringExtra("city");
        if (cityFromIntent != null){
            currentCity = cityFromIntent;
        }
        System.out.println(cityFromIntent);
        startService(new Intent(this, MyService.class));
        loop = true;
        updateData();
    }

    private Uri getUri(){
        return Uri.parse(TEMPERATURE_URI_STRING+"/"+currentCity);
    }

    private void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (loop) {
                    try {
                        Log.d("MainActivity","updateData");
                        Cursor cursor = getContentResolver().query(getUri(), null, null, null, null);
                        Integer i = cursor.getCount();
                        //Log.d("Found in database", i.toString());
                        while (cursor.moveToNext()) {
                            Double temp = cursor.getFloat(cursor.getColumnIndex("temperature")) - 273.15;
                            Intent intent = new Intent();
                            intent.setAction(CHANGE_WEATHER);
                            intent.putExtra("temp",temp.toString());
                            intent.putExtra("city",currentCity);
                            sendBroadcast(intent);
                        }
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void settingsButtonClicked(View v){
                Log.d("MainActivity","SettingButtonClicked");
                loop = false;
                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity","onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity","onPause");
        super.onPause();
        //onDestroy();
    }
}
