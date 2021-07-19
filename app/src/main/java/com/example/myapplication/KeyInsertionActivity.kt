package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import okhttp3.*
import java.io.IOException

class KeyInsertionActivity : AppCompatActivity() {

    lateinit var startScanButton: Button
    lateinit var textInputLayout: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key_insertion)
        startScanButton = findViewById<Button>(R.id.start_scan_button)
        textInputLayout = findViewById(R.id.textInputLayout)

        // remembers the key input from the last session
        val apiKey = loadKey()

        textInputLayout.editText?.setText(apiKey)
        val hintTextField = findViewById<TextView>(R.id.hintToInsertKey)
        val linkToAPIWebsite = findViewById<TextView>(R.id.linkToWebsiteText)
        hintTextField.text = "Please Insert a valid API Key from BARCODE LOOKUP"
        hintTextField.setTextColor(Color.rgb(155, 33, 8))
        linkToAPIWebsite.movementMethod = LinkMovementMethod.getInstance()

        // The startScanButton will check if the entered key is valid through a normal api call
        startScanButton.setOnClickListener {

            // the key is the apikey from the user's account, that allows him to perform api calls
            val apiKey = textInputLayout.editText?.text.toString();

            //save the key for the next session to not have to enter it again
            saveKey()
            println(apiKey)
            val client = OkHttpClient()
            val endpoint = "https://api.barcodelookup.com/v2/products?"
            val url = endpoint + "barcode=" + "3661434004308" + "&key=" +  apiKey;
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {

                // in case of failure an output message is returned
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("http request:", "failed to execute request")

                }

                //in case of success the app will redirect to the MainActivity where products can be scanned
                override fun onResponse(call: Call, response: Response) {
                    println("zebi" + response.body?.string())
                    println("call code:"+ response.code)
                     if (response.code != 200) {
                        println("Invalid API key")
                   } else {
                    val intent = Intent(this@KeyInsertionActivity, MainActivity::class.java).apply {
                        putExtra("key", apiKey)
                            }
                         startActivity(intent)
                    }

                }
            })



        }


    }

    // save the key in the preferences to avoid reentering it in the next session
    private fun saveKey(){
        val sharedPref: SharedPreferences = getSharedPreferences("shared preferences" , Context.MODE_PRIVATE)
        val key = textInputLayout.editText?.text.toString();
        with(sharedPref.edit()){
            putString("APIKey", key);
            apply()
        }
    }

    //loads the saved key
    private fun loadKey() : String? {
        val sharedPref: SharedPreferences = getSharedPreferences("shared preferences" , Context.MODE_PRIVATE)

        return sharedPref.getString("APIKey", null)
    }
}