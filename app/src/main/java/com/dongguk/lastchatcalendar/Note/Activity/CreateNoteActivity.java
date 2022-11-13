package com.dongguk.lastchatcalendar.Note.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongguk.lastchatcalendar.Note.database.NotesDatabase;
import com.dongguk.lastchatcalendar.Note.entities.Note;
import com.dongguk.lastchatcalendar.R;
import com.dongguk.lastchatcalendar.databinding.ActivityCreateNoteBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private ActivityCreateNoteBinding binding;
    private View viewSubtitleIndicator;

    private String selectedNoteColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        binding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();




        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.KOREAN)
                        .format(new Date())
        );

        selectedNoteColor = "#333333"; // 기본 색 설정

        initSelectColors();
        setSubtitleIndicator();

    }

    private void setListeners() {
       binding.imageSave.setOnClickListener(v -> saveNote());
       binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
    private void saveNote(){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note Title 은 반드시 입력해야 합니다!", Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNoteSubtitle.getText().toString().trim().isEmpty()
            && inputNoteText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note 내용을 입력하세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectedNoteColor);

        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

    private void initSelectColors(){
        final LinearLayout layoutSelectColor = findViewById(R.id.layoutColors);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutSelectColor);
        layoutSelectColor.findViewById(R.id.textManyColors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutSelectColor.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutSelectColor.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutSelectColor.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutSelectColor.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutSelectColor.findViewById(R.id.imageColor5);

        layoutSelectColor.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicator();
            }
        });

        layoutSelectColor.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicator();
            }
        });

        layoutSelectColor.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicator();
            }
        });
        layoutSelectColor.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#3A52Fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicator();
            }
        });
        layoutSelectColor.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicator();
            }
        });
    }
    private void setSubtitleIndicator() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }
}