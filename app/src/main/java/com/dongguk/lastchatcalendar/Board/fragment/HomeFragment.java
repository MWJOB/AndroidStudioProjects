package com.dongguk.lastchatcalendar.Board.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dongguk.lastchatcalendar.Board.Board2.adapter.PostingAdapter2;
import com.dongguk.lastchatcalendar.Board.adapter.PostingAdapter;
import com.dongguk.lastchatcalendar.Board.object.Posting;

import com.dongguk.lastchatcalendar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView rvHomePost;
    private ArrayList<Posting> listPosting;
    private boolean isFirstPageLoad = true;
    private boolean isNull;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastPost;
    private Context context;


    private ImageView btnComment;

    private PostingAdapter2 postingAdapter2;

    public HomeFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listPosting = new ArrayList<>();

        rvHomePost = view.findViewById(R.id.rv_homepost);
        btnComment = view.findViewById(R.id.iv_item_comment);

        context = getContext();
        postingAdapter2 = new PostingAdapter2(listPosting);

        firebaseAuth = FirebaseAuth.getInstance();

        rvHomePost.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvHomePost.setAdapter(postingAdapter2);

        if (firebaseAuth.getCurrentUser() != null) {

            firestore = FirebaseFirestore.getInstance();

            rvHomePost.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom){
                        loadMorePost();
                    }
                    else {

                    }

                }
            });



                Query queryFirstLoad = firestore.collection("Post").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
            ListenerRegistration registration = queryFirstLoad.addSnapshotListener(new EventListener<QuerySnapshot>(){
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (isFirstPageLoad){
                        lastPost = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);
                    }
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String postId = doc.getDocument().getId();
                            Posting posting = doc.getDocument().toObject(Posting.class).withId(postId);
                            if (isFirstPageLoad) {
                                listPosting.add(posting);
                            }
                            else {
                                listPosting.add(0, posting);
                            }
                            postingAdapter2.notifyDataSetChanged();
                        }
                    }
                    isFirstPageLoad = false;
                }
            });







        }
        // Inflate the layout for this fragment
        return view;
    }

    private void loadMorePost(){

        Query queryMorePost = firestore.collection("Post")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastPost)
                .limit(5);


        queryMorePost.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    lastPost = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String postId = doc.getDocument().getId();

                            Posting posting = doc.getDocument().toObject(Posting.class).withId(postId);
                            listPosting.add(posting);
                            postingAdapter2.notifyDataSetChanged();

                        }

                    }

                }
                else {

                    Toast.makeText(getContext(), "No more post", Toast.LENGTH_LONG).show();

                }


            }
        });
    }
}
