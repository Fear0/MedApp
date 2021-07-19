package com.example.myapplication

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_reviews.*

class ReviewsActivity : AppCompatActivity() {

        private lateinit var  product : Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)
        product = (intent.getSerializableExtra("ProductForReviews") as Product?)!!
        val productBarcode = product.barcode_number
        val linkField = findViewById<TextView>(R.id.linkToProduct)
        val productLink = "https://www.barcodelookup.com/$productBarcode"
        val linkedText = String.format("<a href=\"%s \"> click here to submit your own review </a>",productLink)
        linkField.text = (HtmlCompat.fromHtml(linkedText,HtmlCompat.FROM_HTML_MODE_LEGACY))
        linkField.movementMethod = LinkMovementMethod.getInstance()

        setUpRecyclerView(findViewById(R.id.recyclerView))
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = ReviewsAdapter(product.reviews)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}