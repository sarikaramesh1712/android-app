package com.example.mobileproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileproject.adapter.SignalAdapter
import com.example.mobileproject.data.AppContainer
import com.example.mobileproject.data.SignalRepository
import com.example.mobileproject.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController
import com.example.mobileproject.data.AppDatabase
import com.example.mobileproject.data.RetrofitClient
import com.example.mobileproject.data.SignalViewModelFactory
import com.example.mobileproject.ui.dashboard.SignalViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SignalAdapter
    private val viewModel: HomeViewModel by viewModels {
        val context = requireContext().applicationContext
        val db = AppDatabase.getInstance(context)
        val apiService = RetrofitClient.apiService

        val repository = SignalRepository(
            apiService = apiService,
            signalDao = db.signalDao(),
            localSignalDao = db.localSignalDao(),
            context = context
        )

        HomeViewModelFactory(repository)
    }

    private lateinit var mapViewModel: SignalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ensureAppContainerInitialized()) {
            setupViewModel()
            setupObservers()
        }

        adapter = SignalAdapter(emptyList()) { signal ->
            // Navigate to edit existing signal
            val action = HomeFragmentDirections
                .actionHomeFragmentToSignalDetailFragment(signal.id)
            findNavController().navigate(action)
        }

        binding.recyclerSignals.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSignals.adapter = adapter

        viewModel.allLocalSignals.observe(viewLifecycleOwner) { signals ->
            adapter.updateSignals(signals)
            binding.textSignalCount.text = "Signals: ${signals.size}"
        }

        // Add new signal
        binding.fabAddSignal.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToSignalDetailFragment(-1)
            findNavController().navigate(action)
        }
    }

    private fun ensureAppContainerInitialized(): Boolean {
        return try {
            AppContainer.getRepository()
            true
        } catch (e: IllegalStateException) {
            println("AnalysisFragment: AppContainer not ready, retrying...")
            // Retry after a short delay
            view?.postDelayed({
                if (isAdded) { // Check if fragment is still attached
                    setupViewModel()
                    setupObservers()
                }
            }, 100)
            false
        }
    }

    private fun setupViewModel() {
        val repository = AppContainer.getRepository()
        val factory = SignalViewModelFactory(repository)
        mapViewModel = ViewModelProvider(requireActivity(), factory).get(SignalViewModel::class.java)
    }

    private fun setupObservers() {
        mapViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerSignals.visibility = View.GONE
                binding.fabAddSignal.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.recyclerSignals.visibility = View.VISIBLE
                binding.fabAddSignal.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}