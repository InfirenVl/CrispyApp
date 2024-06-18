package com.infiren.crispyapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.infiren.crispyapp.R
import com.infiren.crispyapp.view.adapter.HelpFragmentAdapter

class HelpActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        pager = findViewById(R.id.pager)
        val pagerAdapter = HelpFragmentAdapter(this)
        pager.adapter = pagerAdapter

        val buttonSkip: Button = findViewById(R.id.button_skip)
        buttonSkip.setOnClickListener {
            navigateToLoginActivity()
        }

        val buttonNext: Button = findViewById(R.id.button_next)
        buttonNext.setOnClickListener {
            if (pager.currentItem + 1 < pagerAdapter.itemCount) {
                pager.currentItem += 1
            } else {
                navigateToRegistrationActivity()
            }
        }
    }

    private fun navigateToRegistrationActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
