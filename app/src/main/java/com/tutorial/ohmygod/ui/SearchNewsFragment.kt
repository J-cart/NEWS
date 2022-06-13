package com.tutorial.ohmygod.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.arch.paging3.BNLoadingAdapter
import com.tutorial.ohmygod.arch.paging3.BNPagingAdapter
import com.tutorial.ohmygod.databinding.FragmentSearchNewsBinding
import com.tutorial.ohmygod.utils.NewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNews : Fragment() {

    private val viewModel: NewsViewModel by activityViewModels()
    private val searchNewsAdapter: NewsAdapter by lazy { NewsAdapter() }
    var job: Job? = null
    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private val pagingAdapter: BNPagingAdapter by lazy { BNPagingAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideError()
        hideLoadingState()
        binding.emptyStateTv.isVisible = true
//        binding.searchView.isActivated = false
        binding.searchNewsRV.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
            header = BNLoadingAdapter { pagingAdapter.retry() },
            footer = BNLoadingAdapter { pagingAdapter.retry() }
        )

        viewModel.pagingSearchNews.observe(viewLifecycleOwner) {
            pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        pagingAdapter.addLoadStateListener { loadstate ->
            when (loadstate.source.refresh) {
                is LoadState.Loading -> {
                    binding.emptyStateTv.isVisible = false
                    hideError()
                    showLoadingState()
                }
                is LoadState.Error -> {
                    binding.emptyStateTv.isVisible = false
                    hideLoadingState()
                    val errorState = loadstate.source.refresh as? LoadState.Error
                        ?: loadstate.source.refresh as? LoadState.Error
                    showError("${errorState?.error}")
                    binding.retryLoadBtn.setOnClickListener {
                        pagingAdapter.retry()
                    }
                }
                is LoadState.NotLoading -> {
                    binding.emptyStateTv.isVisible = true
                    hideError()
                    hideLoadingState()
                }
            }

            pagingAdapter.adapterClickListener { article ->
                val navigate = SearchNewsDirections.actionSearchNewsToArticleFragment(article)
                findNavController().navigate(navigate)
            }

            binding.searchView.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null)
                        viewModel.querySearch(query)
                    binding.searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    binding.searchView.isActivated = true
                    job?.cancel()
                    job = MainScope().launch {
                        delay(2000L)
                        newText?.let { viewModel.querySearch(it) }
                    }
                    //newText.let { viewModel.searchNews(it) }
                    return true
                }
            })


            //region NORMAL REQUEST
            /*   binding.apply {
                   searchNewsRV.adapter = searchNewsAdapter
               }

               binding.searchView.isActivated = false
               viewModel.searchNews.observe(viewLifecycleOwner) { resource ->
                   when (resource) {
                       is Resource.Loading -> {
                           binding.emptyStateTv.isVisible = false
                           hideError()
                           showLoadingState()
                       }
                       is Resource.Successful -> {
                           binding.emptyStateTv.isVisible = false
                           hideError()
                           hideLoadingState()
                           searchNewsAdapter.submitList(resource.data?.articles)
                       }
                       is Resource.Failure -> {
                           binding.emptyStateTv.isVisible = false
                           hideLoadingState()
                           resource.msg?.let {
                               showError(it)
                           }
                       }
                       is Resource.Empty->{
                           binding.emptyStateTv.isVisible = true
                           hideLoadingState()
                           hideError()
                       }
                   }

               }
               searchNewsAdapter.adapterClickListener {
                   val navigate = SearchNewsDirections.actionSearchNewsToArticleFragment(it)
                   findNavController().navigate(navigate)
               }

               binding.searchView.setOnQueryTextListener(object :
                   androidx.appcompat.widget.SearchView.OnQueryTextListener {
                   override fun onQueryTextSubmit(query: String?): Boolean {
                       return false
                   }

                   override fun onQueryTextChange(newText: String?): Boolean {
                       binding.searchView.isActivated = true
                       job?.cancel()
                       job = MainScope().launch {
                           delay(1000L)
                           if (newText!!.isNotBlank()) {
                               newText.let { viewModel.handleSearch(it) }
                           }
                       }
                       return true
                   }
               })*/
            //endregion
        }


    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingState() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(errorText: String) {
        binding.errorImg.visibility = View.VISIBLE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = errorText


    }

    private fun hideError() {
        binding.errorImg.visibility = View.GONE
        binding.errorText.visibility = View.GONE
    }

}
