package com.jica.cleaningcuisine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Button btnAlarm, btnGuide, btnRst, btnInfo;
    private BottomNavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAlarm = findViewById(R.id.btnAlarm);

        btnAlarm.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ItemsAlarmActivity.class);
            startActivity(intent);
        });

        btnGuide = findViewById(R.id.btnGuide);

        btnGuide.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CleanGuideActivity.class);
            startActivity(intent);
        });


        btnRst = findViewById(R.id.btnRst);

        btnRst.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ItemManageActivity.class);
            startActivity(intent);
        });

        btnInfo = findViewById(R.id.btnInfo);

        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
            startActivity(intent);
        });

    }


}