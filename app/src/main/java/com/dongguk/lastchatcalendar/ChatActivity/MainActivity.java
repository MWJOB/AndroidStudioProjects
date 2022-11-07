package com.dongguk.lastchatcalendar.ChatActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.dongguk.lastchatcalendar.R;
import com.dongguk.lastchatcalendar.databinding.ActivityMainBinding;
import com.dongguk.lastchatcalendar.utilities.Constants;
import com.dongguk.lastchatcalendar.utilities.PreferenceManger;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManger preferenceManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManger =new PreferenceManger(getApplicationContext());
        loadUserDetails();
        getToken();
        setListeners();
    }

    //매인 화면 기능 읽어오기
    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v-> signOut());
        //새로운 유저 추가 작동
        binding.fabNewChat.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    private void loadUserDetails(){
        binding.textName.setText(preferenceManger.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManger.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManger.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));

    }
    //로그아웃 로직
    private void signOut() {
        showToast("로그아웃 중...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManger.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManger.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("로그아웃 실패"));
    }
}
