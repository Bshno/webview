package com.example.myfirst

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.window.SplashScreen
import androidx.annotation.RequiresApi
import com.example.myfirst.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize WebView
        val webView: WebView = findViewById(R.id.webView)
        val progressBar: View = findViewById(R.id.progressBar)
        setupWebView()


        binding.retryButton.setOnClickListener {
            if (isNetworkAvailable()) {
                binding.webView.visibility = View.VISIBLE
                binding.errorLayout.visibility = View.GONE
                if (webView.canGoBack())  binding.webView.reload()
                else webView.loadUrl("https://google.com")
            } else {
                Toast.makeText(this, "Still no internet connection.", Toast.LENGTH_SHORT).show()
            }
        }




        // Set WebChromeClient to track progress
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    progressBar.visibility = View.VISIBLE
                    webView.visibility=View.GONE
                } else {
                    progressBar.visibility = View.GONE
                    webView.visibility=View.VISIBLE
                }
            }
        }

        // Enable JavaScript (if needed)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode=WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.apply { isFocusable=true
        isFocusableInTouchMode=true
        setLayerType(View.LAYER_TYPE_HARDWARE,null
        )}



    }//end on Create

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val webView: WebView = findViewById(R.id.webView)
        if (webView.canGoBack()) {
            webView.goBack()
            binding.errorLayout.visibility=View.GONE
        } else {
            super.onBackPressed()
        }
    }





    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(binding.webView) {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false // Load URLs in the WebView itself
                }

                @Deprecated("Deprecated in Java")
                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    showErrorPage()
                }

                override fun onReceivedHttpError(
                    view: WebView,
                    request: WebResourceRequest,
                    errorResponse: WebResourceResponse
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    showErrorPage()
                }
            }

            val isDarkMode =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

            if (isNetworkAvailable()) {

                    with(binding.webView) {
                        if (isDarkMode) {
                            settings.forceDark = WebSettings.FORCE_DARK_ON
                        }
                        loadUrl("https://google.com") // Load your URL here
                    }

                binding.webView.visibility = View.VISIBLE
                binding.errorLayout.visibility = View.GONE
            } else {
                showErrorPage()
            }





        }
    }

    // Display a custom error page
    private fun showErrorPage() {

        binding.webView.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE



    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
    }



    override fun onDestroy() {
        // Clear WebView resources
        binding.webView.clearCache(true)
        binding.webView.clearHistory()
        binding.webView.destroy()
        super.onDestroy()
    }



}