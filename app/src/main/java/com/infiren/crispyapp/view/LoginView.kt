package com.infiren.crispyapp.view

import android.content.Context

interface LoginView {
    fun onLoginSuccess(token: String)
    fun onLoginError(errorMessage: String)
    fun getContext(): Context
}