package com.niazi.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.niazi.chatsapp.R;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Splash_Screen.this, PhoneNumberActivity.class);
                startActivity(intent);
                finish();


            }
        },1000);
    }
}