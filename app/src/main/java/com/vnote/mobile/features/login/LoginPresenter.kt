package com.vnote.mobile.features.login

class LoginPresenter(
    private val view: LoginContract.View,
    private val model: LoginModel
) : LoginContract.Presenter {

    override fun onLoginClicked(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Please fill in all fields")
            return
        }

        view.showLoading()

        model.loginUser(username, password) { isSuccess, token, errorMsg ->
            view.hideLoading()

            if (isSuccess && token != null) {
                view.saveToken(token)
                view.showSuccess()
                view.navigateToDashboard()
            } else {
                view.showError(errorMsg ?: "Unknown error occurred")
            }
        }
    }

    override fun onRegisterClicked() {
        view.navigateToRegister()
    }
}