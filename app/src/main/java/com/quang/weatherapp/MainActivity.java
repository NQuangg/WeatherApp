package com.quang.weatherapp;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quang.weatherapp.city.City;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "My Tag";
    RequestQueue requestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        TextView tvCityCountry = findViewById(R.id.tvCityCountry);
        TextView tvUpdatedTime = findViewById(R.id.tvUpdatedTime);
        TextView tvWeather = findViewById(R.id.tvWeather);
        TextView tvTemp = findViewById(R.id.tvTemp);
        TextView tvSunrise = findViewById(R.id.tvSunrise);
        TextView tvSunset = findViewById(R.id.tvSunset);
        TextView tvWind = findViewById(R.id.tvWind);
        TextView tvPressure = findViewById(R.id.tvPressure);
        TextView tvHumidity = findViewById(R.id.tvHumidity);
        TextView tvCloud = findViewById(R.id.tvCloud);

        requestQueue = Volley.newRequestQueue(this);
        String url ="http://api.openweathermap.org/data/2.5/weather?q=Hanoi&units=metric&appid=7d5900f6608dfa535fc591a8354c8f0a";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        City city = new Gson().fromJson(response, City.class);
                        String textCityCountry = city.getName() + ", " + city.getSys().getCountry();
                        String textUpdatedTime = "Update: " + new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(city.getDt()*1000L));
                        String textWeather = city.getWeather().get(0).getMain();
                        String textTemp = city.getMain().getTemp() + "°C";
                        String textSunrise = "Sunrise \n" + new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(city.getSys().getSunrise()*1000L));
                        String textSunset = "Sunset \n" + new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(city.getSys().getSunset()*1000L));
                        String textWind = "Wind \n" + city.getWind().getSpeed() + " m/s";
                        String textPressure = "Pressure \n" + city.getMain().getPressure() + " hPa";
                        String textHumidity = "Humidity \n" + city.getMain().getHumidity() + "%";
                        String textCloud = "Cloud \n" + city.getClouds().getAll() + "%";
                        String icon = city.getWeather().get(0).getIcon();

                        tvCityCountry.setText(textCityCountry);
                        tvUpdatedTime.setText(textUpdatedTime);
                        tvWeather.setText(textWeather);
                        tvTemp.setText(textTemp);
                        tvSunrise.setText(textSunrise);
                        tvSunset.setText(textSunset);
                        tvWind.setText(textWind);
                        tvPressure.setText(textPressure);
                        tvHumidity.setText(textHumidity);
                        tvCloud.setText(textCloud);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvTemp.setText(getResources().getString(R.string.error));
            }
        });

        requestQueue.add(stringRequest);
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}