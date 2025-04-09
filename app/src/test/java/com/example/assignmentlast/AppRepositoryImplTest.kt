package com.example.assignmentlast

import com.example.assignmentlast.data.api.ApiService
import com.example.assignmentlast.data.models.DashboardResponse
import com.example.assignmentlast.data.models.Entity
import com.example.assignmentlast.data.models.LoginRequest
import com.example.assignmentlast.data.models.LoginResponse
import com.example.assignmentlast.data.repository.AppRepositoryImpl
import okhttp3.ResponseBody
import retrofit2.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AppRepositoryImplTest {

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var repository: AppRepositoryImpl

    @Before
    fun setup() {
        repository = AppRepositoryImpl(apiService)
    }

    @Test
    fun `login success should return keypass`() = runTest {
        // Given
        val loginRequest = LoginRequest("username", "password")
        val loginResponse = LoginResponse("test-keypass")
        `when`(apiService.login("location", loginRequest))
            .thenReturn(Response.success(loginResponse))

        // When
        val result = repository.login("username", "password", "location")

        // Then
        assert(result.isSuccess)
        assert(result.getOrNull() == "test-keypass")
    }

    @Test
    fun `login failure should return error`() = runTest {
        // Given
        val loginRequest = LoginRequest("username", "password")
        `when`(apiService.login("location", loginRequest))
            .thenReturn(Response.error(401, ResponseBody.create(null, "Unauthorized")))

        // When
        val result = repository.login("username", "password", "location")

        // Then
        assert(result.isFailure)
    }

    @Test
    fun `getDashboard success should return entities`() = runTest {
        // Given
        val entities = listOf(
            mapOf("name" to "Property 1", "scientificName" to "Property 2", "description" to "Description 1"),
            mapOf("name" to "Property 3", "scientificName" to "Property 4", "description" to "Description 2")
        )
        val dashboardResponse = DashboardResponse(entities, 2)
        `when`(apiService.getDashboard("keypass"))
            .thenReturn(Response.success(dashboardResponse))

        // When
        val result = repository.getDashboard("keypass")

        // Then
        assert(result.isSuccess)
        assert(result.getOrNull()?.size == 2)
    }

    @Test
    fun `getDashboard failure should return error`() = runTest {
        // Given
        `when`(apiService.getDashboard("keypass"))
            .thenReturn(Response.error(500, ResponseBody.create(null, "Server error")))

        // When
        val result = repository.getDashboard("keypass")

        // Then
        assert(result.isFailure)
    }
}
