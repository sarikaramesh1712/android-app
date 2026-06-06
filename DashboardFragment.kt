package com.example.mobileproject.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobileproject.R
import com.example.mobileproject.adapter.SignalGridAdapter
import com.example.mobileproject.data.AppContainer
import com.example.mobileproject.data.SignalViewModelFactory

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SignalViewModel
    private lateinit var adapter: SignalGridAdapter

    private lateinit var errorView: View
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signal_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupAdapter()
        setupViewModel()
        setupObservers()

        println("DashboardFragment: View created, data fetch will auto-start if needed")
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.signalGridRecyclerView)
        errorView = view.findViewById(R.id.errorView)
        errorText = view.findViewById(R.id.errorText)
        retryButton = view.findViewById(R.id.retryButton)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        // Ensure RecyclerView starts hidden
        recyclerView.visibility = View.GONE
        errorView.visibility = View.GONE

        retryButton.setOnClickListener {
            viewModel.clearError()
            viewModel.fetchSignals()
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.forceRefresh()
        }
    }

    private fun setupAdapter() {
        adapter = SignalGridAdapter()

        adapter.onGridDimensionsChanged = { width, height ->
            updateGridLayout(width)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
        recyclerView.adapter = adapter

        recyclerView.isHorizontalScrollBarEnabled = true
        recyclerView.isVerticalScrollBarEnabled = true
        recyclerView.setPadding(8, 8, 8, 8)

        println("DashboardFragment: Adapter setup complete")
    }

    private fun setupViewModel() {
        val repository = AppContainer.getRepository()
        viewModel = ViewModelProvider(
            this,
            SignalViewModelFactory(repository)
        ).get(SignalViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading

            if (isLoading) {
                // Hide everything except loading indicator during loading
                recyclerView.visibility = View.GONE
                errorView.visibility = View.GONE
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            println("DashboardFragment: Data state changed to: $state")
            when (state) {
                SignalViewModel.DataState.CHECKING -> {
                    swipeRefreshLayout.isRefreshing = true
                    recyclerView.visibility = View.GONE
                    errorView.visibility = View.GONE
                }
                SignalViewModel.DataState.FETCHING -> {
                    swipeRefreshLayout.isRefreshing = true
                    recyclerView.visibility = View.GONE
                    errorView.visibility = View.GONE
                }
                SignalViewModel.DataState.AVAILABLE -> {
                    swipeRefreshLayout.isRefreshing = false
                    recyclerView.visibility = View.VISIBLE
                    errorView.visibility = View.GONE
                }
                SignalViewModel.DataState.ERROR -> {
                    swipeRefreshLayout.isRefreshing = false
                    recyclerView.visibility = View.GONE
                    errorView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                errorText.text = error
            }
        }

        viewModel.allSignals.observe(viewLifecycleOwner) { signals ->
            if (signals.isNotEmpty()) {
                adapter.updateSignals(signals)
                updateGridLayout()
                println("DashboardFragment: Displaying ${signals.size} signals")

                // Only make RecyclerView visible when we have actual data
                if (viewModel.dataState.value == SignalViewModel.DataState.AVAILABLE) {
                    recyclerView.visibility = View.VISIBLE
                }
            } else {
                println("DashboardFragment: No signals to display")
                // Keep RecyclerView hidden if no signals
                recyclerView.visibility = View.GONE
            }
        }
    }

    private fun updateGridLayout() {
        val spanCount = try {
            adapter.getGridWidth()
        } catch (e: Exception) {
            println("getGridWidth not available, using default span count")
            10
        }

        recyclerView.post {
            val layoutManager = GridLayoutManager(requireContext(), spanCount)
            recyclerView.layoutManager = layoutManager
        }
    }

    private fun updateGridLayout(spanCount: Int) {
        recyclerView.post {
            val layoutManager = GridLayoutManager(requireContext(), spanCount)
            recyclerView.layoutManager = layoutManager
        }
    }
}