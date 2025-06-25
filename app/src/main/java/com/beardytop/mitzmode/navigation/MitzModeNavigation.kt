package com.beardytop.mitzmode.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.beardytop.mitzmode.ui.CompletedMitzvotScreen
import com.beardytop.mitzmode.ui.MitzModeApp
import com.beardytop.mitzmode.viewmodel.MitzModeViewModel

@Composable
fun MitzModeNavigation(
    viewModel: MitzModeViewModel,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MitzModeApp(viewModel = viewModel)
        }
        composable("completed") {
            CompletedMitzvotScreen(
                completedMitzvot = viewModel.completedMitzvot.value,
                onBack = { navController.popBackStack() }
            )
        }
    }
} 