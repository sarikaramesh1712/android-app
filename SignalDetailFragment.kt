package com.example.mobileproject.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobileproject.data.AppDatabase
import com.example.mobileproject.data.LocalSignal
import com.example.mobileproject.data.RetrofitClient
import com.example.mobileproject.data.SignalRepository
import com.example.mobileproject.databinding.FragmentSignalDetailBinding
import com.example.mobileproject.ui.home.HomeViewModel
import com.example.mobileproject.ui.home.HomeViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class SignalDetailFragment : Fragment() {

    private var _binding: FragmentSignalDetailBinding? = null
    private val binding get() = _binding!!
    private val args: SignalDetailFragmentArgs by navArgs()

    private val viewModel: HomeViewModel by viewModels {
        val context = requireContext().applicationContext
        val db = AppDatabase.getInstance(context) // your Room database singleton
        val apiService = RetrofitClient.apiService      // your Retrofit API instance

        val repository = SignalRepository(
            apiService = apiService,
            signalDao = db.signalDao(),
            localSignalDao = db.localSignalDao(),
            context = context
        )

        HomeViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signalId = args.signalId

        // Hide delete button when adding
        binding.buttonDelete.visibility = if (signalId == -1) View.GONE else View.VISIBLE

        // If editing, load existing signal
        if (signalId != -1) {
            viewLifecycleOwner.lifecycleScope.launch {
                val existing = viewModel.getLocalSignalById(signalId)
                existing?.let { signal ->
                    binding.editX.setText(signal.x.toString())
                    binding.editY.setText(signal.y.toString())
                    binding.editS1.setText(signal.s1.toString())
                    binding.editS2.setText(signal.s2.toString())
                    binding.editS3.setText(signal.s3.toString())
                }
            }
        }

        // Save (add or update)
        binding.buttonSave.setOnClickListener {
            val s1 = binding.editS1.text.toString().toIntOrNull() ?: 0
            val s2 = binding.editS2.text.toString().toIntOrNull() ?: 0
            val s3 = binding.editS3.text.toString().toIntOrNull() ?: 0

            viewLifecycleOwner.lifecycleScope.launch {
                val (x, y) = viewModel.computeCoordinatesFromSignals(s1, s2, s3)
                    ?: (0 to 0) // fallback if no reference signals available

                if (signalId == -1) {
                    // 🟢 Add new signal
                    viewModel.addLocalSignal(x, y, s1, s2, s3)
                } else {
                    // 🟡 Update existing
                    val updated = LocalSignal(id = signalId, x = x, y = y, s1 = s1, s2 = s2, s3 = s3)
                    viewModel.updateLocalSignal(updated)
                }

                findNavController().navigateUp()
            }
        }


        // Delete button
        binding.buttonDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete signal")
                .setMessage("Are you sure you want to delete this signal?")
                .setPositiveButton("Delete") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.deleteLocalSignalById(signalId)
                        findNavController().navigateUp()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
