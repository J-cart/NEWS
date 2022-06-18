package com.tutorial.ohmygod.ui

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tutorial.ohmygod.R
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.databinding.FragmentSavedNewsBinding
import com.tutorial.ohmygod.db.SavedArticle
import com.tutorial.ohmygod.utils.LocalNewsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNews : Fragment() {

    private val viewModel: NewsViewModel by activityViewModels()
    private val savedNewsAdapter: LocalNewsAdapter by lazy { LocalNewsAdapter() }

    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            savedNewsRV.adapter = savedNewsAdapter
        }
        viewModel.allSavedNews.observe(viewLifecycleOwner) { list ->
            when {
                list.isNotEmpty() -> {

                    savedNewsAdapter.submitList(list)
                    binding.progressBar.isVisible = false
                    binding.emptyStateTv.isVisible = false

                }
                else -> {
                    binding.emptyStateTv.isVisible = true
//                    binding.savedNewsRV.isVisible = false
                }
            }
        }

        savedNewsAdapter.adapterClickListener {
            val navigate = SavedNewsDirections.actionSavedNewsToSavedNewsWebView(it)
            findNavController().navigate(navigate)
        }


        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.layoutPosition
                val article = savedNewsAdapter.currentList[position]
                viewModel.deleteSavedArticle(article)
                Snackbar.make(view, "Article deleted", Snackbar.LENGTH_SHORT).apply {
                    setAction("UNDO delete") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.savedNewsRV)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.saved_news_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteAll -> {
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage("Are you sure you want to delete all saved news?")
            setPositiveButton("YES") { d, i ->
                viewModel.deleteAllSavedNews()
                savedNewsAdapter.submitList(emptyList())
                savedNewsAdapter.notifyDataSetChanged()
            }
            setNegativeButton("NO") { d, i ->
                d.dismiss()
            }
            show()
        }

    }
}