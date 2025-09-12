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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val textviewQuestion = findViewById<TextView>(R.id.textView_question)
        val textviewReponse = findViewById<TextView>(R.id.textView_response)


        textviewReponse.visibility = View.INVISIBLE
        textviewQuestion.setOnClickListener {
            textviewQuestion.visibility = View.INVISIBLE
            textviewReponse.visibility = View.VISIBLE
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


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

//            Snackbar.make(findViewById(R.id.editTextQuestion),
//                "The message to display",
//                Snackbar.LENGTH_SHORT)
//                .show()
        }


        findViewById<View>(R.id.myBtn).setOnClickListener{
            val intent = Intent (this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }



    }
}