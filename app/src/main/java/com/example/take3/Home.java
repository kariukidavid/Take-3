package com.example.take3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class Home extends AppCompatActivity {


    private EditText etFeature1, etFeature2, etFeature3, etFeature4, etFeature5, etFeature6, etFeature7, etFeature8;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etFeature1 = findViewById(R.id.etFeature1);
        etFeature2 = findViewById(R.id.etFeature2);
        etFeature3 = findViewById(R.id.etFeature3);
        etFeature4 = findViewById(R.id.etFeature4);
        etFeature5 = findViewById(R.id.etFeature5);
        etFeature6 = findViewById(R.id.etFeature6);
        etFeature7 = findViewById(R.id.etFeature7);
        etFeature8 = findViewById(R.id.etFeature8);

        Button btnPredict = findViewById(R.id.btnPredict);
        tvResult = findViewById(R.id.tvResult);

        btnPredict.setOnClickListener(v -> new PredictTask().execute());
    }

    private class PredictTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.1.25:5000/predict");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Create JSON object with feature values
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("num_preg", Integer.parseInt(etFeature1.getText().toString()));
                jsonParam.put("glucose_conc", Double.parseDouble(etFeature2.getText().toString()));
                jsonParam.put("diastolic_bp", Double.parseDouble(etFeature3.getText().toString()));
                jsonParam.put("insulin", Double.parseDouble(etFeature4.getText().toString()));
                jsonParam.put("bmi", Double.parseDouble(etFeature5.getText().toString()));
                jsonParam.put("diab_pred", Double.parseDouble(etFeature6.getText().toString()));
                jsonParam.put("age", Integer.parseInt(etFeature7.getText().toString()));
                jsonParam.put("skin", Double.parseDouble(etFeature8.getText().toString()));

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                Scanner inStream = new Scanner(urlConnection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (inStream.hasNextLine()) {
                    response.append(inStream.nextLine());
                }

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    boolean hasDiabetes = jsonResponse.getInt("diabetes") == 1;

                    // Show toast message based on prediction result
                    String message = hasDiabetes ? "Positive for Diabetes" : "Negative for Diabetes";
                    Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();

                    // Clear the input fields
                    etFeature1.setText("");
                    etFeature2.setText("");
                    etFeature3.setText("");
                    etFeature4.setText("");
                    etFeature5.setText("");
                    etFeature6.setText("");
                    etFeature7.setText("");
                    etFeature8.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
