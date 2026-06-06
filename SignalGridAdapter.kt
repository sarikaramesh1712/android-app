package com.example.mobileproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.R
import com.example.mobileproject.data.SignalIni

class SignalGridAdapter : RecyclerView.Adapter<SignalGridAdapter.GridViewHolder>() {

    companion object {
        private const val TYPE_CORNER = 0
        private const val TYPE_ROW_LABEL = 1
        private const val TYPE_COLUMN_LABEL = 2
        private const val TYPE_CELL = 3
    }

    private var signals: List<SignalIni> = emptyList()

    // Grid dimensions with safe defaults
    private var displayMinX = -1
    private var displayMaxX = 1
    private var displayMinY = -1
    private var displayMaxY = 1
    private var gridWidth = 3
    private var gridHeight = 3
    private var totalWidth = 4
    private var totalHeight = 4

    private val signalMap = mutableMapOf<Pair<Int, Int>, SignalIni>()

    var onGridDimensionsChanged: ((width: Int, height: Int) -> Unit)? = null

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cellText: TextView = itemView.findViewById(R.id.cell_text)
    }

    private fun calculateGridDimensions(signals: List<SignalIni>) {
        println("Adapter: Calculating grid dimensions for ${signals.size} signals")

        if (signals.isEmpty()) {
            println("Adapter: No signals, using default grid")
            // Use default small grid
            displayMinX = -1
            displayMaxX = 1
            displayMinY = -1
            displayMaxY = 1
        } else {
            try {
                val xValues = signals.map { it.x }
                val yValues = signals.map { it.y }

                val minX = xValues.minOrNull() ?: 0
                val maxX = xValues.maxOrNull() ?: 0
                val minY = yValues.minOrNull() ?: 0
                val maxY = yValues.maxOrNull() ?: 0

                println("Adapter: Raw ranges - X: $minX to $maxX, Y: $minY to $maxY")

                // Add padding around the data
                displayMinX = minX - 1
                displayMaxX = maxX + 1
                displayMinY = minY - 1
                displayMaxY = maxY + 1

                println("Adapter: Padded ranges - X: $displayMinX to $displayMaxX, Y: $displayMinY to $displayMaxY")

            } catch (e: Exception) {
                println("Adapter: Error calculating dimensions: ${e.message}")
                // Fall back to defaults
                displayMinX = -1
                displayMaxX = 1
                displayMinY = -1
                displayMaxY = 1
            }
        }

        // Calculate grid dimensions
        gridWidth = (displayMaxX - displayMinX + 1).coerceAtLeast(1)
        gridHeight = (displayMaxY - displayMinY + 1).coerceAtLeast(1)
        totalWidth = gridWidth + 1
        totalHeight = gridHeight + 1

        println("Adapter: Final grid - Data: ${gridWidth}x$gridHeight, Total: ${totalWidth}x$totalHeight")

        // Populate signal map
        signalMap.clear()
        signals.forEach { signal ->
            signalMap[signal.x to signal.y] = signal
        }

        println("Adapter: Signal map populated with ${signalMap.size} entries")

        // Notify about dimension changes
        onGridDimensionsChanged?.invoke(totalWidth, totalHeight)
    }

    override fun getItemViewType(position: Int): Int {
        if (position >= totalWidth * totalHeight) {
            return TYPE_CELL // Safety fallback
        }

        val row = position / totalWidth
        val col = position % totalWidth

        return when {
            row == 0 && col == 0 -> TYPE_CORNER
            row == 0 -> TYPE_COLUMN_LABEL
            col == 0 -> TYPE_ROW_LABEL
            else -> TYPE_CELL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val layoutRes = when (viewType) {
            TYPE_CORNER -> R.layout.grid_corner_item
            TYPE_ROW_LABEL, TYPE_COLUMN_LABEL -> R.layout.grid_label_item
            else -> R.layout.grid_cell_item
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        try {
            if (position >= totalWidth * totalHeight) {
                println("Adapter: Position $position out of bounds")
                return
            }

            val row = position / totalWidth
            val col = position % totalWidth

            when (getItemViewType(position)) {
                TYPE_CORNER -> {
                    holder.cellText.text = ""
                    holder.cellText.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
                }
                TYPE_ROW_LABEL -> {
                    if (row > 0) {
                        val y = displayMaxY - (row - 1)
                        holder.cellText.text = y.toString()
                        holder.cellText.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
                        holder.cellText.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
                    }
                }
                TYPE_COLUMN_LABEL -> {
                    if (col > 0) {
                        val x = displayMinX + (col - 1)
                        holder.cellText.text = x.toString()
                        holder.cellText.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
                        holder.cellText.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
                    }
                }
                TYPE_CELL -> {
                    if (row > 0 && col > 0) {
                        val x = displayMinX + (col - 1)
                        val y = displayMaxY - (row - 1)

                        val signal = signalMap[x to y]
                        val hasSignal = signal != null
                        holder.cellText.text = if (hasSignal) "1" else "0"

                        if (hasSignal) {
                            holder.cellText.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark))
                            holder.cellText.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
                        } else {
                            holder.cellText.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.transparent))
                            holder.cellText.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Adapter: Error binding view at position $position: ${e.message}")
        }
    }

    override fun getItemCount(): Int = totalWidth * totalHeight

    fun updateSignals(newSignals: List<SignalIni>) {
        println("Adapter: updateSignals called with ${newSignals.size} signals")
        this.signals = newSignals
        calculateGridDimensions(newSignals)
        notifyDataSetChanged()
        println("Adapter: notifyDataSetChanged completed")
    }
    fun getGridWidth(): Int {
        return totalWidth
    }
    fun getGridHeight(): Int {
        return totalHeight
    }
}