package com.vnote.mobile.features.register

interface RegisterContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSuccess()
        fun showError(message: String)
        fun navigateToLogin()
    }

    interface Presenter {
        fun onRegisterClicked(name: String, username: String, password: String, confirmPass: String)
        fun onLoginClicked()
    }
}