package com.infiren.crispyapp.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.infiren.crispyapp.model.WorkspaceInfo

class SharedPrefsHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.apply()
    }

    fun setToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getUserId(): Int? {
        return sharedPreferences.getInt("user_id", -1).takeIf { it != -1 }
    }

    fun setUserId(userId: Int) {
        sharedPreferences.edit().putInt("user_id", userId).apply()
    }

    fun saveWorkspaces(workspaces: List<WorkspaceInfo>) {
        val workspacesJson = Gson().toJson(workspaces)
        sharedPreferences.edit().putString("workspaces", workspacesJson).apply()
    }

    fun getWorkspaces(): List<WorkspaceInfo> {
        val workspacesJson = sharedPreferences.getString("workspaces", null)
        return if (workspacesJson != null) {
            val type = object : TypeToken<List<WorkspaceInfo>>() {}.type
            Gson().fromJson(workspacesJson, type)
        } else {
            emptyList()
        }
    }
}