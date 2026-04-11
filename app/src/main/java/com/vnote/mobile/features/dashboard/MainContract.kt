package com.vnote.mobile.features.dashboard

import com.vnote.mobile.core.network.NoteResponse

interface MainContract {
    interface View {
        fun getToken(): String
        fun showNotes(notes: List<NoteResponse>)
        fun updateNoteCount(count: Int)
        fun showError(message: String)
        fun showSessionExpired()
        fun navigateToLogin()
        fun navigateToCreateNote()
        fun navigateToEditNote(note: NoteResponse)
        fun navigateToProfile()
        fun showDeleteConfirmation(note: NoteResponse, position: Int)
        fun removeNoteFromList(position: Int)
        fun resetSwipeState(position: Int)
    }

    interface Presenter {
        fun loadNotes()
        fun onSearchQueryChanged(query: String)
        fun onNoteClicked(note: NoteResponse)
        fun onCreateNoteClicked()
        fun onProfileMenuClicked()
        fun onNoteSwiped(note: NoteResponse, position: Int)
        fun confirmDeleteNote(note: NoteResponse, position: Int)
        fun cancelDeleteNote(position: Int)
    }
}