package com.iox_prime.neomemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViewById(R.id.view).postDelayed(new Runnable() {
            @Override
            public void run() {
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
            }
        },1000);
    }
}
