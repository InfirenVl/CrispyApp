package com.infiren.crispyapp.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.infiren.crispyapp.R
import com.infiren.crispyapp.model.UserInfo
import com.infiren.crispyapp.model.WorkspaceInfo
import com.infiren.crispyapp.service.RetrofitClient
import com.infiren.crispyapp.util.SharedPrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var buttonAddWorkspace: ImageButton
    private lateinit var workspaceContainer: LinearLayout
    private lateinit var textNoSpace: TextView
    private lateinit var bottomNavigationView: BottomNavigationView
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAddWorkspace = findViewById(R.id.button_add_workspace)
        workspaceContainer = findViewById(R.id.workspace_container)
        textNoSpace = findViewById(R.id.textNoSpace)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        buttonAddWorkspace.setOnClickListener {
            showCreateWorkspaceDialog()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    // Действие для "Settings"
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        setupBottomNavigationView(bottomNavigationView)
        loadUserData()
    }

    private fun loadUserData() {
        val prefsHelper = SharedPrefsHelper(this)
        token = prefsHelper.getToken()

        if (token != null) {
            RetrofitClient.getClient(this).getUserProfile("Bearer $token")
                .enqueue(object : Callback<UserInfo> {
                    override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                        if (response.isSuccessful) {
                            response.body()?.let { user ->
                                displayWorkspaces(user.workspaces)
                            }
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Ошибка загрузки данных: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                        Toast.makeText(
                            this@MainActivity,
                            "Ошибка: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            // Если нет токена, перенаправляем на экран входа
            navigateToLogin()
        }
    }

    private fun displayWorkspaces(workspaces: List<WorkspaceInfo>) {
        workspaceContainer.removeAllViews()
        if (workspaces.isNotEmpty()) {
            textNoSpace.visibility = View.GONE
            for (workspace in workspaces) {
                addWorkspaceCard(workspace)
            }
        } else {
            textNoSpace.visibility = View.VISIBLE
        }

    }

    private fun showCreateWorkspaceDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_workspace, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialogBuilder.show()

        val editTextWorkspaceName = dialogView.findViewById<EditText>(R.id.editTextWorkspaceName)
        val buttonCreateWorkspace = dialogView.findViewById<Button>(R.id.buttonCreateWorkspace)

        buttonCreateWorkspace.setOnClickListener {
            val workspaceName = editTextWorkspaceName.text.toString()
            if (workspaceName.isNotEmpty()) {
                createWorkspace(workspaceName)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createWorkspace(workspaceName: String) {
        token?.let {
            RetrofitClient.getClient(this).createWorkspace(workspaceName, "Bearer $it")
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            loadUserData() // Перезагружаем данные пользователя, чтобы отобразить новое рабочее пространство
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Ошибка создания рабочего пространства: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@MainActivity,
                            "Ошибка: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun addWorkspaceCard(workspace: WorkspaceInfo) {
        val cardView =
            LayoutInflater.from(this).inflate(R.layout.item_workspace, workspaceContainer, false)

        val textWorkspaceName = cardView.findViewById<TextView>(R.id.textWorkspaceName)
        val textWorkspaceUsers = cardView.findViewById<TextView>(R.id.textWorkspaceUsers)
        val buttonSettings = cardView.findViewById<ImageButton>(R.id.buttonSettings)
        val buttonDelete = cardView.findViewById<ImageButton>(R.id.buttonDelete)

        // Удаляем кавычки из имени рабочего пространства
        val cleanWorkspaceName = workspace.name.replace("\"", "")

        cardView.elevation = 0f
        textWorkspaceName.text = cleanWorkspaceName
        textWorkspaceUsers.text = workspace.users.toString()

        cardView.setOnClickListener {
            navigateToWorkspace(cleanWorkspaceName)
        }
        buttonSettings.setOnClickListener {
            showEditWorkspaceDialog(workspace)
        }
        buttonDelete.setOnClickListener {
            deleteWorkspace(workspace.id, cardView)
        }
        workspaceContainer.addView(cardView)
    }

    private fun showEditWorkspaceDialog(workspace: WorkspaceInfo) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_workspace, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialogBuilder.show()

        val editTextWorkspaceName = dialogView.findViewById<EditText>(R.id.editTextWorkspaceName)
        val buttonUpdateWorkspace = dialogView.findViewById<Button>(R.id.buttonUpdateWorkspace)
        val buttonDeleteWorkspace = dialogView.findViewById<Button>(R.id.buttonDeleteWorkspace)

        editTextWorkspaceName.setText(workspace.name)

        buttonUpdateWorkspace.setOnClickListener {
            val newWorkspaceName = editTextWorkspaceName.text.toString()
            if (newWorkspaceName.isNotEmpty()) {
                updateWorkspace(workspace.id, newWorkspaceName)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Введите новое название", Toast.LENGTH_SHORT).show()
            }
        }

        buttonDeleteWorkspace.setOnClickListener {
            deleteWorkspace(workspace.id, null)
            alertDialog.dismiss()
        }
    }

    private fun deleteWorkspace(workspaceId: Int, cardView: View?) {
        token?.let {
            RetrofitClient.getClient(this).deleteWorkspace(workspaceId, "Bearer $it")
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@MainActivity,
                                "Рабочее пространство удалено",
                                Toast.LENGTH_SHORT
                            ).show()
                            cardView?.let { workspaceContainer.removeView(it) }
                            loadUserData() // Перезагружаем данные пользователя, чтобы отобразить обновленный список рабочих пространств
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Ошибка удаления рабочего пространства: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@MainActivity,
                            "Ошибка: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun navigateToWorkspace(workspaceName: String) {
        val intent = Intent(this, WorkspaceActivity::class.java)
        intent.putExtra("WORKSPACE_NAME", workspaceName)
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateWorkspace(workspaceId: Int, newName: String) {
        val token = SharedPrefsHelper(this).getToken()
        if (token != null) {
            val workspaceUpdate = WorkspaceInfo(workspaceId, newName, "", 0)
            RetrofitClient.getClient(this)
                .updateWorkspace(workspaceId, workspaceUpdate, "Bearer $token")
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@MainActivity,
                                "Рабочее пространство обновлено",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadUserData() // Перезагружаем данные пользователя, чтобы отобразить обновленное рабочее пространство
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Ошибка обновления рабочего пространства: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@MainActivity,
                            "Ошибка: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun setupBottomNavigationView(bottomNavigationView: BottomNavigationView) {
        for (i in 0 until bottomNavigationView.menu.size()) {
            val menuItem = bottomNavigationView.menu.getItem(i)
            val customView =
                LayoutInflater.from(this).inflate(R.layout.bottom_navigation_item, null)
            val icon = customView.findViewById<ImageView>(R.id.icon)
            icon.setImageDrawable(menuItem.icon)
            menuItem.actionView = customView
        }
    }
}