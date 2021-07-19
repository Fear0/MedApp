package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.review_list_content.view.*

class ReviewsAdapter( private val reviews : List<Review>): RecyclerView.Adapter<ReviewsAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_list_content, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        println("ReviewsSIze: " + reviews.size)

        // this reviewsAdapter method is responsible for writing the reviews
        if (!reviews.isEmpty()) {
            val review = reviews[position]
            holder.reviewField.text = review.toString()
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class CustomViewHolder(private val v : View) : RecyclerView.ViewHolder(v) {
        val reviewField: TextView = v.findViewById<TextView>(R.id.review_content)
    }

}