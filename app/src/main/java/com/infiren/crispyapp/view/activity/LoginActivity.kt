package com.infiren.crispyapp.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.infiren.crispyapp.R
import com.infiren.crispyapp.model.UserSignIn
import com.infiren.crispyapp.presenter.LoginPresenter
import com.infiren.crispyapp.util.SharedPrefsHelper
import com.infiren.crispyapp.view.LoginView

class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createTextView: TextView
    private lateinit var presenter: LoginPresenter
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        createTextView = findViewById(R.id.text_create)

        presenter = LoginPresenter(this)
        sharedPrefsHelper = SharedPrefsHelper(this)


        val token = sharedPrefsHelper.getToken()
        if (token != null) {
            navigateToMain()
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val userSignIn = UserSignIn(username, password)
                presenter.loginUser(userSignIn)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        createTextView.setOnClickListener {
            navigateToRegistration()
        }
    }

    override fun onLoginSuccess(token: String) {
        sharedPrefsHelper.saveToken(token)
        Toast.makeText(this, "Вход успешен", Toast.LENGTH_SHORT).show()
        navigateToMain()
    }

    override fun onLoginError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun getContext(): Context {
        return this
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}