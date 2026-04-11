package com.vnote.mobile.features.login

interface LoginContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSuccess()
        fun showError(message: String)
        fun saveToken(token: String)
        fun navigateToDashboard()
        fun navigateToRegister()
    }

    interface Presenter {
        fun onLoginClicked(username: String, password: String)
        fun onRegisterClicked()
    }
}