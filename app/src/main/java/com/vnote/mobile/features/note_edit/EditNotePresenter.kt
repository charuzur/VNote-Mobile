package com.vnote.mobile.features.note_edit

import com.vnote.mobile.core.network.NoteRequest

class EditNotePresenter(
    private val view: EditNoteContract.View,
    private val model: EditNoteModel
) : EditNoteContract.Presenter {

    private var currentMode = "CREATE"
    private var noteId: Long = -1L

    override fun initialize(mode: String, noteId: Long) {
        this.currentMode = mode
        this.noteId = noteId
        view.setUIForMode(currentMode)
    }

    override fun onActionButtonClicked(title: String, content: String) {
        when (currentMode) {
            "VIEW" -> {
                currentMode = "EDIT"
                view.setUIForMode(currentMode)
            }
            "EDIT" -> {
                view.showSaveConfirmation()
            }
            "CREATE" -> {
                onSaveConfirmed(title, content)
            }
        }
    }

    override fun onSaveConfirmed(title: String, content: String) {
        if (title.isEmpty() || content.isEmpty()) {
            view.showMessage("Title and content cannot be empty")
            return
        }

        val token = view.getToken()
        val userId = view.getUserId()

        if (userId == null) {
            view.showMessage("Session error")
            return
        }

        val request = NoteRequest(title, content, userId)

        if (currentMode == "CREATE") {
            model.createNote(token, request) { isSuccess, msg ->
                if (msg != null) view.showMessage(msg)
                if (isSuccess) view.closeScreen()
            }
        } else if (currentMode == "EDIT") {
            model.updateNote(token, noteId, request) { isSuccess, msg ->
                if (msg != null) view.showMessage(msg)
                if (isSuccess) view.closeScreen()
            }
        }
    }

    override fun onBackPressed() {
        if (currentMode == "CREATE" || currentMode == "EDIT") {
            view.showDiscardConfirmation()
        } else {
            view.closeScreen()
        }
    }
}