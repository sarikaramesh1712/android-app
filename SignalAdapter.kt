package com.example.mobileproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.data.LocalSignal
import com.example.mobileproject.databinding.ItemSignalBinding

class SignalAdapter(
    private var signals: List<LocalSignal>,
    private val onItemClick: (LocalSignal) -> Unit
) : RecyclerView.Adapter<SignalAdapter.SignalViewHolder>() {

    inner class SignalViewHolder(val binding: ItemSignalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(signal: LocalSignal) {
            binding.textSignalInfo.text =
                "ID: ${signal.id}, X: ${signal.x}, Y: ${signal.y}, S1: ${signal.s1}, S2: ${signal.s2}, S3: ${signal.s3}"
            binding.root.setOnClickListener { onItemClick(signal) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalViewHolder {
        val binding = ItemSignalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SignalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SignalViewHolder, position: Int) {
        holder.bind(signals[position])
    }

    override fun getItemCount(): Int = signals.size

    fun updateSignals(newSignals: List<LocalSignal>) {
        signals = newSignals
        notifyDataSetChanged()
    }
}
