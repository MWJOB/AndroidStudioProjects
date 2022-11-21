package com.dongguk.lastchatcalendar.Board.Board2.adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dongguk.lastchatcalendar.Board.Board2.CommentActivity2;
import com.dongguk.lastchatcalendar.Board.object.Posting;
import com.dongguk.lastchatcalendar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostingAdapter2 extends RecyclerView.Adapter<PostingAdapter2.ViewHolder> implements View.OnClickListener {

    private ArrayList<Posting> listPosting;
    private Context context;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    public PostingAdapter2(ArrayList<Posting> listPosting){
        this.listPosting = listPosting;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item2, parent, false);


        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        firestore = FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final Posting posting = listPosting.get(position);

        final String postId = listPosting.get(position).PostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        holder.tvCaption.setText(posting.getCaption());
        Glide.with(context)
                .load(posting.getImage_url())
                .apply(new RequestOptions().centerCrop())
                .into(holder.ivPostImg);



        String userId = posting.getUser_id();
        firestore.collection("UsersInfo").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    String name = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.tvUserid.setText(name);
                    Glide.with(context)
                            .load(userImage)
                            .apply(new RequestOptions().centerCrop())
                            .into(holder.userimage);

                }
                else {

                    String errorMsg = task.getException().getMessage();

                }

            }
        });

        long milisecond = listPosting.get(position).getTimestamp().getTime();
        holder.tvDate.setText(convertTime(milisecond));

        firestore.collection("Post2/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e == null){
                    if (!queryDocumentSnapshots.isEmpty()){

                        int count = queryDocumentSnapshots.size();
                        holder.tvLikeCount.setText(count + " 좋아요");

                    }else {

                        holder.tvLikeCount.setText("0 좋아요");

                    }
                }


            }
        });

        firestore.collection("Post2/" + postId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e == null){
                    if (!queryDocumentSnapshots.isEmpty()){

                        int count = queryDocumentSnapshots.size();
                        holder.tvCommentCount.setText(count + "개의 댓글이 있습니다!");

                    }else {

                        holder.tvCommentCount.setText("아직 댓글이 없습니다.");

                    }
                }


            }
        });


        firestore.collection("Post2/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null){
                    if (documentSnapshot.exists()){

                        holder.btnLike.setImageDrawable(context.getDrawable(R.mipmap.action_like_red));

                    }else {

                        holder.btnLike.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));

                    }
                }

            }
        });



        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firestore.collection("Post2/" + postId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firestore.collection("Post2/" + postId + "/Likes").document(currentUserId).set(likesMap);

                        } else {
                            firestore.collection("Post2/" + postId + "/Likes").document(currentUserId).delete();

                        }

                    }
                });

            }
        });

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity2.class);
                intent.putExtra("postId",postId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPosting.size();
    }

    @Override
    public void onClick(View v) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCaption, tvUserid, tvDate, tvLikeCount, tvCommentCount;
        private View mView;
        private ImageView ivPostImg, btnLike, btnComment;
        private CircleImageView userimage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCaption = itemView.findViewById(R.id.tv_item_caption);
            tvUserid = itemView.findViewById(R.id.tv_item_username);
            ivPostImg = itemView.findViewById(R.id.iv_item_post);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            userimage = itemView.findViewById(R.id.iv_addcomment_user);
            btnLike = itemView.findViewById(R.id.btn_item_like);
            tvLikeCount = itemView.findViewById(R.id.tv_item_likecount);
            btnComment = itemView.findViewById(R.id.iv_item_comment);
            tvCommentCount = itemView.findViewById(R.id.tv_item_komencount);


        }
    }

    public static String convertTime(long milisecond){


        SimpleDateFormat formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("dd/MM/yyyy");
        }
        Long now = System.currentTimeMillis();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String date = formatter.format(new Date(milisecond));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milisecond);
        String ago = DateUtils.getRelativeTimeSpanString(milisecond, now, DateUtils.MINUTE_IN_MILLIS).toString();
        return ago;
    }
}
