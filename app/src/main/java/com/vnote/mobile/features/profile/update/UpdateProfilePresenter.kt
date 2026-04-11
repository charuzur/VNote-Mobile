package com.vnote.mobile.features.profile.update

import com.vnote.mobile.core.network.UpdateProfileRequest

class UpdateProfilePresenter(
    private val view: UpdateProfileContract.View,
    private val model: UpdateProfileModel
) : UpdateProfileContract.Presenter {

    private var originalName = ""
    private var originalUsername = ""

    override fun initialize(currentName: String, currentUsername: String) {
        this.originalName = currentName
        this.originalUsername = currentUsername
        view.prefillData(currentName, currentUsername)
    }

    override fun onSaveClicked(newName: String, newUsername: String) {
        // If they leave it blank, fall back to the old value so we don't break the DB
        val finalName = newName.ifEmpty { originalName }
        val finalUsername = newUsername.ifEmpty { originalUsername }

        view.showSaveConfirmation(finalName, finalUsername)
    }

    override fun onSaveConfirmed(newName: String, newUsername: String) {
        val userId = view.getToken()
        if (userId.isEmpty()) {
            view.showMessage("Session error. Please log in again.")
            return
        }

        val request = UpdateProfileRequest(newName, newUsername)

        model.updateProfile(userId, request) { isSuccess, message ->
            if (message != null) view.showMessage(message)
            if (isSuccess) view.closeScreen()
        }
    }

    override fun onBackClicked() {
        view.closeScreen()
    }
}