package com.techov8.medfer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("LoginData",
                0);
        String string = prefs.getString("data",
                "no");
        if(!string.equals("Yes")){
            Intent i=new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        }else{
                Intent ii=new Intent(SplashActivity.this,MainActivity.class);
                                startActivity(ii);
                                finish();

        }

    }
}