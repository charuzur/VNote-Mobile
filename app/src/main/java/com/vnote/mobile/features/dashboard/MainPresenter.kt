package com.vnote.mobile.features.dashboard

import com.vnote.mobile.core.network.NoteResponse

class MainPresenter(
    private val view: MainContract.View,
    private val model: MainModel
) : MainContract.Presenter {

    private var fullNotesList = mutableListOf<NoteResponse>()

    override fun loadNotes() {
        val token = view.getToken()
        if (token.isEmpty()) {
            view.navigateToLogin()
            return
        }

        model.fetchNotes(token) { isSuccess, notes, code ->
            if (isSuccess && notes != null) {
                fullNotesList.clear()
                fullNotesList.addAll(notes)

                view.showNotes(fullNotesList)
                view.updateNoteCount(fullNotesList.size)
            } else if (code == 401) {
                view.showSessionExpired()
                view.navigateToLogin()
            } else {
                view.showError("Failed to load notes: Network Error")
            }
        }
    }

    override fun onSearchQueryChanged(query: String) {
        if (query.isEmpty()) {
            view.showNotes(fullNotesList)
        } else {
            val filtered = fullNotesList.filter {
                it.title.lowercase().contains(query) || it.content.lowercase().contains(query)
            }
            view.showNotes(filtered)
        }
    }

    override fun onNoteClicked(note: NoteResponse) {
        view.navigateToEditNote(note)
    }

    override fun onCreateNoteClicked() {
        view.navigateToCreateNote()
    }

    override fun onProfileMenuClicked() {
        view.navigateToProfile()
    }

    override fun onNoteSwiped(note: NoteResponse, position: Int) {
        view.showDeleteConfirmation(note, position)
    }

    override fun confirmDeleteNote(note: NoteResponse, position: Int) {
        val token = view.getToken()
        model.deleteNote(token, note.noteId) { isSuccess ->
            if (isSuccess) {
                fullNotesList.remove(note)
                view.removeNoteFromList(position)
                view.updateNoteCount(fullNotesList.size)
                view.showError("Note Deleted") // Using showError just to trigger a Toast
            } else {
                view.resetSwipeState(position)
                view.showError("Failed to delete note")
            }
        }
    }

    override fun cancelDeleteNote(position: Int) {
        view.resetSwipeState(position)
    }
}