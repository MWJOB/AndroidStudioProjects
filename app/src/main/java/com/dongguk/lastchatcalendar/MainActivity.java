package com.dongguk.lastchatcalendar;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dongguk.lastchatcalendar.Activity.ApplyActivity;
import com.dongguk.lastchatcalendar.Activity.ReportActivity;
import com.dongguk.lastchatcalendar.Board.Board2.NewBoardMainActivity;
import com.dongguk.lastchatcalendar.Board.BoardMainActivity;
import com.dongguk.lastchatcalendar.ChatActivity.Activity.BaseActivity;
import com.dongguk.lastchatcalendar.ChatActivity.Activity.ChatMainActivity;
import com.dongguk.lastchatcalendar.ChatActivity.utilities.Constants;
import com.dongguk.lastchatcalendar.ChatActivity.utilities.PreferenceManger;
import com.dongguk.lastchatcalendar.NoteActivity.Activity.NoteActivity;
import com.dongguk.lastchatcalendar.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private PreferenceManger preferenceManger;

    String user_id = null;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManger =new PreferenceManger(getApplicationContext());
        loadUserDetails();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(SignInActivity.class);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });


            setListeners();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();

        ImageView btn_report = findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
    }
    private void loadUserDetails(){
        binding.textName.setText(preferenceManger.getString(Constants.KEY_NAME) + "님 어서오세요!");
        byte[] bytes = Base64.decode(preferenceManger.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }




    //로그아웃 로직
    private void signOut() {
        showToast("로그아웃 중...");
        mFirebaseAuth.signOut();
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

    //탈퇴 처리
    private void DeleteUser() {
        showToast("탈퇴처리 중...");
        mFirebaseAuth.getCurrentUser().delete();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference;
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManger.getString(Constants.KEY_USER_ID));
        documentReference.delete().addOnSuccessListener(unused -> {
                    preferenceManger.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("탈퇴 실패"));
    }

    //매인 화면 기능 읽어오기
    private void setListeners(){
        binding.btnNew.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), BoardMainActivity.class)));
        binding.btnNew2.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), NewBoardMainActivity.class)));
        binding.btnChat.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
        binding.btnLogout.setOnClickListener(v-> signOut());
        binding.btnNote.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), NoteActivity.class)));
        binding.btnReport.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), ReportActivity.class)));
        binding.btnApply.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), ApplyActivity.class)));
        binding.btnDelete.setOnClickListener(v->DeleteUser());
    }

    public void onHomePageClicked(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.google.com"));
        startActivity(intent);
    }

    private void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}