package com.dongguk.lastchatcalendar.ChatActivity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AlertDialog;

import com.dongguk.lastchatcalendar.ChatActivity.adapters.UsersAdapter;
import com.dongguk.lastchatcalendar.databinding.ActivityUsersBinding;
import com.dongguk.lastchatcalendar.ChatActivity.listeners.UserListener;
import com.dongguk.lastchatcalendar.ChatActivity.models.User;
import com.dongguk.lastchatcalendar.ChatActivity.utilities.Constants;
import com.dongguk.lastchatcalendar.ChatActivity.utilities.PreferenceManger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManger preferenceManger;
    //AlertDialog 사용 url 삽입을 위해
    private AlertDialog dialogSearchEmail;


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
        binding.imageSearch.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), FindUsersActivity.class)));
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
                           user.university = queryDocumentSnapshot.getString(Constants.KEY_UNIVERSITY);
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

//    private void showSearchDialog(){
//        if(dialogSearchEmail== null){
//            AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
//            View view = LayoutInflater.from(this).inflate(
//                    R.layout.layout_serach_email,
//                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
//            );
//            builder.setView(view);
//
//            dialogSearchEmail = builder.create();
//            dialogSearchEmail.show();
//            if(dialogSearchEmail.getWindow() != null){
//                dialogSearchEmail.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//            }
//            final EditText inputEmail = view.findViewById(R.id.InputEmail);
//            inputEmail.requestFocus();
//
//            view.findViewById(R.id.textSearch).setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    if(inputEmail.getText().toString().trim().isEmpty()){
//                        Toast.makeText(UsersActivity.this, "이메일을 입력하십시오.", Toast.LENGTH_SHORT).show();
//                    }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
//                        Toast.makeText(UsersActivity.this, "잘못된 이메일을 입력했습니다.", Toast.LENGTH_SHORT).show();
//                    }else {
//                        dialogSearchEmail.dismiss();
//                    }
//                }
//            });
//
//            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    dialogSearchEmail.dismiss();
//                }
//            });
//        }
//    }



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