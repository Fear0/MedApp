package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.parcel.Parcelize
import okhttp3.*
import java.io.*
import java.lang.Exception
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class ProductActivity() :Serializable {
    private var barCodes : ArrayList<String> = arrayListOf()
    private var products : ArrayList<Product>  = arrayListOf()
    private var key : String = String() //"62wirlrvew94wfwv5qfse47mowzuhb"


    public fun getBarCodes() : MutableList<String> {
        return barCodes;
    }

    public fun getProducts() : ArrayList<Product> {
        return this.products;
    }
    public fun setBarCodes( codes : ArrayList<String>){
        this.barCodes = codes
    }

    public fun setKey( key : String){
        this.key = key;
    }

    public  fun getKey() : String{
        return this.key;
    }


    /* This method is responsible for fetching the barcodes from the API
    and stores them in Products class.
     */
    public fun getProductJSON() {
        val endpoint = "https://api.barcodelookup.com/v3/products?"
        for (item: String in barCodes) {
            println("haw zebi"+item)
            val client = OkHttpClient()
            val url = endpoint + "barcode=" +  item + "&key=" + key
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("http request:", "failed to execute request")
                }

                override fun onResponse(call: Call, response: Response) {
                    println("call code: " + response.code)
                    if (response.code != 200){
                      //  products.add(Product("0"))
                        return
                    }
                    val body = response?.body?.string()
                    println("response is:$body")
                    val gson = GsonBuilder().create()
                    val productsFromJSON = gson.fromJson(body, Products::class.java)


                    products.add(productsFromJSON.products.last())
                    println("this is the last product in the products table " + products[products.size-1])

                    println("product" + products.last())
                }

            })
        }
    }

}

// The Products class the stores the fetched product

data class Products(val products : List<Product>) :Serializable
data class Review (val name : String, val rating : Int, val title : String, val review : String, val datetime: String) : Serializable {
    override fun toString(): String {
        return "name: $name \nrating: $rating \ntitle: $title \nreview: $review"
    }
}

// Each fetched JSON string from the api is represented by a Product objected that stores all the necessary attributes
 data class Product constructor (var barcode_number : String ) : Serializable{
     var title: String = String();
      var category: String = String();
     var brand: String = String();
      var description: String = String();
     var images : List<String> = ArrayList<String>();
      var reviews : List<Review> = ArrayList<Review>();

     override fun toString(): String {
         return "Product: barcode_number=$barcode_number \ntitle=$title \ncategory=$category \nbrand=$brand \ndescription$description \nImageURL$images \nReviews:$reviews"
     }
 }

