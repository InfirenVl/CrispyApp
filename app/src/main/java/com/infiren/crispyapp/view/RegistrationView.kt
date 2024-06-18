package com.infiren.crispyapp.view

import android.content.Context

interface RegistrationView {
    fun onRegistrationSuccess()
    fun onRegistrationError(errorMessage: String)
    fun getContext(): Context
}