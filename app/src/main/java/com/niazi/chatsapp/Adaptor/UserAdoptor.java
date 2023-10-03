package com.niazi.chatsapp.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niazi.chatsapp.Activities.ChatActivity;
import com.niazi.chatsapp.Model_Classes.User;
import com.niazi.chatsapp.R;
import com.niazi.chatsapp.databinding.RowConversatinBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserAdoptor extends RecyclerView.Adapter<UserAdoptor.UserViewHolder>{


    Context context;
    ArrayList<User> users;

    public UserAdoptor(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_conversatin,parent,false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = users.get(position);
        holder.binding.personname.setText(user.getName());

        String senderId = FirebaseAuth.getInstance().getUid();


        String senderRoom = senderId+user.getUid();

        FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(senderRoom).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                     if (snapshot.exists()){

                        String lastmsg = snapshot.child("lastMsg").getValue(String.class);
                        long time = snapshot.child("lastMsgTime").getValue(Long.class);
                         SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                        holder.binding.lastmsg.setText(lastmsg);
                        holder.binding.timess.setText(dateFormat.format(new Date(time)));



                     }else
                     {
                         holder.binding.lastmsg.setText("Tap to chat");
                         holder.binding.timess.setVisibility(View.INVISIBLE);
                     }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.user)
                .into(holder.binding.userpic);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);

                intent.putExtra("name",user.getName());
                intent.putExtra("uid",user.getUid());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        RowConversatinBinding binding;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=RowConversatinBinding.bind(itemView);
        }
    }
}
