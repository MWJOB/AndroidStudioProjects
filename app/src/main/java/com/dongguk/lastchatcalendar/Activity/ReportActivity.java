package com.dongguk.lastchatcalendar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.dongguk.lastchatcalendar.ChatActivity.Activity.BaseActivity;
import com.dongguk.lastchatcalendar.MainActivity;
import com.dongguk.lastchatcalendar.R;
import com.dongguk.lastchatcalendar.SetupActivity;
import com.dongguk.lastchatcalendar.databinding.ActivityReportBinding;
import com.dongguk.lastchatcalendar.models.ReportUserinfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportActivity extends BaseActivity {
    private static final String TAG = "ReportActivity";

    private Toolbar mainToolbar;
    EditText title, contents;
    Button ReportUploadButton;
    TextView forbid_behaviour;
    private FirebaseUser user;
    private ActivityReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(R.layout.activity_report);

        mainToolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(mainToolbar);
        setToolbarTitle("신고 하기");


        title = findViewById(R.id.reportEditText);
        contents = findViewById(R.id.reportEditText2);
        ReportUploadButton = findViewById(R.id.btn_reportupload);
        forbid_behaviour = findViewById(R.id.forbid_behaviour);

        forbid_behaviour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this, ForbidActActivity.class));
            }
        });

        ReportUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_title = title.getText().toString();
                String txt_contents = contents.getText().toString();
                if (TextUtils.isEmpty(txt_title) || TextUtils.isEmpty(txt_contents)){
                    Toast.makeText(ReportActivity.this,"내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    ReportSend(txt_title, txt_contents);
                }
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

    private void sendToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void sendToSetup() {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }



    private void uploader(ReportUserinfo reportUserinfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("report").add(reportUserinfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Error",e);
                    }
                });
    }

    public void ReportSend(String txtTitle, String txt_contents) {
        final String title = ((EditText) findViewById(R.id.reportEditText)).getText().toString();
        final String write = ((EditText) findViewById(R.id.reportEditText2)).getText().toString();

        if (title.length() > 0 && write.length() > 0) {
            Toast.makeText(ReportActivity.this, "접수 완료", Toast.LENGTH_SHORT).show();
            user = FirebaseAuth.getInstance().getCurrentUser();
            ReportUserinfo reportUserinfo = new ReportUserinfo(user.getUid(), title, write);
            uploader(reportUserinfo);
            finish();

        } else {
            startToast("접수 실패");
            return;
        }
    }



    private void startToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

}