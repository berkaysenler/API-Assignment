// app/src/main/java/com/example/assignmentlast/data/repository/AppRepository.kt
package com.example.assignmentlast.data.repository

import com.example.assignmentlast.data.api.ApiService
import com.example.assignmentlast.data.models.DashboardResponse
import com.example.assignmentlast.data.models.Entity
import com.example.assignmentlast.data.models.LoginRequest
import com.example.assignmentlast.data.models.LoginResponse
import retrofit2.Response
import javax.inject.Inject

interface AppRepository {
    suspend fun login(username: String, password: String, location: String): Result<String>
    suspend fun getDashboard(keypass: String): Result<List<Entity>>
}

class AppRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AppRepository {

    override suspend fun login(username: String, password: String, location: String): Result<String> {
        return try {
            val response = apiService.login(location, LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.keypass)
            } else {
                Result.failure(Exception("Login failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDashboard(keypass: String): Result<List<Entity>> {
        return try {
            val response = apiService.getDashboard(keypass)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.entities)
            } else {
                Result.failure(Exception("Failed to get dashboard: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}