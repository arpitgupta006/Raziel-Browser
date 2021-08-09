package com.arpit.razielbrowser

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_main.bottomBar
import kotlinx.android.synthetic.main.activity_q_r_code.*

const val CAMERA_REQUEST_CODE = 101
class QRCode : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_code)



        setupPermission()
        codeScanner()
        codelink()
        setUpTabBar()
    }

    private fun setUpTabBar() {
        bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_browse -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_feed -> {
                    val intent = Intent(this, Feed::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_qrcode -> {
                    val intent = Intent(this, QRCode::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    true
                }
            }
        }
    }

    private fun codeScanner(){
        codeScanner = CodeScanner(this , scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    tvCode.text = it.text
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main" , "Camera Initialization Error: ${it.message}")
                }
            }


        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }


    }

    private fun codelink(){
        tvCode.setOnClickListener {
            val textToCopy = tvCode.text
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

    private fun setupPermission(){
        val permission = ContextCompat.checkSelfPermission(this ,
                android.Manifest.permission.CAMERA)

        if(permission!= PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this , arrayOf(android.Manifest.permission.CAMERA) , CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this , "You need the camera permission to use this app", Toast.LENGTH_LONG).show()
                } else{
                    //successful
                }
            }
        }

    }
}