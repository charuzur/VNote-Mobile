package com.vnote.mobile.features.note_edit

interface EditNoteContract {
    interface View {
        fun getToken(): String
        fun getUserId(): Long?
        fun showMessage(message: String)
        fun closeScreen()
        fun setUIForMode(mode: String)
        fun showSaveConfirmation()
        fun showDiscardConfirmation()
    }

    interface Presenter {
        fun initialize(mode: String, noteId: Long)
        fun onActionButtonClicked(title: String, content: String)
        fun onSaveConfirmed(title: String, content: String)
        fun onBackPressed()
    }
}