package com.arpit.razielbrowser

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arpit.antinotasknewsapp.Article
import com.arpit.newsapp3.MyAdapter
import com.arpit.newsapp3.News
import com.arpit.newsapp3.newsService
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bottomBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Feed : AppCompatActivity() {

    lateinit var newsadapter : MyAdapter
    private var articlesList = mutableListOf<Article>()
    var pagenum = 1
    var totalResultsNews = -1
    private  lateinit var mAdView: AdView
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)


        newsadapter = MyAdapter(this, articlesList)
        rvNews.adapter = newsadapter
         val lAYoutManager =   LinearLayoutManager(this)
        rvNews.layoutManager = lAYoutManager


//        rvNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val visibleItemCount = lAYoutManager.childCount
//                val pastVisibleItem = lAYoutManager.findFirstVisibleItemPosition()
//                val total  = newsadapter.itemCount
//                if (visibleItemCount + pastVisibleItem>= total){
//                    progressBar.visibility = View.VISIBLE
//                    pagenum++
//                    getNews()
//                }
//
//                super.onScrolled(recyclerView, dx, dy)
//            }
//
//        })

        getJokes()
        tvJokes.setOnClickListener {
            getJokes()
        }
        getNews()
        setUpTabBar()
        bannerAd()
    }

    private fun getJokes() {
        val jokes = jokesService.api.getJokes()
        jokes.enqueue(object : Callback<ResponseJokes> {
            override fun onResponse(call: Call<ResponseJokes>, response: Response<ResponseJokes>) {
                val joke = response.body()
                val setup = joke!!.setup
                val delivery = joke.delivery
                tvJokes.text = "${setup}\n" + "$delivery"
                tvJokes.setMovementMethod(ScrollingMovementMethod())

            }

            override fun onFailure(call: Call<ResponseJokes>, t: Throwable) {
                tvJokes.setText("Error Loading Data")
            }

        })
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

    private fun getNews() {
        Log.d("mainActivity", "Requestsent for $pagenum")
        val news = newsService.apiService.getHeadlines()
        news.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                if (news != null) {
                    // Log.d("Loading" , news.toString() )
                    totalResultsNews = news.totalResults
                    articlesList.addAll(news.articles)
                    newsadapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("ErrorLoading", "Can't fetch news", t)
            }

        })
    }
    private fun bannerAd() {
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG , "Ad Loaded")
            }

            override fun onAdFailedToLoad(p0: LoadAdError?) {
                Log.d(TAG , "Ad Failed To Load")
            }

            override fun onAdOpened() {
                Log.d(TAG , "Ad Opened")
            }

            override fun onAdClicked() {
                Log.d(TAG , "Ad Clicked")
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }

            override fun onAdClosed() {
                Log.d(TAG , "Ad Closed")
            }
        }
    }
}