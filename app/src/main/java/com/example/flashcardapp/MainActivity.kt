package com.example.flashcardapp

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

        // pour supprimer une carte

        findViewById<View>(R.id.delete_btn).setOnClickListener {
            // On récupère la question actuellement affichée
            val flashcardQuestionToDelete = findViewById<TextView>(R.id.textView_question).text.toString()

            //  On la supprime de la base de données
            flashcardDatabase.deleteCard(flashcardQuestionToDelete)

            // On recharge la liste des cartes depuis la base de données
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()

            // On ajuste l’index courant (au cas où la dernière carte a été supprimée)
            if (allFlashcards.size == 0) {
                // Plus de cartes ! On affiche un message sympathique
                findViewById<TextView>(R.id.textView_question).text = "Aucune carte restante 😅"
                findViewById<TextView>(R.id.textView_response).text = ""
            } else {
                // Si l’index actuel dépasse la taille, on revient au début
                if (currentCardDisplayIndex >= allFlashcards.size) {
                    currentCardDisplayIndex = 0
                }

                // On affiche la carte suivante disponible
                val currentCard = allFlashcards[currentCardDisplayIndex]
                findViewById<TextView>(R.id.textView_question).text = currentCard.question
                findViewById<TextView>(R.id.textView_response).text = currentCard.answer
            }

            // Petit feedback utilisateur
            Snackbar.make(findViewById(R.id.main), "Carte supprimée ✅", Snackbar.LENGTH_SHORT).show()
        }







        findViewById<View>(R.id.nextBtn).setOnClickListener {
            if (allFlashcards.size == 0){
                return@setOnClickListener
            }

            val leftOutAnim = AnimationUtils.loadAnimation(it.context, R.anim.left_out)
            val rightInAnim = AnimationUtils.loadAnimation(it.context, R.anim.right_in)

            // Récupérer la vue de la carte
            val questionTextView = findViewById<TextView>(R.id.textView_question)
            val answerTextView = findViewById<TextView>(R.id.textView_response)

            // Écouteur pour savoir quand l'animation "sortie gauche" est terminée
            leftOutAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // Rien à faire ici pour le moment
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // Passer à la carte suivante une fois que l’animation "sortie" est terminée
                    currentCardDisplayIndex++

                    if (currentCardDisplayIndex >= allFlashcards.size) {
                        currentCardDisplayIndex = 0
                    }

                    val nextCard = allFlashcards[currentCardDisplayIndex]
                    questionTextView.text = nextCard.question
                    answerTextView.text = nextCard.answer

                    // Exécuter l'animation d'entrée droite
                    questionTextView.startAnimation(rightInAnim)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // Pas nécessaire ici
                }
            })

            // Lancer la première animation (sortie gauche)
            questionTextView.startAnimation(leftOutAnim)
            questionTextView.startAnimation(rightInAnim)


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

        // pour montre la response quand on clique sur la question en circle

        val questionSideView = findViewById<View>(R.id.textView_question)
        val responseSideView = findViewById<View>(R.id.textView_response)

        questionSideView.setOnClickListener {
            val cx = responseSideView.width / 2
            val cy = responseSideView.height / 2

            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(
                responseSideView, cx, cy, 0f, finalRadius
            )
            questionSideView.visibility = View.INVISIBLE
            responseSideView.visibility = View.VISIBLE
            anim.duration = 1000
            anim.start()

        }

        // L'inverse pour montre la question quand on clique sur la reponse en circle

        responseSideView.setOnClickListener {
            val cx = responseSideView.width / 2
            val cy = responseSideView.height / 2
            val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            val anim = ViewAnimationUtils.createCircularReveal(responseSideView, cx, cy, initialRadius, 0f)

            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // No action needed here
                }

                override fun onAnimationEnd(animation: Animator) {
                    responseSideView.visibility = View.INVISIBLE
                    questionSideView.visibility = View.VISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {
                    // No action needed here
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // No action needed here
                }


            })

            anim.duration = 1000
            anim.start()
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
            overridePendingTransition(R.anim.left_out, R.anim.right_in)
        }



    }
}