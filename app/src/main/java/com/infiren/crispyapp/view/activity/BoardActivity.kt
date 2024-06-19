package com.infiren.crispyapp.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.infiren.crispyapp.R
import com.infiren.crispyapp.model.BoardInfoModel
import com.infiren.crispyapp.model.Card
import com.infiren.crispyapp.model.Column
import com.infiren.crispyapp.service.RetrofitClient
import com.infiren.crispyapp.util.SharedPrefsHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BoardActivity : AppCompatActivity() {

    private lateinit var buttonAddCard: ImageButton
    private lateinit var linearLayoutPlanned: LinearLayout
    private lateinit var boardName: String
    private var token: String? = null
    private var boardId: Int = 0
    private var toDoColumnId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        buttonAddCard = findViewById(R.id.button_add_card)
        linearLayoutPlanned = findViewById(R.id.linearLayoutPlanned)

        boardName = intent.getStringExtra("BOARD_NAME") ?: "Board"
        boardId = intent.getIntExtra("BOARD_ID", 0)
        findViewById<TextView>(R.id.board_name).text = boardName

        addPlannedHeader()

        buttonAddCard.setOnClickListener {
            showCreateCardDialog()
        }

        loadBoardData()
    }

    private fun loadBoardData() {
        token = SharedPrefsHelper(this).getToken()
        if (token != null) {
            RetrofitClient.getClient(this).getBoard("Bearer $token", boardId)
                .enqueue(object : Callback<BoardInfoModel> {
                    override fun onResponse(call: Call<BoardInfoModel>, response: Response<BoardInfoModel>) {
                        if (response.isSuccessful) {
                            response.body()?.let { board ->
                                // Найти колонку "To do"
                                val toDoColumn = board.columns.find { it.name == "To do" }
                                toDoColumnId = toDoColumn?.id
                                displayCards(toDoColumn?.cards ?: emptyList())
                            }
                        } else {
                            Toast.makeText(this@BoardActivity, "Ошибка загрузки данных: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<BoardInfoModel>, t: Throwable) {
                        Toast.makeText(this@BoardActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            navigateToLogin()
        }
    }

    private fun displayCards(cards: List<Card>) {
        linearLayoutPlanned.removeAllViews()
        addPlannedHeader() // Добавить заголовок обратно после очистки
        for (card in cards) {
            addCardToLayout(card)
        }
    }

    private fun addPlannedHeader() {
        val textView = TextView(this).apply {
            text = "Запланировано"
            textSize = 18f
            setTextColor(resources.getColor(R.color.sand_light, theme))
            setPadding(16, 16, 16, 16)
        }
        linearLayoutPlanned.addView(textView)
    }

    private fun showCreateCardDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_card, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialogBuilder.show()

        val editTextCardName = dialogView.findViewById<EditText>(R.id.editTextWorkspaceName)
        val editTextCardDescription = dialogView.findViewById<EditText>(R.id.editTextCardDescription)
        val buttonCreateCard = dialogView.findViewById<Button>(R.id.buttonCreateCard)
        val buttonCloseDialog = dialogView.findViewById<ImageButton>(R.id.buttonCloseDialog)

        buttonCreateCard.setOnClickListener {
            val cardName = editTextCardName.text.toString()
            val cardDescription = editTextCardDescription.text.toString()
            if (cardName.isNotEmpty()) {
                createCard(cardName, cardDescription)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Введите название карточки", Toast.LENGTH_SHORT).show()
            }
        }

        buttonCloseDialog.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun createCard(cardName: String, cardDescription: String) {
        token?.let {
            val cardData = mapOf(
                "name" to cardName,
                "description" to cardDescription,
                "creationTime" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "deadLineTime" to LocalDateTime.now().plusYears(1).format(DateTimeFormatter.ISO_DATE_TIME),
                "storyPoints" to "0"
            )
            toDoColumnId?.let { columnId ->
                RetrofitClient.getClient(this).createCard(columnId, cardData, "Bearer $it")
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                loadBoardData()
                            } else {
                                Toast.makeText(this@BoardActivity, "Ошибка создания карточки: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@BoardActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    private fun addCardToLayout(card: Card) {
        val cardView = layoutInflater.inflate(R.layout.item_card, linearLayoutPlanned, false)
        val textCardName = cardView.findViewById<TextView>(R.id.textCardName)
        textCardName.text = card.name
        linearLayoutPlanned.addView(cardView)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}