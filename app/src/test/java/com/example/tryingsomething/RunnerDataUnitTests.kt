package com.example.tryingsomething

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.tryingsomething.dto.Runner
import com.example.tryingsomething.service.RunnerService
import com.example.tryingsomething.ui.main.MainViewModel
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RunnerDataUnitTests {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    lateinit var mainViewModel: MainViewModel

    var runnerService: RunnerService = mockk<RunnerService>()

    @Test
    fun confirmEasternRedbud_outputsEasterRedbud() {
        var runner : Runner = Runner(1, "", "Tajce")
        assertEquals("Tajce", runner.toString())
    }

    @Test
    fun searchForRedBud_returnsRedBud(){
        givenAFeedOfRunnersDataAreAvailable()
        whenSearchForRedBud()
        thanResultConstainsEasternRedbud()
    }

    private fun givenAFeedOfRunnersDataAreAvailable() {
        mainViewModel = MainViewModel()
    }

    private fun createMockData() {
        var allRunnersLiveData = MutableLiveData<ArrayList<Runner>>()
        var allRunners = ArrayList<Runner>()
        //create and add mock runners
        var redbud = Runner(1, "Tajce","Trajceski")
        allRunners.add(redbud)
        var anotherRunner = Runner(2,"Tajce", "Veleski")
        allRunners.add(anotherRunner)

        allRunnersLiveData.postValue(allRunners)
        every {
            runnerService.fetchRunners(any())
        }returns allRunnersLiveData
        mainViewModel.runnerService = runnerService

    }

    private fun whenSearchForRedBud() {
        mainViewModel.fetchRunners("Tajce")
    }

    private fun thanResultConstainsEasternRedbud() {
        var redBudFound = false
        mainViewModel.runners.observeForever{
            //here is where we do the observing data
            assertNotNull(it)
            assertTrue(it.size > 0)
            it.forEach{
                if (it.name == "Tajce" && it.surName.contains("trajkoski")){
                    redBudFound = true
                }
            }
        }
        assertTrue(redBudFound)
    }

    @Test
    fun searchForGarbage_returnsNothing(){
        givenAFeedOfRunnersDataAreAvailable()
        whenIsSearchedForGarbage()
        thanReturnZeroResults()
    }

    private fun whenIsSearchedForGarbage() {
        mainViewModel.fetchRunners("somethingelse")

    }

    private fun thanReturnZeroResults() {
        mainViewModel.runners.observeForever{
            assertEquals(0, it.size)
        }
    }
}