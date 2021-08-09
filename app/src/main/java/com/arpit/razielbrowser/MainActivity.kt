package com.arpit.razielbrowser

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    lateinit var downloadListener: DownloadListener
    var writeAccess = false
    private val PERMISSION_REQUEST_CODE = 1234
    private var mUploadMessage: ValueCallback<Uri?>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null

   lateinit var sharedPref : SharedPreferences
   lateinit var editor: SharedPreferences.Editor

    private val mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         sharedPref = applicationContext.getSharedPreferences("myPref" , Context.MODE_PRIVATE)
        editor = sharedPref.edit()

        setUpTabBar()
        webViewSetup()
        checkWriteAccess()
        createDownloadListener()
        onDownloadComplete()

        if (ActivityCompat.checkSelfPermission(this , android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this , arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE) , 666)
        }
        webView.setDownloadListener(downloadListener)
        webView!!.setWebChromeClient(object : WebChromeClient() {

            override fun onShowFileChooser(mWebView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }
                uploadMessage = filePathCallback
                var intent: Intent? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent = fileChooserParams.createIntent()
                }
                try {
                    startActivityForResult(intent, 100)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    return false
                }
                return true
            }
        })
    }

    private fun createDownloadListener()
    {
        downloadListener = DownloadListener { url, userAgent, contentDescription, mimetype, contentLength ->

            val request = DownloadManager.Request(Uri.parse(url))
            request.allowScanningByMediaScanner()

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val fileName = URLUtil.guessFileName(url, contentDescription, mimetype)

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val dManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            if(writeAccess) {
                dManager.enqueue(request)
            }
            else{
                Toast.makeText(applicationContext,"Unable to download file. Required Privileges are not available.", Toast.LENGTH_LONG).show()
                checkWriteAccess()
            }
        }

    }


    private fun checkWriteAccess()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Required permission to write external storage to save downloaded file.")
                    builder.setTitle("Please Grant Write Permission")
                    builder.setPositiveButton("OK") { _, _->
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                PERMISSION_REQUEST_CODE
                        )
                    }
                    builder.setNeutralButton("Cancel", null)
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE
                    )
                }
            }
            else {
                writeAccess = false
            }
        }
    }


    private fun onDownloadComplete()
    {
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                Toast.makeText(applicationContext,"File Downloaded", Toast.LENGTH_LONG).show()
            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private fun webViewSetup() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                editor.apply{

                    putString("lastUrl" , url)
                    apply()
                }
                etSearch.setText(webView.url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                progressBar.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }
            
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress

                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE)
                }
                super.onProgressChanged(view, newProgress)
            }
        }

        buSearch.setOnClickListener {
            webView.apply {
                val inputURL = etSearch.text
                loadUrl(inputURL.toString())
                val displayURL = url

                etSearch.setText(displayURL)

                editor.apply{

                    putString("lastUrl" , url)
                    apply()
                }
                settings.javaScriptEnabled = true
                settings.allowFileAccessFromFileURLs = true
                settings.displayZoomControls = true
                settings.allowUniversalAccessFromFileURLs = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.allowFileAccess = true
                settings.domStorageEnabled = true
                settings.allowContentAccess = true
                settings.allowFileAccess = true
                settings.javaScriptCanOpenWindowsAutomatically = true

            }
        }

        val urlLast = sharedPref.getString("lastUrl" , null)
        if (urlLast.isNullOrEmpty()){
            webView.apply {
                loadUrl("https://google.com")
                val displayURL = url
                etSearch.setText(displayURL)

                editor.apply{

                    putString("lastUrl" , url)
                    apply()
                }
                settings.javaScriptEnabled = true
                settings.allowFileAccessFromFileURLs = true
                settings.displayZoomControls = true
                settings.allowUniversalAccessFromFileURLs = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.allowFileAccess = true
                settings.domStorageEnabled = true
                settings.allowContentAccess = true
                settings.allowFileAccess = true
                settings.javaScriptCanOpenWindowsAutomatically = true

            }

        } else{
            webView.apply {
                loadUrl(urlLast)
            }
        }






    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == 100) {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                uploadMessage = null
            }

        } else if (requestCode == 101) {
            if (null == mUploadMessage) return
            val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 666 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== 1234) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeAccess=true
            } else {
                // Permission denied
                writeAccess=false
                Toast.makeText(applicationContext,"Permission Denied. This app will not work with right permission.", Toast.LENGTH_LONG).show()
            }
        }
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

//    override fun onRestart() {
//
//        val urlLast = sharedPref.getString("lastUrl" , null)
//        webView.apply {
//            loadUrl(urlLast)
//        }
//        super.onRestart()
//    }

            override fun onResume() {
                val urlLast = sharedPref.getString("lastUrl", null)
                webView.apply {
                    loadUrl(urlLast)
                }
                super.onResume()
            }

            override fun onDestroy() {
                sharedPref.edit().clear().apply()
//        val empty = ""
//        editor.apply{
//            putString("lastUrl" , empty )
//            apply()
//        }
                super.onDestroy()
            }
        }
