package com.tutorial.ohmygod.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.tutorial.ohmygod.R
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.databinding.FragmentSavedNewsWebViewBinding
import com.tutorial.ohmygod.db.JsonResponse


class SavedNewsWebView : Fragment() {

    private val viewModel: NewsViewModel by activityViewModels()
    private val args: SavedNewsWebViewArgs by navArgs()


    private var _binding: FragmentSavedNewsWebViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentSavedNewsWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener{
            binding.webView.reload()
            binding.swipeRefresh.isRefreshing = false
        }

        val chromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressBar.progress = newProgress
            }
        }
        val webClient = object :WebViewClient(){
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
            args.savedArticle.url?.let { loadUrl(it) }
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
                    data = Uri.parse(args.savedArticle.url)
                    startActivity(this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }
}