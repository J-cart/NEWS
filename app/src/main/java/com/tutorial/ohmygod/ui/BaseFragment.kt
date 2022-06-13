package com.tutorial.ohmygod.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tutorial.ohmygod.arch.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment:Fragment() {
    protected val viewModel:NewsViewModel by activityViewModels()
}