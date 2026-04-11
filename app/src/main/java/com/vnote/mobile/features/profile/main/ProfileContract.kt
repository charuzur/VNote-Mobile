package com.vnote.mobile.features.profile.main

import java.io.File

interface ProfileContract {
    interface View {
        fun getToken(): String
        fun showProfileData(name: String, username: String, memberSince: String)
        fun showProfileImage(base64String: String?)
        fun showNotesCount(count: Int)
        fun showMessage(message: String)
        fun showLogoutConfirmation()
        fun navigateToLogin()
        fun navigateToDashboard()
        fun navigateToEditProfile(currentName: String, currentUsername: String)
        fun navigateToChangePassword()
    }

    interface Presenter {
        fun loadProfile()
        fun onEditProfileClicked()
        fun onChangePasswordClicked()
        fun onLogoutClicked()
        fun confirmLogout()
        fun onHomeNavClicked()
        fun onImagePicked(file: File?)
    }
}