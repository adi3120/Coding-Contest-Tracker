package com.example.contesttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.contesttracker.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private TextView data;
    private Button getDataButton;
    public int count=0;

    public String prettyJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data=(TextView) findViewById(R.id.data);
        getDataButton=(Button) findViewById(R.id.btnData);
        data.setMovementMethod(new ScrollingMovementMethod());


        getDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://codeforces.com/api/contest.list";

//        data.setText(url);

                RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());


                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray result= (JSONArray) response.get("result");

                                    String mainstring = "";
                                    for(int i=0;i<result.length();i++){
                                        JSONObject rec=result.getJSONObject(i);
                                        String phase=rec.getString("phase");
                                        if(phase.equals("BEFORE")){
                                            mainstring+=(i+1)+" : ";
                                            mainstring+=rec.getString("name");
                                            mainstring+=" ON <b>";

                                            long unix_seconds=rec.getLong("startTimeSeconds");
                                            Date date = new Date(unix_seconds*1000L);



                                            // format of the date
                                            SimpleDateFormat jdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
                                            jdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                                            String java_date = jdf.format(date);



                                            mainstring+="<u>"+java_date+"</u>";
                                            mainstring+="</b>";
                                            mainstring+="<br><br>";
                                        }
                                    }
                                    data.setText(Html.fromHtml(mainstring));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                JsonElement je = JsonParser.parseString(response.toString());
                                prettyJsonString = gson.toJson(je);

//                                data.setText(prettyJsonString);



                                try {
                                    JSONArray jsonArray = response.getJSONArray("results");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String s=JSON_object_to_GSON_String(jsonObject);


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                        // hide the progress dialog
                        data.setText(error.getMessage());
                    }
                });
                requestQueue.add(jsonObjReq);

            }
        });



    }

    public String JSON_object_to_GSON_String(JSONObject ob){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(ob.toString());
        String prettyString = gson.toJson(je);
        return prettyString;
    }

    public static String findDay(int month, int day, int year) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");

        Date date = new GregorianCalendar(year, month - 1, day).getTime();
        String dayText = simpleDateformat.format(date);

        return dayText.toUpperCase();
    }

    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    //Convert Calendar to Date
    private Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }



}