package com.quang.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.quang.weatherapp.city.City;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "My Tag";
    private FusedLocationProviderClient mFusedLocationClient;

    private TextView tvCityCountry;
    private TextView tvUpdatedTime;
    private TextView tvWeather;
    private TextView tvTemp;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvWind;
    private TextView tvPressure;
    private TextView tvHumidity;
    private TextView tvCloud;

    RequestQueue requestQueue;
    String latitude = "21.02";
    String longtitude = "105.84";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        tvCityCountry = findViewById(R.id.tvCityCountry);
        tvUpdatedTime = findViewById(R.id.tvUpdatedTime);
        tvWeather = findViewById(R.id.tvWeather);
        tvTemp = findViewById(R.id.tvTemp);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvWind = findViewById(R.id.tvWind);
        tvPressure = findViewById(R.id.tvPressure);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvCloud = findViewById(R.id.tvCloud);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = "" + location.getLatitude();
                            longtitude = "" + location.getLongitude();

                            callAPI();
                        }
                    }
                });
    }

    private void callAPI() {
        requestQueue = Volley.newRequestQueue(this);
        String url ="http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longtitude+"&units=metric&appid=7d5900f6608dfa535fc591a8354c8f0a";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        City city = new Gson().fromJson(response, City.class);
                        String textCityCountry = city.getName() + ", " + city.getSys().getCountry();
                        String textUpdatedTime = "Updated: " + new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(city.getDt()*1000L));
                        String textWeather = city.getWeather().get(0).getMain();
                        String textTemp = city.getMain().getTemp() + "°C";
                        String textSunrise = "Sunrise \n" + new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(city.getSys().getSunrise()*1000L));
                        String textSunset = "Sunset \n" + new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(city.getSys().getSunset()*1000L));
                        String textWind = "Wind \n" + city.getWind().getSpeed() + " m/s";
                        String textPressure = "Pressure \n" + city.getMain().getPressure() + " hPa";
                        String textHumidity = "Humidity \n" + city.getMain().getHumidity() + "%";
                        String textCloud = "Cloud \n" + city.getClouds().getAll() + "%";

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, " Location permission was denied", Toast.LENGTH_SHORT).show();
                    callAPI();
                }
                break;
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}