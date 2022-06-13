package com.tutorial.ohmygod.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.arch.paging3.BNLoadingAdapter
import com.tutorial.ohmygod.arch.paging3.BNPagingAdapter
import com.tutorial.ohmygod.databinding.FragmentBreakingNewsBinding
import com.tutorial.ohmygod.utils.NewsAdapter
import dagger.hilt.android.AndroidEntryPoint

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
 //region PAGING3

        binding.apply {
            breakingNewsRV.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
                header =BNLoadingAdapter{ pagingAdapter.retry() },
                footer =BNLoadingAdapter{ pagingAdapter.retry()}
            )
        }

        pagingAdapter.addLoadStateListener { loadstate->
            when(loadstate.source.refresh){
                is LoadState.Loading->{
                    hideError()
                    showLoadingState()
                }
                is LoadState.Error->{
                    hideLoadingState()
//                   val errorState = loadstate.source.append as? LoadState.Error ?:loadstate.source.prepend as? LoadState.Error
                   val errorState = loadstate.source.refresh as? LoadState.Error ?:loadstate.source.refresh as? LoadState.Error
                    showError("${errorState?.error}")
                    binding.retryLoadBtn.setOnClickListener{
                        pagingAdapter.retry()
                    }
                }
                is LoadState.NotLoading->{
                    hideError()
                    hideLoadingState()
                }
            }


        }

        viewModel.pagingNews.observe(viewLifecycleOwner) {
            pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        pagingAdapter.adapterClickListener {article->
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
        }
*/
        //endregion

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
        binding.retryLoadBtn.visibility = View.VISIBLE


    }

    private fun hideError() {
        binding.retryLoadBtn.visibility = View.GONE
        binding.errorImg.visibility = View.GONE
        binding.errorText.visibility = View.GONE
    }

}