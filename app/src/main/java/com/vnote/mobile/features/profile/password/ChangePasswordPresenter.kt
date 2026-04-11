package com.vnote.mobile.features.profile.password

class ChangePasswordPresenter(
    private val view: ChangePasswordContract.View,
    private val model: ChangePasswordModel
) : ChangePasswordContract.Presenter {

    override fun onSaveClicked(oldPass: String, newPass: String, confirmPass: String) {
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            view.showMessage("Please fill in all fields")
            return
        }

        if (newPass != confirmPass) {
            view.showMessage("New passwords do not match!")
            return
        }

        view.showUpdateConfirmation(oldPass, newPass)
    }

    override fun onUpdateConfirmed(oldPass: String, newPass: String) {
        val userId = view.getToken()
        if (userId.isEmpty()) {
            view.showMessage("Session error. Please log in again.")
            return
        }

        val passwords = mapOf(
            "oldPassword" to oldPass,
            "newPassword" to newPass
        )

        model.changePassword(userId, passwords) { isSuccess, message ->
            if (message != null) view.showMessage(message)
            if (isSuccess) view.closeScreen()
        }
    }

    override fun onBackClicked() {
        view.closeScreen()
    }
}