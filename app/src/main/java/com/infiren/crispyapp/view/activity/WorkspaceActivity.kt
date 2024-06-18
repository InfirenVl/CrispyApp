package com.infiren.crispyapp.view.activity

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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.infiren.crispyapp.R

class WorkspaceActivity : AppCompatActivity() {

    private lateinit var buttonAddBoard: ImageButton
    private lateinit var boardContainer: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var workspaceName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace)

        buttonAddBoard = findViewById(R.id.button_add_board)
        boardContainer = findViewById(R.id.board_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        workspaceName = intent.getStringExtra("WORKSPACE_NAME") ?: "Workspace"
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

        updateAddBoardButtonVisibility()
    }

    private fun showCreateBoardDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null)
        val dialogBuilder = android.app.AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialogBuilder.show()

        val editTextBoardName = dialogView.findViewById<EditText>(R.id.editTextBoardName)
        val buttonCreateBoard = dialogView.findViewById<Button>(R.id.buttonCreateBoard)

        buttonCreateBoard.setOnClickListener {
            val boardName = editTextBoardName.text.toString()
            if (boardName.isNotEmpty()) {
                addBoardCard(boardName)
                alertDialog.dismiss()
            } else {
                // Show error message if the name is empty
                editTextBoardName.error = "Введите название"
            }
        }
    }

    private fun addBoardCard(boardName: String) {
        val cardView = LayoutInflater.from(this).inflate(R.layout.item_board, boardContainer, false)

        val textBoardName = cardView.findViewById<TextView>(R.id.textBoardName)
        val buttonEdit = cardView.findViewById<ImageButton>(R.id.buttonEdit)

        textBoardName.text = boardName

        buttonEdit.setOnClickListener {
            // Handle edit click
        }

        boardContainer.addView(cardView)
        updateAddBoardButtonVisibility()
    }

    private fun updateAddBoardButtonVisibility() {
        buttonAddBoard.visibility = if (boardContainer.childCount < 3) View.VISIBLE else View.GONE
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