package com.example.assignmentlast.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmentlast.data.models.Entity
import com.example.assignmentlast.databinding.ItemEntityBinding

class EntityAdapter(private val onItemClick: (Entity) -> Unit) :
    ListAdapter<Entity, EntityAdapter.EntityViewHolder>(EntityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val binding = ItemEntityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EntityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EntityViewHolder(private val binding: ItemEntityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(entity: Map<String, Any>) {
            // Determine primary field (name, species, title, etc.)
            val primaryField = findPrimaryField(entity)
            binding.primaryText.text = entity[primaryField]?.toString() ?: "Unknown"

            // Determine secondary field (scientific name, author, etc.)
            val secondaryField = findSecondaryField(entity, primaryField)
            if (secondaryField != null && entity.containsKey(secondaryField)) {
                binding.secondaryText.text = entity[secondaryField].toString()
                binding.secondaryText.visibility = ViewGroup.VISIBLE
            } else {
                binding.secondaryText.visibility = ViewGroup.GONE
            }

            // Determine status field (care level, conservation status, etc.)
            val statusField = findStatusField(entity)
            if (statusField != null && entity.containsKey(statusField)) {
                val statusValue = entity[statusField].toString()
                binding.statusBadge.text = statusValue
                binding.statusBadge.visibility = ViewGroup.VISIBLE

                // Set badge color based on status value
                val (bgColor, textColor) = getStatusColors(statusField, statusValue)
                binding.statusBadge.setBackgroundColor(Color.parseColor(bgColor))
                binding.statusBadge.setTextColor(Color.parseColor(textColor))
            } else {
                binding.statusBadge.visibility = ViewGroup.GONE
            }
        }

        private fun findPrimaryField(entity: Map<String, Any>): String {
            // Priority order for primary field
            val primaryFieldOptions = listOf(
                "commonName", "species", "name", "title", "dishName"
            )

            for (field in primaryFieldOptions) {
                if (entity.containsKey(field)) {
                    return field
                }
            }

            // If none of the preferred fields exist, use the first field that's not scientific name or description
            return entity.keys.firstOrNull {
                !it.contains("scientific", ignoreCase = true) &&
                        !it.contains("description", ignoreCase = true) &&
                        !it.contains("level", ignoreCase = true) &&
                        !it.contains("status", ignoreCase = true)
            } ?: entity.keys.first()
        }

        private fun findSecondaryField(entity: Map<String, Any>, primaryField: String): String? {
            // Priority order for secondary field
            val secondaryFieldOptions = listOf(
                "scientificName", "author", "subtitle", "origin"
            )

            for (field in secondaryFieldOptions) {
                if (entity.containsKey(field) && field != primaryField) {
                    return field
                }
            }

            // If no specific secondary field, find something appropriate
            return entity.keys.firstOrNull {
                it != primaryField &&
                        !it.contains("description", ignoreCase = true) &&
                        !it.contains("level", ignoreCase = true) &&
                        !it.contains("status", ignoreCase = true)
            }
        }

        private fun findStatusField(entity: Map<String, Any>): String? {
            // Priority order for status field
            val statusFieldOptions = listOf(
                "careLevel", "conservationStatus", "difficulty", "status", "priority"
            )

            for (field in statusFieldOptions) {
                if (entity.containsKey(field)) {
                    return field
                }
            }

            // If no specific status field, look for fields containing "level" or "status"
            return entity.keys.firstOrNull {
                it.contains("level", ignoreCase = true) ||
                        it.contains("status", ignoreCase = true) ||
                        it.contains("difficulty", ignoreCase = true)
            }
        }

        private fun getStatusColors(statusField: String, statusValue: String): Pair<String, String> {
            // Default colors
            var bgColor = "#E3F2FD" // Light blue
            var textColor = "#1565C0" // Dark blue

            // Determine colors based on status value
            when (statusValue.lowercase()) {
                "easy", "low", "good", "stable", "least concern" -> {
                    bgColor = "#E8F5E9" // Light green
                    textColor = "#2E7D32" // Dark green
                }
                "moderate", "medium", "fair", "near threatened" -> {
                    bgColor = "#FFF8E1" // Light amber
                    textColor = "#F57F17" // Dark amber
                }
                "hard", "difficult", "high", "poor", "endangered", "vulnerable", "critical" -> {
                    bgColor = "#FFEBEE" // Light red
                    textColor = "#C62828" // Dark red
                }
            }

            return Pair(bgColor, textColor)
        }
    }

    class EntityDiffCallback : DiffUtil.ItemCallback<Entity>() {
        override fun areItemsTheSame(oldItem: Entity, newItem: Entity): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Entity, newItem: Entity): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }
}
