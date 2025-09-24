package com.example.flashcardapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

lateinit var flashcardDatabase: FlashcardDatabase
var allFlashcards = mutableListOf<Flashcard>()
var currentCardDisplayIndex = 0



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.nextBtn).setOnClickListener {
            if (allFlashcards.size == 0){
                return@setOnClickListener
            }
            currentCardDisplayIndex++


            if (currentCardDisplayIndex >= allFlashcards.size){
                Snackbar.make(
                    findViewById(R.id.main),
                    "You've reached the end of the cards, going back to start",
                    Snackbar.LENGTH_SHORT
                ).show()
                currentCardDisplayIndex = 0
            }

            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
            val(question, answer) = allFlashcards[currentCardDisplayIndex]



            findViewById<TextView>(R.id.textView_question).text = question
            findViewById<TextView>(R.id.textView_response).text = answer

        }

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        if (allFlashcards.size > 0) {

            findViewById<TextView>(R.id.textView_question).text = allFlashcards[0].question
            findViewById<TextView>(R.id.textView_response).text = allFlashcards[0].answer

        }

        val textviewQuestion = findViewById<TextView>(R.id.textView_question)
        val textviewReponse = findViewById<TextView>(R.id.textView_response)


        textviewReponse.visibility = View.INVISIBLE
        textviewQuestion.setOnClickListener {
            textviewQuestion.visibility = View.INVISIBLE
            textviewReponse.visibility = View.VISIBLE
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val data: Intent? = result.data
            val extras= data?.extras

            if (extras != null) {
                val question = extras.getString("card_question")
                val answer = extras.getString("card_reponse")
                Log.i("MainActivity", "question: $question")
                Log.i("MainActivity", "answer: $answer")

                findViewById<TextView>(R.id.textView_question).text = question
                findViewById<TextView>(R.id.textView_response).text = answer


        if (question != null && answer != null) {
            flashcardDatabase.insertCard(Flashcard(question, answer))
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        }else{
        Log.e("TAG","missing question or answer to input into database. Question is $question and answer is $answer")}
            }

            else {
                Log.i("MainActivity", "Returned null data from AddCardActivity")
            }



            if (result.resultCode == RESULT_OK){
                val data: Intent? = result.data
                val question = data?.getStringExtra("card_question")
                val reponse = data?.getStringExtra("card_reponse")

                Log.i("MainActivity", "question: $question")
                Log.i("MainActivity", "reponse: $reponse")

                findViewById<TextView>(R.id.textView_question).text = question
                findViewById<TextView>(R.id.textView_response).text = reponse


            }else{
                Log.i("MainActivity", "Returned null data from AddCardActivity")
            }


        }


        findViewById<View>(R.id.myBtn).setOnClickListener{
            val intent = Intent (this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }



    }
}