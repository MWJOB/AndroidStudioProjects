//package com.dongguk.lastchatcalendar.Board;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.dongguk.lastchatcalendar.ChatActivity.Activity.BaseActivity;
//import com.example.myapplication.Activity.BasicActivity;
//import com.example.myapplication.R;
//import com.example.myapplication.Writeinfo;
//import com.example.myapplication.adapter.MainAdapter;
//import com.example.myapplication.listener.OnPostListener;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.storage.FirebaseStorage;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//public class NewActivity extends BaseActivity {
//    private static final String TAG = "NewActivity";
//    private FirebaseUser firebaseUser;
//    private FirebaseFirestore firebaseFirestore;
//    private MainAdapter mainAdapter;
//    private ArrayList<Writeinfo> postList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new);
//
//        setToolbarTitle("신입생 게시판");
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//
//        postList = new ArrayList<>();
//        mainAdapter = new MainAdapter(NewActivity.this, postList);
//        mainAdapter.setOnPostListener(onPostListener);
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(NewActivity.this));
//        recyclerView.setAdapter(mainAdapter);
//
//        FloatingActionButton btn_plus = findViewById(R.id.btn_plus);
//        btn_plus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(NewActivity.this, NewWriteActivity.class);
//                startActivity(intent);
//
//            }
//        });
//    }
//
//    OnPostListener onPostListener = new OnPostListener() {
//        @Override
//        public void onDelete() {
//            postUpdate();
//            Log.e("로그", "삭제 성공");
//        }
//
//        @Override
//        public void onModify() {
//            Log.e("로그", "수정 성공");
//        }
//    };
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        postUpdate();
//    }
//
//    private void postUpdate() {
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        firebaseFirestore = FirebaseFirestore.getInstance();
//
//        if (firebaseUser != null) {
//            CollectionReference collectionReference = firebaseFirestore.collection("posts");
//            collectionReference.orderBy("createAt", Query.Direction.DESCENDING).get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            postList.clear();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                postList.add(new Writeinfo(
//                                        document.getData().get("title").toString(),
//                                        (ArrayList<String>) document.getData().get("contents"),
//                                        (ArrayList<String>) document.getData().get("formats"),
//                                        document.getData().get("publisher").toString(),
//                                        new Date(document.getDate("createAt").getTime()),
//                                        document.getId()));
//                            }
//                            mainAdapter.notifyDataSetChanged();
//
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    });
//        }
//    }
//
//}