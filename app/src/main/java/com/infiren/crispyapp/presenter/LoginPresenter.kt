package com.infiren.crispyapp.presenter

import com.infiren.crispyapp.model.UserSignIn
import com.infiren.crispyapp.service.RetrofitClient
import com.infiren.crispyapp.view.LoginView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter(private val view: LoginView) {

    fun loginUser(userSignIn: UserSignIn) {
        RetrofitClient.getClient(view.getContext()).signin(userSignIn)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val token = response.body()
                        if (token != null) {
                            view.onLoginSuccess(token)
                        } else {
                            view.onLoginError("Token is null")
                        }
                    } else {
                        view.onLoginError("Аккаунт не найден")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    view.onLoginError("Failure: ${t.message}")
                }
            })
    }
}