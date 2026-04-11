package com.vnote.mobile.features.profile.password

interface ChangePasswordContract {
    interface View {
        fun getToken(): String
        fun showMessage(message: String)
        fun showUpdateConfirmation(oldPass: String, newPass: String)
        fun closeScreen()
    }

    interface Presenter {
        fun onSaveClicked(oldPass: String, newPass: String, confirmPass: String)
        fun onUpdateConfirmed(oldPass: String, newPass: String)
        fun onBackClicked()
    }
}