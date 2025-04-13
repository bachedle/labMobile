package com.example.exercise2

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.exercise2.SentimentAnalyzer.analyzeSentiment
import java.util.Locale

class MainActivity : AppCompatActivity()
{
    private var inputText: EditText? = null
    private var analyzeButton: Button? = null
    private var rootLayout: LinearLayout? = null
    private var emoji: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupButtonClickListener()
    }

    private fun initializeViews()
    {
        inputText = findViewById(R.id.inputText)
        analyzeButton = findViewById(R.id.analyzeButton)
        rootLayout = findViewById(R.id.rootLayout)
        emoji = findViewById(R.id.emoji)
    }

    private fun setupButtonClickListener()
    {
        analyzeButton!!.setOnClickListener { view: View? ->
            val text = inputText!!.text.toString().trim { it <= ' ' }
            if (text.isEmpty())
            {
                showToast("Please enter some text!")
                return@setOnClickListener
            }
            analyzeSentiment(text)
        }
    }

    private fun analyzeSentiment(text: String)
    {
        analyzeSentiment(text) { sentiment: String ->
            runOnUiThread {
                Log.d("Sentiment", "Response: $sentiment")
                updateUI(sentiment)
            }
        }
    }

    private fun updateUI(sentiment: String)
    {
        val lowerSentiment = sentiment.lowercase(Locale.getDefault()).trim { it <= ' ' }
        val backgroundColor: Int
        val emojiRes: Int

        when (lowerSentiment)
        {
            "POS" -> {
                backgroundColor = Color.GREEN
                emojiRes = R.drawable.happy
            }

            "NEU" -> {
                backgroundColor = Color.YELLOW
                emojiRes = R.drawable.neutral
            }

            "NEG" -> {
                backgroundColor = Color.RED
                emojiRes = R.drawable.sad
            }

            else -> {
                backgroundColor = Color.WHITE
                emojiRes = 0
                showToast("Error: ${lowerSentiment}" )
            }
        }

        rootLayout!!.setBackgroundColor(backgroundColor)
        if (emojiRes != 0)
        {
            emoji!!.setImageResource(emojiRes)
        }
        else
        {
            emoji!!.setImageDrawable(null)
        }
    }

    private fun showToast(message: String)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}