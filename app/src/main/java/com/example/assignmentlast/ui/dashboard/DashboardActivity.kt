package com.example.assignmentlast.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentlast.data.models.Entity
import com.example.assignmentlast.databinding.ActivityDashboardBinding
import com.example.assignmentlast.ui.details.DetailsActivity
import com.example.assignmentlast.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: EntityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupLogoutButton()

        // Get keypass from intent
        val keypass = intent.getStringExtra("keypass") ?: ""
        if (keypass.isBlank()) {
            Toast.makeText(this, "Invalid keypass", Toast.LENGTH_SHORT).show()
            navigateToLogin()
            return
        }

        viewModel.fetchDashboard(keypass)
    }

    private fun setupRecyclerView() {
        adapter = EntityAdapter { entity ->
            val intent = Intent(this, DetailsActivity::class.java).apply {
                @Suppress("UNCHECKED_CAST")
                putExtra("entity", HashMap(entity) as HashMap<String, Any>)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.entities.observe(this) { result ->
            result.fold(
                onSuccess = { entities ->
                    adapter.submitList(entities)
                    binding.emptyView.visibility = if (entities.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility = if (entities.isEmpty()) View.GONE else View.VISIBLE
                },
                onFailure = { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}
