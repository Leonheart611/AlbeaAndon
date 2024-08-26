package com.mika.enterprise.albeaandon.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateViewBinding(inflater, container)
        return binding.root
    }

    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}