package com.example.take3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Nutritional_App");
        }
    }

    public void register(View view) {
        Intent i = new Intent(MainActivity.this, registerActivity.class);
        startActivity(i);
    }

    public void Login(View view) {
        Intent i = new Intent(MainActivity.this, Login.class);
        startActivity(i);
    }
}
