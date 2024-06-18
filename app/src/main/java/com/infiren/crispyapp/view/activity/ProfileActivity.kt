package com.infiren.crispyapp.view.activity


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.infiren.crispyapp.R
import com.infiren.crispyapp.model.UserInfo
import com.infiren.crispyapp.presenter.ProfilePresenter
import com.infiren.crispyapp.util.SharedPrefsHelper
import com.infiren.crispyapp.view.ProfileView

class ProfileActivity : AppCompatActivity(), ProfileView {

    private lateinit var profileName: TextView
    private lateinit var editTextNewName: EditText
    private lateinit var profileChangeNameButton: ImageButton
    private lateinit var logoutButton: Button
    private lateinit var deleteAccountButton: Button
    private lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileName = findViewById(R.id.profile_name)
        profileChangeNameButton = findViewById(R.id.profile_ch_name)
        logoutButton = findViewById(R.id.button_logout)
        deleteAccountButton = findViewById(R.id.button_delete_account)

        presenter = ProfilePresenter(this)


        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        deleteAccountButton.setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }

        profileChangeNameButton.setOnClickListener {
            showChangeNameDialog()
        }

        val token = SharedPrefsHelper(this).getToken()
        if (token != null) {
            presenter.loadUserProfile(token)
        }
    }

    override fun onProfileLoaded(user: UserInfo) {
        profileName.text = user.name
    }

    override fun onProfileLoadError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onAccountDeleted() {
        SharedPrefsHelper(this).clearToken()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onNameChanged(newName: String) {
        profileName.text = newName
    }

    override fun getContext(): Context {
        return this
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_logout, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.button_confirm_logout).setOnClickListener {
            SharedPrefsHelper(this).clearToken()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.button_cancel_logout).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteAccountConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete_account, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.button_confirm_delete).setOnClickListener {
            val token = SharedPrefsHelper(this).getToken()
            if (token != null) {
                presenter.deleteUserAccount(token)
            }
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.button_cancel_delete).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showChangeNameDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_name, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialogBuilder.show()

        val editTextNewName = dialogView.findViewById<EditText>(R.id.editTextNewName)
        val buttonChangeName = dialogView.findViewById<Button>(R.id.buttonChangeName)
        val buttonCloseDialog = dialogView.findViewById<ImageButton>(R.id.buttonCloseDialog)

        buttonCloseDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        buttonChangeName.setOnClickListener {
            val newName = editTextNewName.text.toString()
            val token = SharedPrefsHelper(this).getToken()
            if (token != null && newName.isNotEmpty() && newName.length >= 4) {
                presenter.changeUserName(token, newName)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Имя должно содержать минимум 4 символа", Toast.LENGTH_SHORT).show()
            }
        }
    }
}