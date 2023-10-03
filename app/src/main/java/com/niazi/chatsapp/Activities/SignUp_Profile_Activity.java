package com.niazi.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.niazi.chatsapp.Model_Classes.User;
import com.niazi.chatsapp.databinding.ActivitySignUpProfileBinding;

/** @noinspection ALL*/
public class SignUp_Profile_Activity extends AppCompatActivity {



    ProgressDialog progressDialog;
    Uri selectedImage;
    FirebaseAuth auth;


    FirebaseDatabase database;
    FirebaseStorage storage;
    ActivitySignUpProfileBinding binding;
    private int Gallery_Code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editTextname.requestFocus();


        progressDialog = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.imageView.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, Gallery_Code);
        });


        binding.buttonContinue.setOnClickListener(view -> {
            String name = binding.editTextname.getText().toString();

            if (name.isEmpty()) {
                binding.editTextname.setError("Please type a Name");
                return;
            } else if (selectedImage != null) {

                AlertDialog.Builder alert = new AlertDialog.Builder(SignUp_Profile_Activity.this);

                alert.setMessage("Are You Sure To Create Your Profile ");

                alert.setCancelable(false);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        progressDialog.setMessage("Create Your Profile...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();


                        StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                        reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();

                                            String uid = auth.getUid();
                                            String phone = auth.getCurrentUser().getPhoneNumber();
                                            String name = binding.editTextname.getText().toString();

                                            User user = new User(uid, name, phone, imageUrl);

                                            database.getReference()
                                                    .child("users")
                                                    .child(uid)
                                                    .setValue(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressDialog.dismiss();
                                                            Intent intent = new Intent(SignUp_Profile_Activity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        });
                    }


                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(SignUp_Profile_Activity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            } else {

                AlertDialog.Builder alert = new AlertDialog.Builder(SignUp_Profile_Activity.this);

                alert.setMessage("Are You Sure To Create Your Profile ");

                alert.setCancelable(false);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        progressDialog.setMessage("Create Your Profile...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        String uid = auth.getUid();
                        String phone = auth.getCurrentUser().getPhoneNumber();

                        User user = new User(uid, name, phone, "No Image");

                        database.getReference().child("users").child(uid).setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(SignUp_Profile_Activity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(SignUp_Profile_Activity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
        });


            }
    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Code && resultCode == RESULT_OK) {

            selectedImage = data.getData();
            binding.imageView.setImageURI(selectedImage);
        }

    }
}