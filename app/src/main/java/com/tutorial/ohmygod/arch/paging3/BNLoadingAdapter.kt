package com.tutorial.ohmygod.arch.paging3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tutorial.ohmygod.R
import com.tutorial.ohmygod.databinding.NewsLoadStateBinding

class BNLoadingAdapter (private val onRetryClicked:()->Unit): LoadStateAdapter<BNLoadingAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = NewsLoadStateBinding.bind(view)
        init {
           binding.retryBtn.setOnClickListener{
                onRetryClicked.invoke()
            }
        }
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorText.text = loadState.error.localizedMessage
            }

            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                errorText.isVisible = loadState is LoadState.Error
                retryBtn.isVisible = loadState is LoadState.Error

            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_load_state, parent, false)
        return ViewHolder(view)
    }

}