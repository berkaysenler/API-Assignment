package com.example.assignmentlast.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmentlast.R
import com.example.assignmentlast.databinding.ActivityLoginBinding
import com.example.assignmentlast.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLocationSpinner()
        setupObservers()
        setupClickListeners()
    }

    private fun setupLocationSpinner() {
        // Create an ArrayAdapter using a simple spinner layout and locations array
        val locations = arrayOf("Sydney", "Footscray", "ORT")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        binding.locationSpinner.adapter = adapter

        // Set default selection to Sydney
        binding.locationSpinner.setSelection(0)
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { keypass ->
                    // Navigate to Dashboard with keypass
                    val intent = Intent(this, DashboardActivity::class.java).apply {
                        putExtra("keypass", keypass)
                    }
                    startActivity(intent)
                },
                onFailure = { error ->
                    // Show error message
                    Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loginButton.isEnabled = !isLoading
        }
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get selected location from spinner
            val location = binding.locationSpinner.selectedItem.toString().lowercase()

            viewModel.login(username, password, location)
        }
    }
}
