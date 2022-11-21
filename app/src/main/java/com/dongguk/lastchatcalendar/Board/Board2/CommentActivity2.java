package com.dongguk.lastchatcalendar.Board.Board2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dongguk.lastchatcalendar.Board.Board2.adapter.CommentAdapter2;
import com.dongguk.lastchatcalendar.Board.adapter.CommentAdapter;
import com.dongguk.lastchatcalendar.Board.object.Comments;
import com.dongguk.lastchatcalendar.Board.object.PostId;
import com.dongguk.lastchatcalendar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity2 extends AppCompatActivity {

    private RecyclerView rvComment;
    private ArrayList<Comments> listComment;
    private CommentAdapter2 commentAdapter2;
    private boolean isFirstPageLoad = true;
    private boolean isNull;
//    private String postId;

    private CircleImageView userImageAddComment;
    private ImageView btnSend;
    private EditText etComment;

    private FirebaseAuth firebaseAuth;
    private PostId postIdClass;
    private Context context;

    private String postId;

    private FirebaseFirestore firestore;
    private DocumentSnapshot lastComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment2);
        Bundle bundle = getIntent().getExtras();
        postId = bundle.getString("postId");

        userImageAddComment = findViewById(R.id.iv_addcomment_user);
        btnSend = findViewById(R.id.btn_comment_send);
        etComment = findViewById(R.id.et_addcomment);

        listComment = new ArrayList<>();
        context = getApplicationContext();

        rvComment = findViewById(R.id.rv_comment);
        commentAdapter2 = new CommentAdapter(listComment);

        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setAdapter(commentAdapter2);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("UsersInfo").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    String userImage = task.getResult().getString("image");
                    String name = task.getResult().getString("name");
                    etComment.setHint("Comment as " + name);
                    Glide.with(context)
                            .load(userImage)
                            .apply(new RequestOptions().centerCrop())
                            .into(userImageAddComment);
                }
            }
        });


        if (!isNull){
            Query queryFirstLoad = firestore.collection("Post2").document(postId).collection("Comments").orderBy("timestamp", Query.Direction.ASCENDING);
            queryFirstLoad.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String postId = doc.getDocument().getId();

                            Comments comments = doc.getDocument().toObject(Comments.class);

                            if (isFirstPageLoad) {
                                listComment.add(comments);
                            }
                            else {
                                listComment.add(0, comments);
                            }

                            commentAdapter2.notifyDataSetChanged();

                        }

                    }


                }
            });
        }



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String caption = etComment.getText().toString();
                final String currentUserId = firebaseAuth.getCurrentUser().getUid();
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("comment_text", caption);
                commentMap.put("user_id",currentUserId);
                commentMap.put("timestamp", FieldValue.serverTimestamp());

                firestore.collection("Post2/" + postId + "/Comments").add(commentMap);
                finish();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        firestore.collection("Post2/" + postId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null){
                    if (snapshots.isEmpty()){
                        isNull = true;
//                        Toast.makeText(CommentActivity.this, "True", Toast.LENGTH_LONG).show();

                    }else {
                        isNull = false;
//                        Toast.makeText(CommentActivity.this, "False", Toast.LENGTH_LONG).show();


                    }
                }

            }
        });
    }

}
