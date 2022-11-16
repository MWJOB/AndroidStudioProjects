package com.dongguk.lastchatcalendar.ChatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.dongguk.lastchatcalendar.NoteActivity.Activity.CreateNoteActivity;
import com.dongguk.lastchatcalendar.R;
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

public class FindUsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManger preferenceManger;
    //AlertDialog 사용 url 삽입을 위해
    private AlertDialog dialogSearchEmail;
    private String SearchResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManger = new PreferenceManger(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.imageSearch.setOnClickListener(v-> showSearchDialog());
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
                                showMessage();
                                continue;
                            }
                            if(SearchResult.equals(queryDocumentSnapshot.getString(Constants.KEY_EMAIL))) {
                                User user = new User();
                                user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                user.university = queryDocumentSnapshot.getString(Constants.KEY_UNIVERSITY);
                                user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                user.id = queryDocumentSnapshot.getId();
                                users.add(user);
                                break;
                            }
                            else{
                                showFindMessage();
                            }
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

    private void showSearchDialog(){
        if(dialogSearchEmail== null){
            AlertDialog.Builder builder = new AlertDialog.Builder(FindUsersActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_serach_email,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogSearchEmail = builder.create();
            dialogSearchEmail.show();
            if(dialogSearchEmail.getWindow() != null){
                dialogSearchEmail.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputEmail = view.findViewById(R.id.InputEmail);
            inputEmail.requestFocus();

            view.findViewById(R.id.textSearch).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(inputEmail.getText().toString().trim().isEmpty()){
                        Toast.makeText(FindUsersActivity.this, "이메일을 입력하십시오.", Toast.LENGTH_SHORT).show();
                    }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
                        Toast.makeText(FindUsersActivity.this, "잘못된 이메일을 입력했습니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        SearchResult = inputEmail.getText().toString();
                        dialogSearchEmail.dismiss();
                        getUsers();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dialogSearchEmail.dismiss();
                }
            });
        }
    }


    private void showMessage(){
        binding.textErrorMessage.setText(String.format("%s", "자기 자신은 검색할 수 없습니다."));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showFindMessage(){
        binding.textErrorMessage.setText(String.format("%s", "검색 완료"));
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