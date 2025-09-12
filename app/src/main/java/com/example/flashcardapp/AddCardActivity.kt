package com.example.flashcardapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_card)



        val editTextQuestion = findViewById<EditText>(R.id.editTextQuestion)
        val editTextReponse = findViewById<EditText>(R.id.editTextReponse)
        val saveBtn = findViewById<View>(R.id.saveBtn)


        saveBtn.setOnClickListener {
            val question = editTextQuestion.text.toString()
            val reponse = editTextReponse.text.toString()


            val resultIntent = Intent()
            resultIntent.putExtra("card_question", question)
            resultIntent.putExtra("card_reponse", reponse)
            setResult(RESULT_OK, resultIntent)
            finish()
        }



        findViewById<View>(R.id.cancelBtn).setOnClickListener{
         setResult(RESULT_CANCELED)
            finish()
        }


    }
}