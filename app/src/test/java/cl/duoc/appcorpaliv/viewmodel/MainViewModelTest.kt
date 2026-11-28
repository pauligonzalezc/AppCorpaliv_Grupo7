package cl.duoc.appcorpaliv.viewmodel

import cl.duoc.appcorpaliv.navigation.NavigationEvent
import cl.duoc.appcorpaliv.navigation.Screen
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : StringSpec({

    val testDispatcher = UnconfinedTestDispatcher()
    beforeTest { Dispatchers.setMain(testDispatcher) }
    afterTest { Dispatchers.resetMain() }

    "navigateTo, navigateBack y navigateUp deben emitir sus eventos en orden" {
        val viewModel = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()

        runTest(testDispatcher) {
            // escuchamos los eventos como lo hace AppNavigation
            backgroundScope.launch { viewModel.navigationEvents.toList(eventos) }

            viewModel.navigateTo(Screen.Registros)
            viewModel.navigateTo(Screen.Home, popUpTo = Screen.Home, inclusive = true)
            viewModel.navigateBack()
            viewModel.navigateUp()

            eventos shouldContainExactly listOf(
                NavigationEvent.NavigateTo(route = Screen.Registros),
                NavigationEvent.NavigateTo(route = Screen.Home, popUpToRoute = Screen.Home, inclusive = true),
                NavigationEvent.PopBackStack,
                NavigationEvent.NavigateUp
            )
        }
    }
})
