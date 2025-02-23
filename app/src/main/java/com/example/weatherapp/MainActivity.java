package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private Button buttonFetch;
    private TextView textViewTemp, textViewStatus, textViewHumidity, textViewDate;
    private ImageView imageViewWeather;
    private final String API_KEY = BuildConfig.API_KEY; // Replace with your actual API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        buttonFetch = findViewById(R.id.buttonFetch);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewHumidity = findViewById(R.id.textViewHumidity);
        textViewDate = findViewById(R.id.textViewDate);
        imageViewWeather = findViewById(R.id.imageViewWeather); // Add this line

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    fetchWeather(city);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchWeather(String city) {
        String url = "https://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=" + city + "&aqi=no";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject current = response.getJSONObject("current");
                            double tempC = current.getDouble("temp_c");
                            int humidity = current.getInt("humidity");
                            String conditionText = current.getJSONObject("condition").getString("text");

                            Pair<Integer, String> weatherStatusPair = mapWeatherStatus(conditionText);
                            String currentDate = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(new Date());

                            textViewTemp.setText(tempC + "°C");
                            textViewStatus.setText(weatherStatusPair.second);
                            textViewHumidity.setText(humidity + "%");
                            textViewDate.setText(currentDate);

                            imageViewWeather.setImageResource(weatherStatusPair.first); // Set the image resource

                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Failed to fetch data. Check city name or connection.", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    private Pair<Integer, String> mapWeatherStatus(String condition) {
        condition = condition.toLowerCase();
        if (condition.contains("sun")) return new Pair<>(R.drawable.weatherapp, "Sunny");
        if (condition.contains("rain")) return new Pair<>(R.drawable.rainyday, "Rainy");
        if (condition.contains("cloud")) return new Pair<>(R.drawable.clouds, "Cloudy");
        if (condition.contains("wind")) return new Pair<>(R.drawable.windy, "Windy");
        if (condition.contains("storm")) return new Pair<>(R.drawable.stormy, "Stormy");
        return new Pair<>(R.drawable.clouddefault, capitalizeFirstLetter(condition)); // Default
    }

    private String capitalizeFirstLetter(String condition) {
        return condition.substring(0, 1).toUpperCase() + condition.substring(1);
    }
}
