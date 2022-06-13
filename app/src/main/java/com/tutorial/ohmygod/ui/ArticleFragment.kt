package com.tutorial.ohmygod.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.databinding.FragmentArticleBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ArticleFragment : Fragment() {
    private val viewModel:NewsViewModel by activityViewModels()
    private val args:ArticleFragmentArgs by navArgs()

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.apply {
            webViewClient = WebViewClient()
            args.article.url?.let { loadUrl(it) }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsChannel.collect { event->
                when(event){
                    is NewsViewModel.Events.Successful->{
                        Snackbar.make(view,"Article saved Successfully",Snackbar.LENGTH_SHORT).show()
                    }
                    is NewsViewModel.Events.Failure->{
                        Snackbar.make(view,"ERROR , IT EXISTS ALREADY",Snackbar.LENGTH_SHORT).show()
                    }
                }

            }
        }

        binding.fab.setOnClickListener {
            /*viewModel.insertArticle(article =args.article )
            Snackbar.make(view,"Article saved Successfully",Snackbar.LENGTH_SHORT).show()*/

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.checkExisting(args.article)
            }

        }
    }

}