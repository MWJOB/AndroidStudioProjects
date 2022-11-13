package com.dongguk.lastchatcalendar.ChatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.dongguk.lastchatcalendar.adapters.UsersAdapter;
import com.dongguk.lastchatcalendar.databinding.ActivityUsersBinding;
import com.dongguk.lastchatcalendar.listeners.UserListener;
import com.dongguk.lastchatcalendar.models.User;
import com.dongguk.lastchatcalendar.utilities.Constants;
import com.dongguk.lastchatcalendar.utilities.PreferenceManger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManger preferenceManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManger = new PreferenceManger(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                   loading(false);
                   String currentUserId = preferenceManger.getString(Constants.KEY_USER_ID);
                   if(task.isSuccessful() && task.getResult() != null){
                       List<User> users = new ArrayList<>();
                       for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                           if(currentUserId.equals(queryDocumentSnapshot.getId())){
                               continue;
                           }
                           User user = new User();
                           user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                           user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                           user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                           user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                           user.id = queryDocumentSnapshot.getId();
                           users.add(user);
                       }
                       if(users.size() > 0){
                           UsersAdapter usersAdapter = new UsersAdapter(users, this);
                           binding.usersRecyclerView.setAdapter(usersAdapter);
                           binding.usersRecyclerView.setVisibility(View.VISIBLE);
                       }else{
                           showErrorMessage();
                       }
                   }else{
                       showErrorMessage();
                   }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}