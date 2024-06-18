package com.infiren.crispyapp.presenter


import com.infiren.crispyapp.model.UserModel
import com.infiren.crispyapp.service.RetrofitClient
import com.infiren.crispyapp.view.RegistrationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationPresenter(private val view: RegistrationView) {

    fun registerUser(user: UserModel) {
        RetrofitClient.getClient(view.getContext()).signup(user).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    view.onRegistrationSuccess()
                } else {
                    view.onRegistrationError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                view.onRegistrationError("Failure: ${t.message}")
            }
        })
    }
}