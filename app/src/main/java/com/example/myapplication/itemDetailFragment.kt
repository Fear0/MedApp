package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.dummy.DummyContent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_detail.view.*

/**
 * A fragment representing a single item detail screen.
 * This fragment is either contained in a [itemListActivity]
 * in two-pane mode (on tablets) or a [itemDetailActivity]
 * on handsets.
 */
class itemDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: DummyContent.DummyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title =
                    item?.product?.barcode_number

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        // Show the content of its product: title, description, image, brand and category
        item?.let {
            val itemImageView = rootView.findViewById<ImageView>(R.id.item_image)
            val reviewButton = rootView.findViewById<Button>(R.id.reviews_button)
            val deleteButton = rootView.findViewById<Button>(R.id.button_delete)
            val product = it.product
            Picasso.with(context).load(it.product.images.last()).into(itemImageView)
            rootView.findViewById<TextView>(R.id.item_name).text = it.product.title
            rootView.findViewById<TextView>(R.id.item_description).text = it.product.description
            rootView.findViewById<TextView>(R.id.item_brand).text = it.product.brand
            rootView.findViewById<TextView>(R.id.item_category).text = it.product.category

            // this button redirects to the reviews tab
            reviewButton.setOnClickListener {
                val intent = Intent(activity,ReviewsActivity::class.java).apply {
                    putExtra("ProductForReviews", product)
                }
                    startActivity(intent)
            }
            // this button allows deleting a product from the recycler view
            deleteButton.setOnClickListener {
                val intent = Intent(activity, itemListActivity::class.java).apply {
                    putExtra("ProductToDelete",product.barcode_number)
                }
                startActivity(intent)
            }
        }
        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}


