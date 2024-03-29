package com.dongguk.lastchatcalendar.Board.Board2.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dongguk.lastchatcalendar.Board.object.Comments;
import com.dongguk.lastchatcalendar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter2 extends RecyclerView.Adapter<CommentAdapter2.ViewHolder> {

    private ArrayList<Comments> listComment;
    private Context context;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    public CommentAdapter2(ArrayList<Comments> listComment) {
        this.listComment = listComment;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item2, parent, false);

        context = parent.getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final Comments comment = listComment.get(position);
        final String postId = listComment.get(position).PostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        long milisecond;

        holder.tvCaptions.setText(comment.getComment_text());
//        long milisecond = listComment.get(position).getTimestamp().getTime();
        if (listComment.get(position).getTimestamp() != null){
            milisecond = listComment.get(position).getTimestamp().getTime();
            holder.tvDate.setText(convertTime(milisecond));
        }


        String userId = comment.getUser_id();
        firestore.collection("UsersInfo").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    String name = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.tvUsername.setText(name);
                    Glide.with(context.getApplicationContext())
                            .load(userImage)
                            .apply(new RequestOptions().centerCrop())
                            .into(holder.userImage);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername, tvCaptions, tvDate;
        private CircleImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_item_comment_usermame);
            tvCaptions = itemView.findViewById(R.id.tv_item_comment_captions);
            tvDate = itemView.findViewById(R.id.tv_item_comment_date);
            userImage = itemView.findViewById(R.id.iv_item_comment_user);

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
