package com.niazi.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.material.ProgressIndicatorDefaults;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukeshsolanki.OnOtpCompletionListener;
import com.niazi.chatsapp.R;
import com.niazi.chatsapp.databinding.ActivityOTPBinding;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/** @noinspection ALL*/
public class oTPActivity extends AppCompatActivity {

    ActivityOTPBinding binding;

    String verificationId;

    FirebaseAuth auth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Sending OTP...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String phoneNumber= getIntent().getStringExtra("phoneNumber");
        binding.textViewlable.setText("Verify " +phoneNumber);

        PhoneAuthOptions Options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber).setTimeout(60L, TimeUnit.SECONDS).setActivity(oTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String VerifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(VerifyID, forceResendingToken);

                         verificationId = VerifyID;

                        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        binding.otpView.requestFocus();
                        progressDialog.dismiss();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(Options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otp);
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(oTPActivity.this,SignUp_Profile_Activity.class);
                            startActivity(intent);
                            finishAffinity();
                            Toast.makeText(oTPActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(oTPActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}