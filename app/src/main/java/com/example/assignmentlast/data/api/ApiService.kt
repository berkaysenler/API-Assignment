// app/src/main/java/com/example/assignmentlast/data/api/ApiService.kt
package com.example.assignmentlast.data.api

import com.example.assignmentlast.data.models.DashboardResponse
import com.example.assignmentlast.data.models.LoginRequest
import com.example.assignmentlast.data.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("{location}/auth")
    suspend fun login(
        @Path("location") location: String,
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("dashboard/{keypass}")
    suspend fun getDashboard(
        @Path("keypass") keypass: String
    ): Response<DashboardResponse>
}