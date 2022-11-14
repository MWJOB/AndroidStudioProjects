package com.dongguk.lastchatcalendar.Note.Activity;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.dongguk.lastchatcalendar.Note.adapters.NotesAdapter;
import com.dongguk.lastchatcalendar.Note.database.NotesDatabase;
import com.dongguk.lastchatcalendar.Note.entities.Note;
import com.dongguk.lastchatcalendar.Note.listeners.NotesListener;
import com.dongguk.lastchatcalendar.R;
import com.dongguk.lastchatcalendar.databinding.ActivityNoteBinding;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements NotesListener {

    private ActivityNoteBinding binding;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTES = 3;

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
//        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
//        imageAddNoteMain.setOnClickListener((v)-> {
//            startActivityForResult(
//                    new Intent(getApplicationContext(), CreateNoteActivity.class),
//                    REQUEST_CODE_ADD_NOTE
//            );
//        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTES);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpDATE", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);

    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageAddNoteMain.setOnClickListener(v-> startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE));
    }

    //requestCode를 요청함으로로
    private void getNotes(final int requestCode) {

        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase
                        .getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                //requsetcode를 받는 장소
                if(requestCode== REQUEST_CODE_SHOW_NOTES){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                }else if(requestCode == REQUEST_CODE_UPDATE_NOTE){
                    noteList.remove(noteClickedPosition);
                    noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                    notesAdapter.notifyItemChanged(noteClickedPosition);
                }

//                if(noteList.size() == 0){
//                    noteList.addAll(notes);
//                    notesAdapter.notifyDataSetChanged();
//                }else{
//                    noteList.add(0, notes.get(0));
//                    notesAdapter.notifyItemInserted(0);
//                }
//                notesRecyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNotesTask().execute();
    }

    //새로생성하면 바로 화면에 생성되게 작동
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE);
        }else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if(data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE);
            }
        }
    }
}