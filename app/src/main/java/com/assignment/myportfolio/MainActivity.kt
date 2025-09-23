package com.assignment.myportfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.assignment.myportfolio.presentation.PortfolioScreen
import com.assignment.myportfolio.presentation.PortfolioViewModel
import com.assignment.myportfolio.ui.theme.AppTheme
import com.assignment.myportfolio.core.NetworkMonitor
import com.assignment.myportfolio.domain.usecase.ComputePortfolioUseCase
import com.assignment.myportfolio.domain.usecase.GetHoldingsUseCase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val darkMode = remember { mutableStateOf(false) }
            AppTheme(useDarkThemeState = darkMode) {
                val viewModel: PortfolioViewModel = hiltViewModel()
                PortfolioScreen(viewModel = viewModel, darkModeState = darkMode)
            }
        }
    }
}

