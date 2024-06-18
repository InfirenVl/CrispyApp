package com.infiren.crispyapp.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.infiren.crispyapp.R
import com.infiren.crispyapp.model.UserModel
import com.infiren.crispyapp.presenter.RegistrationPresenter
import com.infiren.crispyapp.util.SharedPrefsHelper
import com.infiren.crispyapp.view.RegistrationView

class RegistrationActivity : AppCompatActivity(), RegistrationView {

    private lateinit var usernameEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var presenter: RegistrationPresenter
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private var isPasswordVisible = false
    private var isRepeatPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        usernameEditText = findViewById(R.id.editTextUsername)
        nameEditText = findViewById(R.id.editTextName)
        passwordEditText = findViewById(R.id.editTextPassword)
        repeatPasswordEditText = findViewById(R.id.editTextRepeatPassword)
        registerButton = findViewById(R.id.buttonRegister)

        presenter = RegistrationPresenter(this)
        sharedPrefsHelper = SharedPrefsHelper(this)

        val token = sharedPrefsHelper.getToken()
        if (token != null) {
            navigateToMain()
        }

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val name = nameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val repeatPassword = repeatPasswordEditText.text.toString()

            if (username.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()) {
                if (isLoginValid(username) && isPasswordValid(password)) {
                    if (password == repeatPassword) {
                        val user = UserModel(username, password, name)
                        presenter.registerUser(user)
                    } else {
                        Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (!isLoginValid(username)) {
                        Toast.makeText(
                            this,
                            "Логин должен содержать не менее 6 символов латиницей и цифр.",
                            Toast.LENGTH_LONG
                        ).show()
                    }else if (!isPasswordValid(password)) {
                        Toast.makeText(
                            this,
                            "Пароль должен содержать не менее 8 символов, включая буквы, цифры и спецсимволы.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        passwordEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[2].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    togglePasswordVisibility(passwordEditText, isPasswordVisible)
                    return@setOnTouchListener true
                }
            }
            false
        }

        repeatPasswordEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (repeatPasswordEditText.right - repeatPasswordEditText.compoundDrawables[2].bounds.width())) {
                    isRepeatPasswordVisible = !isRepeatPasswordVisible
                    togglePasswordVisibility(repeatPasswordEditText, isRepeatPasswordVisible)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility(editText: EditText, isVisible: Boolean) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editText.setSelection(editText.text.length)
    }

    private fun isLoginValid(login: String): Boolean {
        // Валидация логина: не менее 6 символов, включая буквы и цифры
        val loginRegex = "^[a-z0-9]{6,}$".toRegex()
        return loginRegex.matches(login)
    }

    private fun isPasswordValid(password: String): Boolean {
        // Валидация пароля: не менее 8 символов, включая буквы, цифры и специальные символы
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#\$%^&+=!]).{8,}$".toRegex()
        return passwordRegex.matches(password)
    }

    override fun onRegistrationSuccess() {
        Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRegistrationError(errorMessage: String) {
        Toast.makeText(this, "Ошибка регистрации: $errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun getContext(): Context {
        return this
    }

    fun onLoginClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}