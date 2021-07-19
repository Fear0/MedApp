package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.dummy.DummyContent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [itemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class itemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var key : String = String()
    private var twoPane: Boolean = false
    private  var newItemsSize : Int = 0
    lateinit var scanMoreButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        scanMoreButton = findViewById(R.id.scan_more_button);

        scanMoreButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {

            }
            startActivity(intent)
        }
        var savedItems = loadData()
        //savedItems = null
        if (savedItems != null) {
            for (item: DummyContent.DummyItem in savedItems) {
                if (!DummyContent.ITEM_MAP.contains(item.product.barcode_number)) {
                    DummyContent.addItem(
                        DummyContent.DummyItem(
                            item.product.barcode_number,
                            item.product
                        )
                    )
                }
            }
        }

        val productToDelete : String? = intent.getStringExtra("ProductToDelete")
        println("product barcode to delete:$productToDelete")
        for (item : DummyContent.DummyItem in DummyContent.ITEMS) {
            if (item.product.barcode_number == productToDelete){
                DummyContent.ITEMS.remove(item)
                DummyContent.ITEM_MAP.remove(item.product.barcode_number)
            }
        }

        val products : ProductActivity? = intent.getSerializableExtra("Products") as ProductActivity?
        println("products from main activity: " + products?.getProducts())
//        newItemsSize = products?.getProducts()?.size!!
        if (products != null) {
            for (product : Product in products.getProducts()){
                if (product.barcode_number != productToDelete  && !DummyContent.ITEM_MAP.contains(product.barcode_number)) {
                    DummyContent.addItem(DummyContent.DummyItem(product.barcode_number, product))
                    println(product)
                }
            }
        }

        saveData()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(findViewById(R.id.include),newItemsSize)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, size : Int) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, twoPane, size)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: itemListActivity,
        private val values: List<DummyContent.DummyItem>,
        private val twoPane: Boolean,
        private val size: Int
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                if (twoPane) {
                    val fragment = itemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(itemDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, itemDetailActivity::class.java).apply {
                        putExtra(itemDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]

            holder.idView.setTextColor(Color.rgb(186,30,30))
            holder.idView.text = item.id
            holder.contentView.text = item.product.title


            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text)
            val contentView: TextView = view.findViewById(R.id.content)
        }
    }

    private fun saveData(){
        val sharedPref: SharedPreferences = getSharedPreferences("shared preferences" , Context.MODE_PRIVATE)

        with(sharedPref.edit()){
            val gson = GsonBuilder().create()
            val items = DummyContent.ITEMS
            val json = gson.toJson(items)
            putString("MyProducts", json)
            apply()
        }
    }
    private fun loadData() : MutableList<DummyContent.DummyItem>?{
        val sharedPref: SharedPreferences = getSharedPreferences("shared preferences" , Context.MODE_PRIVATE)
        val gson = GsonBuilder().create()
        val json = sharedPref.getString("MyProducts", null)
        val type  : Type = object : TypeToken<MutableList<DummyContent.DummyItem>>(){}.type
        val products: MutableList<DummyContent.DummyItem>? = gson.fromJson(json,type)
        return products
    }

    data class ItemsClass(val items : MutableList<DummyContent.DummyItem>)
}