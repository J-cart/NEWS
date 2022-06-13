package com.tutorial.ohmygod.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.databinding.FragmentSavedNewsWebViewBinding


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
        _binding = FragmentSavedNewsWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.apply {
            webViewClient = WebViewClient()
            args.savedArticle.url?.let { loadUrl(it) }
        }

    }
}