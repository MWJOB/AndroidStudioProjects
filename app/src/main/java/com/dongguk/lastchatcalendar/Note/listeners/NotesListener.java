package com.dongguk.lastchatcalendar.Note.listeners;

import com.dongguk.lastchatcalendar.Note.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
