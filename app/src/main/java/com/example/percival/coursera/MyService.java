package com.example.percival.coursera;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service {
    final Uri TEMPERATURE_URI = Uri.parse("content://com.example.percival/temperature");
    private static final String CITY = "city";
    private static final String TEMPERATURE = "temperature";
    private final String[] CITIES = {"London", "Washington", "Beijing", "Moscow", "Berlin"};
    @Override
    public void onCreate() {
        Log.d("MyService","onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getData();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public MyService() {
        super();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String buildUrl(){
        return "http://api.openweathermap.org/data/2.5/weather?q="+MainActivity.currentCity+"&APPID=f3ff8a73074a8cea096b56fba2e48bba";
    }

    public void getData(){
        Log.d("MyService","GetData");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                buildUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.names();
                            obj = new JSONObject(obj.get("main").toString());
                            Float temp = new Float(obj.get("temp").toString());
                            ContentValues cv = new ContentValues();
                            switch (isDatabaseExist()) {
                                case 0:
                                    for (String s:CITIES){
                                        cv = new ContentValues();
                                        cv.put(CITY,s);
                                        cv.put(TEMPERATURE, 0);
                                        Uri newUri = getContentResolver().insert(TEMPERATURE_URI,cv);
                                    }
                                default:
                                    Log.d("Switch",MainActivity.currentCity+" "+temp);
                                    cv = new ContentValues();
                                    cv.put(CITY,MainActivity.currentCity);
                                    cv.put(TEMPERATURE, temp);
                                    getContentResolver().update(TEMPERATURE_URI, cv, "city=\""+MainActivity.currentCity+"\"", null);
                                    Intent intent = new Intent();
                                    temp -= 273.15f;
                                    intent.setAction(MainActivity.CHANGE_WEATHER);
                                    intent.putExtra("temp",temp.toString());
                                    intent.putExtra("city",MainActivity.currentCity);
                                    sendBroadcast(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private int isDatabaseExist(){
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(TEMPERATURE_URI,null, null, null,null);
        if (cursor!=null){
            return cursor.getCount();
        }
        return 0;
    }
}