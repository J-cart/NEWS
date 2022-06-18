package com.tutorial.ohmygod.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.arch.paging3.BNLoadingAdapter
import com.tutorial.ohmygod.arch.paging3.BNPagingAdapter
import com.tutorial.ohmygod.databinding.FragmentBreakingNewsBinding
import com.tutorial.ohmygod.utils.NewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BreakingNews : Fragment() {

    private val viewModel: NewsViewModel by activityViewModels()
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter() }
    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!


    private val pagingAdapter: BNPagingAdapter by lazy { BNPagingAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeToRefresh.setOnRefreshListener{
            pagingAdapter.retry()
            binding.swipeToRefresh.isRefreshing = false
        }

        //region PAGING3
        binding.apply {
            breakingNewsRV.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
                header = BNLoadingAdapter { pagingAdapter.retry() },
                footer = BNLoadingAdapter { pagingAdapter.retry() }
            )
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.breakingNewsEvent.collect { event->
                when(event){
                    is NewsViewModel.Events.Successful->{
                        hideLoadingState()
                    }
                    is NewsViewModel.Events.Failure->{
                        showLoadingState()

                    }
                }

            }
        }

        lifecycleScope.launch {
            pagingAdapter.loadStateFlow
                .distinctUntilChangedBy {
                    it.mediator?.refresh
                }
                .filter { it.mediator?.refresh is LoadState.NotLoading}
                .collect { binding.breakingNewsRV.scrollToPosition(0) }
        }

        pagingAdapter.addLoadStateListener { loadstate ->
            when (loadstate.mediator?.refresh) {
                is LoadState.Loading -> {
                    hideError()
                    viewModel.checkSizeFromDB()
                }
                is LoadState.Error -> {
                    viewModel.checkSizeFromDB()
                    Snackbar.make(requireView(), "ERROR! Try refreshing news", Snackbar.LENGTH_LONG)
                        .setAction("Refresh") { pagingAdapter.retry() }.show()
                }
                is LoadState.NotLoading -> {
                    hideError()
                    hideLoadingState()
                }
            }

        }

        viewModel.mediatorPagingNews.observe(viewLifecycleOwner) {
            pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        pagingAdapter.adapterClickListener { article ->
            val navigate = BreakingNewsDirections.actionBreakingNewsToArticleFragment(article)
            findNavController().navigate(navigate)
        }
        //endregion


//region NORMAL REQUEST
        /*  binding.apply {
              breakingNewsRV.adapter = newsAdapter
          }

          viewModel.breakingNews.observe(viewLifecycleOwner){resource->
              when(resource){
                  is Resource.Loading ->{
                      hideError()
                      showLoadingState()
                  }
                  is Resource.Successful->{
                      hideError()
                      hideLoadingState()
                      newsAdapter.submitList(resource.data?.articles)
                  }
                  is Resource.Failure->{
                      hideLoadingState()
                      resource.msg?.let {
                          showError(it)
                      }
                  }
              }

          }

          newsAdapter.adapterClickListener { article->
              val navigate = BreakingNewsDirections.actionBreakingNewsToArticleFragment(article)
              findNavController().navigate(navigate)
          }*/
        //endregion

    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingState() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(errorText: String) {
    }

    private fun hideError() {

    }

}