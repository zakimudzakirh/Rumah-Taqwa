package com.rumahtaqwa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumahtaqwa.ui.navigation.AppNavGraph
import com.rumahtaqwa.ui.navigation.NavViewModel
import com.rumahtaqwa.ui.theme.RumahTaqwaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val NavViewModel: NavViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent{
            val themeMode by NavViewModel.themeMode.collectAsStateWithLifecycle()

            RumahTaqwaTheme(
                themeMode = themeMode
            ) {
                AppNavGraph()
            }
        }
    }
}