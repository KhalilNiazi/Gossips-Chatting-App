package com.niazi.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.niazi.chatsapp.Adaptor.Top_Status_Adoptor;
import com.niazi.chatsapp.Adaptor.UserAdoptor;
import com.niazi.chatsapp.Model_Classes.Status;
import com.niazi.chatsapp.Model_Classes.User;
import com.niazi.chatsapp.Model_Classes.UserStatus;
import com.niazi.chatsapp.R;
import com.niazi.chatsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** @noinspection ALL*/
public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UserAdoptor userAdoptor;
    Top_Status_Adoptor statusAdoptor;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialog;
    User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Your Story");
        dialog.setCancelable(false);
        database= FirebaseDatabase.getInstance();

        users=new ArrayList<>();
        userStatuses= new ArrayList<>();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        userAdoptor= new UserAdoptor(this,users);
        statusAdoptor= new Top_Status_Adoptor(this,userStatuses);



        binding.chatsrecycleView.setAdapter(userAdoptor);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.statusrecyclerView.setLayoutManager(linearLayoutManager);

        binding.statusrecyclerView.setAdapter(statusAdoptor);



        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Uploadstatus) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 75);
                }
                return false;
            }
        });




        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
/*
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        users.add(user);
                    }
                }*/
                }
                userAdoptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userStatuses.clear();
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                 statusAdoptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setSupportActionBar(binding.appBarMain.toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.app_bar_search) {
            Toast.makeText(this, "Search Click", Toast.LENGTH_SHORT).show();


              } else if (itemId == R.id.groups) {
            Toast.makeText(this, "Group Click", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.invites) {
            Toast.makeText(this, "Invites Click", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {

            if(data.getData() != null) {

                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getName());
                                    obj.put("profileImage", userStatus.getProfileImage());
                                    obj.put("lastUpdated", userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl, userStatus.getLastUpdated());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);

    }
}