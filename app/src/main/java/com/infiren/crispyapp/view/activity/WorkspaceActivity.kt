package com.infiren.crispyapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.infiren.crispyapp.R
import com.infiren.crispyapp.model.BoardInfo
import com.infiren.crispyapp.model.BoardName
import com.infiren.crispyapp.model.WorkspaceIdInfo
import com.infiren.crispyapp.service.RetrofitClient
import com.infiren.crispyapp.util.SharedPrefsHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class WorkspaceActivity : AppCompatActivity() {

    private lateinit var buttonAddBoard: ImageButton
    private lateinit var boardContainer: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var workspaceName: String
    private var workspaceId: Int = 0
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace)

        buttonAddBoard = findViewById(R.id.button_add_board)
        boardContainer = findViewById(R.id.board_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        workspaceName = intent.getStringExtra("WORKSPACE_NAME") ?: "Workspace"
        workspaceId = intent.getIntExtra("WORKSPACE_ID", 0)
        findViewById<TextView>(R.id.workspace_name).text = workspaceName

        buttonAddBoard.setOnClickListener {
            showCreateBoardDialog()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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
        loadWorkspaceData()
    }

    private fun loadWorkspaceData() {
        token = SharedPrefsHelper(this).getToken()
        if (token != null) {
            RetrofitClient.getClient(this).getWorkspace(workspaceId, "Bearer $token")
                .enqueue(object : Callback<WorkspaceIdInfo> {
                    override fun onResponse(call: Call<WorkspaceIdInfo>, response: Response<WorkspaceIdInfo>) {
                        if (response.isSuccessful) {
                            response.body()?.let { workspace ->
                                displayBoards(workspace.boards)
                            }
                        } else {
                            Toast.makeText(this@WorkspaceActivity, "Ошибка загрузки данных: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<WorkspaceIdInfo>, t: Throwable) {
                        Toast.makeText(this@WorkspaceActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            navigateToLogin()
        }
    }

    private fun displayBoards(boards: List<BoardInfo>) {
        boardContainer.removeAllViews()
        for (board in boards) {
            addBoardCard(board.name)
        }
    }

    private fun showCreateBoardDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialogBuilder.show()

        val editTextBoardName = dialogView.findViewById<EditText>(R.id.editTextBoardName)
        val buttonCreateBoard = dialogView.findViewById<Button>(R.id.buttonCreateBoard)
        val buttonCloseDialog = dialogView.findViewById<ImageButton>(R.id.buttonCloseDialog)

        buttonCloseDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        buttonCreateBoard.setOnClickListener {
            val boardName = editTextBoardName.text.toString()
            if (boardName.isNotEmpty()) {
                createBoard(boardName)
                alertDialog.dismiss()
            } else {
                editTextBoardName.error = "Введите название"
            }
        }
    }

    private fun createBoard(boardName: String) {
        token?.let {
            val boardData = BoardName(boardName)
            RetrofitClient.getClient(this).createBoard(workspaceId, boardData, "Bearer $it")
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            loadWorkspaceData() // Обновить данные рабочего пространства
                        } else {
                            Toast.makeText(this@WorkspaceActivity, "Ошибка создания доски: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@WorkspaceActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun addBoardCard(boardName: String) {
        val cardView = LayoutInflater.from(this).inflate(R.layout.item_board, boardContainer, false)

        val textBoardName = cardView.findViewById<TextView>(R.id.textBoardName)
        val buttonEdit = cardView.findViewById<ImageButton>(R.id.buttonEdit)

        textBoardName.text = boardName

        cardView.setOnClickListener {
            navigateToBoard(boardName)
        }

        buttonEdit.setOnClickListener {
            showAddUserDialog()
        }

        boardContainer.addView(cardView)
    }

    private fun showAddUserDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_workspace_add_user, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialogBuilder.show()

        val editTextAddUser = dialogView.findViewById<EditText>(R.id.editAddUser)
        val buttonAddUs = dialogView.findViewById<Button>(R.id.buttonAddUs)
        val buttonCloseDialog = dialogView.findViewById<ImageButton>(R.id.buttonCloseDialog)

        buttonCloseDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        buttonAddUs.setOnClickListener {
            val username = editTextAddUser.text.toString()
            if (username.isNotEmpty()) {
                addUserToWorkspace(username)
                alertDialog.dismiss()
            } else {
                editTextAddUser.error = "Введите логин"
            }
        }
    }

    private fun addUserToWorkspace(username: String) {
        token?.let {
            RetrofitClient.getClient(this).addUserToWorkspace(workspaceId, username, "Bearer $it")
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@WorkspaceActivity, "Пользователь добавлен", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@WorkspaceActivity, "Ошибка добавления пользователя: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@WorkspaceActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun navigateToBoard(boardName: String) {
        val intent = Intent(this, BoardActivity::class.java)
        intent.putExtra("BOARD_NAME", boardName)
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigationView(bottomNavigationView: BottomNavigationView) {
        for (i in 0 until bottomNavigationView.menu.size()) {
            val menuItem = bottomNavigationView.menu.getItem(i)
            val customView = LayoutInflater.from(this).inflate(R.layout.bottom_navigation_item, null)
            val icon = customView.findViewById<ImageView>(R.id.icon)
            icon.setImageDrawable(menuItem.icon)
            menuItem.actionView = customView
        }
    }
}