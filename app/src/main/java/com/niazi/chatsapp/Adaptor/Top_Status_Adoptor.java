package com.niazi.chatsapp.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.niazi.chatsapp.Activities.MainActivity;
import com.niazi.chatsapp.Model_Classes.Status;
import com.niazi.chatsapp.Model_Classes.UserStatus;
import com.niazi.chatsapp.R;
import com.niazi.chatsapp.databinding.ItemStatusBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class Top_Status_Adoptor extends RecyclerView.Adapter<Top_Status_Adoptor.Top_Status_ViewHolder>{

    Context context;
    ArrayList<UserStatus> userStatuses;

    public Top_Status_Adoptor(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }




    @NonNull
    @Override
    public Top_Status_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new Top_Status_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Top_Status_ViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);


        Status laststatus = userStatus.getStatuses().get(userStatus.getStatuses().size()-1);

        Glide.with(context).load(laststatus.getImageUrl()).into(holder.binding.image);

        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());


        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<MyStory> myStories = new ArrayList<>();

                for (Status status : userStatus.getStatuses()){
                    myStories.add(new MyStory(status.getImageUrl()));
                    new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                    .setStoriesList(myStories) // Required
                            .setStoryDuration(2000) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(userStatus.getName()) // Default is Hidden
                            .setSubtitleText("") // Default is Hidden
                            .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {
                                    //your action
                                }

                                @Override
                                public void onTitleIconClickListener(int position) {
                                    //your action
                                }
                            }) // Optional Listeners
                            .build() // Must be called before calling show method
                            .show();
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class Top_Status_ViewHolder extends RecyclerView.ViewHolder {

        ItemStatusBinding binding;
        public Top_Status_ViewHolder(@NonNull View itemView) {
            super(itemView);
        binding= ItemStatusBinding.bind(itemView);
        }

    }
}
