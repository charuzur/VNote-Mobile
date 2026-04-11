package com.vnote.mobile.features.register

class RegisterPresenter(
    private val view: RegisterContract.View,
    private val model: RegisterModel
) : RegisterContract.Presenter {

    override fun onRegisterClicked(name: String, username: String, password: String, confirmPass: String) {
        // 1. Validation Logic
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            view.showError("Please fill in all fields")
            return
        }

        if (password != confirmPass) {
            view.showError("Passwords do not match!")
            return
        }

        // 2. Trigger Loading State
        view.showLoading()

        // 3. Call the Model
        model.registerUser(name, username, password) { isSuccess, errorMsg ->
            view.hideLoading()

            if (isSuccess) {
                view.showSuccess()
                view.navigateToLogin()
            } else {
                view.showError(errorMsg ?: "Unknown error occurred")
            }
        }
    }

    override fun onLoginClicked() {
        view.navigateToLogin()
    }
}