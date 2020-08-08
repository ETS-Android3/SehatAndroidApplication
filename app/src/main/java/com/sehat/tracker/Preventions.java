package com.sehat.tracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Preventions extends AppCompatActivity {

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sh = getSharedPreferences("DarkMode", MODE_PRIVATE);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || sh.getBoolean("DarkMode",false)) {
            setTheme(R.style.Sehat_style_dark);
        }
        else {
            setTheme(R.style.Sehat_style_not);
        }
        setContentView(R.layout.preventions);
        back = findViewById(R.id.backBt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
