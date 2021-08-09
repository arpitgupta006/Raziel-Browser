package com.arpit.newsapp3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arpit.antinotasknewsapp.Article
import com.arpit.razielbrowser.R
import com.bumptech.glide.Glide

class MyAdapter( val context: Context , val articles: List<Article> ) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView){
        var tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        var tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)
        var ivNewsImage = itemView.findViewById<ImageView>(R.id.ivNewsImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent , false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.tvTitle.text = articles[position].title
        holder.tvDescription.text = articles[position].description

        Glide.with(context).load(articles[position].urlToImage).into(holder.ivNewsImage)

    }

    override fun getItemCount() = articles.size
}

