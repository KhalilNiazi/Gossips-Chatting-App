package com.niazi.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niazi.chatsapp.Adaptor.MessageAdaptor;
import com.niazi.chatsapp.Model_Classes.Message;
import com.niazi.chatsapp.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;

    MessageAdaptor adapter;
    ArrayList<Message> messages;
    String senderRoom,receiverRoom;

    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messages = new ArrayList<>();


        String name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);

        String receiverUid = getIntent().getStringExtra("uid");
        String senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        adapter = new MessageAdaptor(this, messages, senderRoom, receiverRoom);
        binding.chatsrecyclview
                .setLayoutManager(new LinearLayoutManager(this));
        binding.chatsrecyclview.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();


      database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);

                        }
                        binding.chatsrecyclview.scrollToPosition(messages.size() - 1);


                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.edmessageed.getText().toString();


               if (messageTxt.isEmpty()) {
                    binding.edmessageed.setClickable(false);
                } else {
                   binding.chatsrecyclview.scrollToPosition(messages.size() - 1);

                   Date date = new Date();
                    Message message = new Message(messageTxt, senderUid, date.getTime());
                    binding.edmessageed.setText("");

                    String randomKey = database.getReference().push().getKey();

                    HashMap<String,Object > lastmessageObj = new HashMap<>();
                    lastmessageObj.put("lastMsg",message.getMessage());
                    lastmessageObj.put("lastMsgTime",date.getTime());
                    database.getReference().child("chats").child(senderRoom).updateChildren(lastmessageObj);
                    database.getReference().child("chats").child(receiverRoom). updateChildren(lastmessageObj);


                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    database.getReference().child("chats")
                                            .child(receiverRoom)
                                            .child("messages")
                                            .child(randomKey)
                                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });

                                    HashMap<String,Object > lastmessageObj = new HashMap<>();
                                    lastmessageObj.put("lastMsg",message.getMessage());
                                    lastmessageObj.put("lastMsgTime",date.getTime());
                                    database.getReference().child("chats").child(senderRoom).updateChildren(lastmessageObj);
                                    database.getReference().child("chats").child(receiverRoom). updateChildren(lastmessageObj);






                                }
                            });

                }
            }
        });




    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}