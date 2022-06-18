package com.tutorial.ohmygod.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.tutorial.ohmygod.R
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.databinding.FragmentArticleBinding
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.SavedArticle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.net.HttpURLConnection

@AndroidEntryPoint
class ArticleFragment : Fragment() {
    private val viewModel: NewsViewModel by activityViewModels()
    private val args: ArticleFragmentArgs by navArgs()

    lateinit var savedArticle: SavedArticle
    lateinit var article: Article

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener {
            binding.webView.reload()
            binding.swipeRefresh.isRefreshing = false
        }

        val chromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressBar.progress = newProgress
            }

        }
        val webClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.isVisible = false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progressBar.isVisible = true
            }


        }

        binding.webView.apply {
            webViewClient = webClient
            args.article.url?.let { loadUrl(it) }
            webChromeClient = chromeClient
            settings.javaScriptEnabled = true
            canGoBack()

            setOnKeyListener { view, i, keyEvent ->
                val webView = binding.webView
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.action == MotionEvent.ACTION_UP && webView.canGoBack()) {
                    webView.goBack()
                    return@setOnKeyListener true
                } else return@setOnKeyListener false
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.articlesEvent.collect { event ->
                when (event) {
                    is NewsViewModel.Events.Successful -> {
                        Snackbar.make(view, "Article saved Successfully", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is NewsViewModel.Events.Failure -> {
                        Snackbar.make(view, "ERROR! .. IT EXISTS ALREADY", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }

        binding.fab.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                val newArticle = args.article.let {
                    SavedArticle(
                        id = it.id,
                        author = it.author,
                        content = it.author,
                        description = it.description,
                        publishedAt = it.publishedAt,
                        source = it.source,
                        title = it.title,
                        url = it.url,
                        urlToImage = it.urlToImage
                    )
                }
                viewModel.checkExisting(newArticle)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.article_frag_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refreshPage -> {
                binding.webView.reload()
                true
            }
            R.id.openOutside -> {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(args.article.url)
                    startActivity(this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}