package com.vnote.mobile.features.profile.main

import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ProfilePresenter(
    private val view: ProfileContract.View,
    private val model: ProfileModel
) : ProfileContract.Presenter {

    private var currentName = ""
    private var currentUsername = ""

    override fun loadProfile() {
        val userId = view.getToken()
        if (userId.isEmpty()) return

        // 1. Fetch Notes Count
        model.fetchNotesCount(userId) { isSuccess, count ->
            if (isSuccess) view.showNotesCount(count)
            else view.showNotesCount(0)
        }

        // 2. Fetch User Profile
        model.fetchProfile(userId) { isSuccess, user ->
            if (isSuccess && user != null) {
                currentName = user.fullName ?: ""
                currentUsername = user.username ?: ""

                // Format the Date
                var memberSinceText = ""
                if (!user.createdAt.isNullOrEmpty()) {
                    try {
                        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                        val date = parser.parse(user.createdAt)
                        val formattedDate = date?.let { formatter.format(it) } ?: ""
                        memberSinceText = "Member since $formattedDate"
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                view.showProfileData(currentName, currentUsername, memberSinceText)
                view.showProfileImage(user.profileImage)
            } else {
                view.showMessage("Profile Sync Failed")
            }
        }
    }

    override fun onImagePicked(file: File?) {
        if (file == null) {
            view.showMessage("Error processing image")
            return
        }

        val userId = view.getToken()
        model.uploadPhoto(userId, file) { isSuccess, message ->
            if (message != null) view.showMessage(message)
            if (isSuccess) {
                loadProfile() // Refresh to show new photo!
            }
        }
    }

    override fun onEditProfileClicked() {
        view.navigateToEditProfile(currentName, currentUsername)
    }

    override fun onChangePasswordClicked() {
        view.navigateToChangePassword()
    }

    override fun onLogoutClicked() {
        view.showLogoutConfirmation()
    }

    override fun confirmLogout() {
        view.navigateToLogin()
    }

    override fun onHomeNavClicked() {
        view.navigateToDashboard()
    }
}