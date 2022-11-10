package com.tutorial.ohmygod.arch.paging3

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tutorial.ohmygod.R
import com.tutorial.ohmygod.databinding.NewsViewHolderBinding
import com.tutorial.ohmygod.db.Article
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
@RequiresApi(Build.VERSION_CODES.O)
class BNPagingAdapter : PagingDataAdapter<Article, BNPagingAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = NewsViewHolderBinding.bind(view)

        fun bind(article: Article) {
            binding.apply {
                imgUrl.load(article.urlToImage) {
                    placeholder(R.drawable.ic_baseline_image_24)
                    error(R.drawable.ic_baseline_broken_image_24)
                }
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt?.let { getDateFormat(it) }
                tvSourceName.text = article.source?.name
            }
            binding.root.setOnClickListener {
                listener?.let {
                    it(article)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos != null) {
            holder.bind(pos)
        }
    }


    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }
        }
    }

    private var listener: ((Article) -> Unit)? = null

    fun adapterClickListener(listener: (Article) -> Unit) {
        this.listener = listener
    }

    private  fun getDateFormat(date: String):String{
        val format =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val localDate = LocalDateTime.parse(date, format)
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy 'at' hh:mm a", Locale.getDefault())
        return localDate.format(dateFormatter)
    }


}