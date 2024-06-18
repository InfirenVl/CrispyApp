package com.infiren.crispyapp.presenter


import android.widget.Toast
import com.infiren.crispyapp.model.UserInfo
import com.infiren.crispyapp.service.RetrofitClient
import com.infiren.crispyapp.view.ProfileView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePresenter(private val view: ProfileView) {

    fun loadUserProfile(token: String) {
        RetrofitClient.getClient(view.getContext()).getUserProfile("Bearer $token").enqueue(object : Callback<UserInfo> {
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        view.onProfileLoaded(user)
                    }
                } else {
                    view.onProfileLoadError("Ошибка загрузки данных: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                view.onProfileLoadError("Ошибка: ${t.message}")
            }
        })
    }

    fun changeUserPassword(token: String, newPassword: String, newPasswordConfirm: String) {
        val passwordChangeRequest = mapOf("newPassword" to newPassword, "newPasswordConfirm" to newPasswordConfirm)
        RetrofitClient.getClient(view.getContext()).changeUserPassword(token, passwordChangeRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(view.getContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
                } else {
                    view.onProfileLoadError("Ошибка смены пароля: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                view.onProfileLoadError("Ошибка: ${t.message}")
            }
        })
    }

    fun changeUserName(token: String, newName: String) {
        if (newName.length < 4) {
            view.onProfileLoadError("Имя должно содержать минимум 4 символа")
            return
        }

        val nameChangeRequest = mapOf("name" to newName)
        RetrofitClient.getClient(view.getContext()).changeUserName(token, nameChangeRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(view.getContext(), "Имя успешно изменено", Toast.LENGTH_SHORT).show()
                    view.onNameChanged(newName)
                } else {
                    view.onProfileLoadError("Ошибка смены имени: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                view.onProfileLoadError("Ошибка: ${t.message}")
            }
        })
    }

    fun deleteUserAccount(token: String) {
        RetrofitClient.getClient(view.getContext()).deleteUserAccount("Bearer $token").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    view.onAccountDeleted()
                } else {
                    view.onProfileLoadError("Ошибка удаления аккаунта: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                view.onProfileLoadError("Ошибка: ${t.message}")
            }
        })
    }
}