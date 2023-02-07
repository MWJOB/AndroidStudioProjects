package com.dongguk.lastchatcalendar.Board.Board2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dongguk.lastchatcalendar.Board.Board2.fragment.HomeFragment2;
import com.dongguk.lastchatcalendar.ChatActivity.utilities.PreferenceManger;
import com.dongguk.lastchatcalendar.MainActivity;
import com.dongguk.lastchatcalendar.R;
import com.dongguk.lastchatcalendar.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewBoardMainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mainToolbar;
    private FloatingActionButton fbAddMain;
    private BottomNavigationView mainBotNav;
    private PreferenceManger preferenceManger;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String userId;

    private Fragment homeFragment2;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board2);

        fbAddMain = findViewById(R.id.fbAddMain);
        mainBotNav = findViewById(R.id.mainBotNav);


        //Fragment 실행
        homeFragment2 = new HomeFragment2();



        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mainToolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(mainToolbar);
        setToolbarTitle("정보 공유 게시판");


        fbAddMain.setOnClickListener(this);


        replaceFragment(homeFragment2);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            userId = mAuth.getCurrentUser().getUid();
            firestore.collection("UsersInfo")
                    .document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (documentSnapshot.exists()){

                                }
                                else {
                                    sendToSetup();
                                }

                            }
                        }
                    });
        }

        fbAddMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NewBoardMainActivity.this, NewPostActivity2.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_go_main:
                sendToMainActivity();
                return true;

            case R.id.action_account_setting_btn:
                sendToSetup();
                return true;

            default:
                return false;
        }

    }

//주의1

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//
//        switch (menuItem.getItemId()){
//
//            case R.id.botnav_action_home:
//                replaceFragment(homeFragment2);
//                return true;
//
//
//                default:
//                    return false;
//        }
//    }


    private void sendToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void sendToSetup() {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }

    private void sendToNewPost() {
        Intent intent = new Intent(this, NewPostActivity2.class);
        startActivity(intent);

    }
    public void setToolbarTitle(String title){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(title);
        }
    }

    //Move Fragment
    private void replaceFragment(Fragment fragment){


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fbAddMain:
                sendToNewPost();

                break;

        }

    }
}
