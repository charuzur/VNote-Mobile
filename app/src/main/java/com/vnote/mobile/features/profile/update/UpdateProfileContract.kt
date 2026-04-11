package com.vnote.mobile.features.profile.update

interface UpdateProfileContract {
    interface View {
        fun getToken(): String
        fun prefillData(name: String, username: String)
        fun showSaveConfirmation(newName: String, newUsername: String)
        fun showMessage(message: String)
        fun closeScreen()
    }

    interface Presenter {
        fun initialize(currentName: String, currentUsername: String)
        fun onSaveClicked(newName: String, newUsername: String)
        fun onSaveConfirmed(newName: String, newUsername: String)
        fun onBackClicked()
    }
}