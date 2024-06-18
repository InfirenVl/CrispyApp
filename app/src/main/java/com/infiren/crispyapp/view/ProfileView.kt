package com.infiren.crispyapp.view

import android.content.Context
import com.infiren.crispyapp.model.UserInfo

interface ProfileView {
    fun onProfileLoaded(user: UserInfo)
    fun onProfileLoadError(errorMessage: String)
    fun onAccountDeleted()
    fun onNameChanged(newName: String)
    fun getContext(): Context
}