package com.tutorial.ohmygod.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.arch.paging3.BNLoadingAdapter
import com.tutorial.ohmygod.arch.paging3.BNPagingAdapter
import com.tutorial.ohmygod.databinding.FragmentSearchNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SearchNews : Fragment() {

    private val viewModel: NewsViewModel by activityViewModels()
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
        hideLoadingState()
        binding.emptyStateTv.isVisible = true
        binding.searchNewsRV.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
            header = BNLoadingAdapter { pagingAdapter.retry() },
            footer = BNLoadingAdapter { pagingAdapter.retry() }
        )

        viewModel.pagingSearchNews.observe(viewLifecycleOwner) {
            pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        lifecycleScope.launch {
            pagingAdapter.loadStateFlow
                .distinctUntilChangedBy {
                    it.refresh
                }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.searchNewsRV.scrollToPosition(0) }
        }

        pagingAdapter.addLoadStateListener { loadstate ->
            when (loadstate.source.refresh) {
                is LoadState.Loading -> {
                    binding.emptyStateTv.isVisible = false
                    showLoadingState()
                }
                is LoadState.Error -> {
                    binding.emptyStateTv.isVisible = true
                    hideLoadingState()
                }
                is LoadState.NotLoading -> {
                    binding.emptyStateTv.isVisible = true
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
                    return false
                }
            })

        }


    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingState() {
        binding.progressBar.visibility = View.GONE
    }


}
