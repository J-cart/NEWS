package com.tutorial.ohmygod.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.arch.paging3.BNLoadingAdapter
import com.tutorial.ohmygod.arch.paging3.BNPagingAdapter
import com.tutorial.ohmygod.databinding.FragmentBreakingNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class BreakingNews : Fragment() {

    private val viewModel: NewsViewModel by activityViewModels()
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
            breakingNewsRV.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
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
                    viewModel.checkSizeFromDB()
                }
                is LoadState.Error -> {
                    viewModel.checkSizeFromDB()
                    Snackbar.make(requireView(), "ERROR! Try refreshing news", Snackbar.LENGTH_LONG)
                        .setAction("Refresh") { pagingAdapter.retry() }.show()
                }
                is LoadState.NotLoading -> {
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

    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingState() {
        binding.progressBar.visibility = View.GONE
    }

}