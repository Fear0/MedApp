package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import okhttp3.internal.wait
import java.io.Serializable
import java.net.DatagramPacket
import java.util.jar.Manifest
import java.util.jar.Pack200

private const val CAMERA_REQUEST_CODE = 1001

class MainActivity : AppCompatActivity() {

    private var key : String = String()
    private lateinit var codeScanner: CodeScanner
    private var barCodes : ArrayList<String> = arrayListOf<String>() //9780140157376 "3661434004308"
    lateinit var btnGetSensorData: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetSensorData = findViewById(R.id.scan_button);
        checkPermission(android.Manifest.permission.CAMERA,"VideoScan", CAMERA_REQUEST_CODE)
        barcodeScanner()

        /* when clicked on the scan button, a ProductActivity object will be created from those barcodes. Its getProdutJSON method
        will then make the api calls for each product and turn the json into a Product class.
        These products will then be fetched and sent to the itemListActivity class to be displayed.
         */
        btnGetSensorData.setOnClickListener {
            if (!barCodes.isEmpty()){
                val productActivity = ProductActivity()
                val key = intent.getStringExtra("key")
                println("The key is: $key");
                if (this.key.isEmpty()) {
                    if (key != null) {
                        productActivity.setKey(key)
                        this.key = key
                    }
                }
                println("barcodes in the table:")
                println(barCodes)
                    productActivity.setBarCodes(barCodes)
                    productActivity.getProductJSON()
                /* this waiting status is needed because the parsing takes some time and we dont want to send the data to the itemListActivity
                when it is not yet created.
                 */
               if (/*!productActivity.getProducts().contains(Product("0")) &&*/ productActivity.getProducts().size != 1) {
                    while (productActivity.getProducts().isEmpty()) {
                        Thread.sleep(1000)
                    }
                }

                // send the products to the itemListActivity to be displayed
                    println("The products to be sent are: " + productActivity.getProducts())
                    val intent = Intent(this, itemListActivity::class.java).apply {
                            putExtra("Products",productActivity)
                            putExtra("TheKey",key)
                    }
                startActivity(intent)
            }
            val toast = Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()

        }

    }

    // this function takes care of all the scanning process
    private fun barcodeScanner() {
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val textView = findViewById<TextView>(R.id.textView)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            // here will every scanned code be added in the barcodes list
            decodeCallback = DecodeCallback {
                runOnUiThread {
                    textView.text = it.text
                    if(!barCodes.contains(it.text)) {
                        barCodes.add(it.text)
                    }
                    println(barCodes.last())
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Camera activation error:'${it.message}")
                }
            }
            scannerView.setOnClickListener {
                codeScanner.startPreview()
            }

        }
    }

    override fun onResume() {
        super.onResume()
       // codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    // this functions allows for giving access to camera
    fun checkPermission(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat
                    .checkSelfPermission(applicationContext, permission) -> {
                    Toast.makeText(
                        applicationContext,
                        "$name␣Berechtigung␣erfolgreich", Toast.LENGTH_SHORT
                    ).show()
                }
                // Convince user to accept the request
                else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission), requestCode
                )
            }
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"You need camera permission", Toast.LENGTH_SHORT).show()
                }
                else {
                 //success
                }
            }
        }
    }
}