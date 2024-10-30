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
import com.mika.enterprise.albeaandon.MainActivity
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.BaseFragment
import com.mika.enterprise.albeaandon.core.model.response.TicketData
import com.mika.enterprise.albeaandon.core.util.Constant.ASSIGNED
import com.mika.enterprise.albeaandon.core.util.Constant.ESKALASI
import com.mika.enterprise.albeaandon.core.util.Constant.NEW
import com.mika.enterprise.albeaandon.core.util.Constant.ONPROG
import com.mika.enterprise.albeaandon.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), HomeItemAdapter.OnHomeItemClicked {

    private val viewModel: HomeViewModel by viewModels()
    private val adapter = HomeItemAdapter(this)
    private var filterAdapter = TicketFilterAdapter {
        viewModel.filteredStatus = it
        adapter.filter.filter(it)
    }

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
        viewModel.ticketResult.observe(viewLifecycleOwner) {
            if (viewModel.currentPage == 1) {
                adapter.submitList(it)
                filterAdapter.submit(viewModel.getFilterData())
            } else adapter.updateList(it)
        }
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showLoadingDialog() else hideLoadingDialog()
        }
        viewModel.username.observe(viewLifecycleOwner) {
            binding.tbHome.title = getString(R.string.home_title_label, it)
        }
        viewModel.isNotAuthorized.observe(viewLifecycleOwner) { if (it) showTokenExpiredDialog { logOut() } }
        viewModel.showEmptyState.observe(viewLifecycleOwner) {
            binding.emptyState.root.visibility = if (it) View.VISIBLE else View.GONE
            binding.rvTicketList.visibility = if (it) View.GONE else View.VISIBLE
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
        (requireActivity() as MainActivity).stopMqttService()
        viewModel.logout()
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
    }

    private fun setupAdapter() {
        binding.refreshTicket.setOnRefreshListener {
            viewModel.getTicketList()
            binding.refreshTicket.isRefreshing = false
        }
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


                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && viewModel.filteredStatus.isEmpty()) {
                    viewModel.loadNextPage()
                }
            }
        })
        binding.rvFilterTicket.adapter = filterAdapter
        binding.rvFilterTicket.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onClickListener(item: TicketData) {
        when (item.ticketStatus) {
            NEW -> {
                val action = HomeFragmentDirections.actionHomeFragmentToAssignFragment(item)
                findNavController().navigate(action)
            }

            ASSIGNED -> {
                val action = HomeFragmentDirections.actionHomeFragmentToProgressFragment(item)
                findNavController().navigate(action)
            }

            ONPROG, ESKALASI -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToFinalizeFragment(ticketData = item)
                findNavController().navigate(action)
            }

            else -> {
                showMessageDialog(
                    title = "Under Construction",
                    message = "This feature is under construction, lagi collect data yang di butuhin juga wkwkwk ^_^",
                    buttonText = "Ok, Sip"
                ) {}
            }
        }
    }

}