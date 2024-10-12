package com.mika.enterprise.albeaandon.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.BaseFragment
import com.mika.enterprise.albeaandon.core.model.response.TicketData
import com.mika.enterprise.albeaandon.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), HomeItemAdapter.OnHomeItemClicked {

    private val viewModel: HomeViewModel by viewModels()
    private val adapter = HomeItemAdapter(this)

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        viewModel.getUserName()
        viewModel.getTicketList()
        viewModel.ticketResult.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showLoadingDialog() else hideLoadingDialog()
        }
        viewModel.username.observe(viewLifecycleOwner) {
            binding.tbHome.title = getString(R.string.home_title_label, it)
        }
        viewModel.isNotAuthorized.observe(viewLifecycleOwner) {
            if (it)
                showMessageDialog(
                    title = getString(R.string.unauthorized_title),
                    message = getString(R.string.unauthorized_desc),
                    buttonText = getString(R.string.unauthorized_button_label)
                ) { logOut() }
        }
        binding.tbHome.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    logOut()
                    true
                }

                else -> false
            }
        }
    }

    private fun logOut() {
        viewModel.logout()
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
    }

    private fun setupAdapter() {
        binding.rvTicketList.adapter = adapter
        binding.rvTicketList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTicketList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                Log.d("HomeFragment", "onScrolled TotalItem:  $totalItemCount")


                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    override fun onClickListener(item: TicketData) {
        val action = HomeFragmentDirections.actionHomeFragmentToAssignFragment(item)
        findNavController().navigate(action)
    }

}