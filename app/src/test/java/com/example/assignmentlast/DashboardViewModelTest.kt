package com.example.assignmentlast

import com.example.assignmentlast.data.models.Entity
import com.example.assignmentlast.data.repository.AppRepository
import com.example.assignmentlast.ui.dashboard.DashboardViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DashboardViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: AppRepository

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchDashboard success should update live data`() = runTest {
        // Given
        val entities = listOf(
            mapOf("name" to "Property 1", "scientificName" to "Property 2", "description" to "Description 1"),
            mapOf("name" to "Property 3", "scientificName" to "Property 4", "description" to "Description 2")
        )
        `when`(repository.getDashboard("keypass"))
            .thenReturn(Result.success(entities))

        // When
        viewModel.fetchDashboard("keypass")
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Then
        val result = viewModel.entities.value
        assert(result != null)
        assert(result!!.isSuccess)
        assert(result.getOrNull()?.size == 2)
    }

    @Test
    fun `fetchDashboard failure should update live data with error`() = runTest {
        // Given
        val exception = Exception("Network error")
        `when`(repository.getDashboard("keypass"))
            .thenReturn(Result.failure(exception))

        // When
        viewModel.fetchDashboard("keypass")
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Then
        val result = viewModel.entities.value
        assert(result != null)
        assert(result!!.isFailure)
        assert(result.exceptionOrNull()?.message == "Network error")
    }
}
