package com.niazi.chatsapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.niazi.chatsapp.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {


    ActivityPhoneNumberBinding binding;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.editTextphonenumber.requestFocus();

        auth =FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(PhoneNumberActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.buttonContinue.setOnClickListener(view -> {

            Intent intent =new Intent(PhoneNumberActivity.this, oTPActivity.class);
            intent.putExtra("phoneNumber",binding.editTextphonenumber.getText().toString());
            startActivity(intent);
        });
    }
}