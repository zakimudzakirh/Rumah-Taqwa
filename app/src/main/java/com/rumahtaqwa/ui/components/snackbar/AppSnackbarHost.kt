package com.rumahtaqwa.ui.components.snackbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.rumahtaqwa.core.util.SnackbarController
import com.rumahtaqwa.core.util.SnackbarType
import com.rumahtaqwa.ui.theme.Amber600
import com.rumahtaqwa.ui.theme.Green600
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnackbarHostViewModel @Inject constructor(
    snackbarController: SnackbarController
) : ViewModel() {
    val messages = snackbarController.messages
}

@Composable
fun AppSnackbarHost(
    modifier: Modifier = Modifier,
    viewModel: SnackbarHostViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var currentType by remember { mutableStateOf(SnackbarType.SUCCESS) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.messages.collect { snackbarMessage ->
            currentType = snackbarMessage.type
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(snackbarMessage.message)
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
        snackbar = { data ->
            AppSnackbar(type = currentType, message = data.visuals.message)
        }
    )
}

@Composable
private fun AppSnackbar(type: SnackbarType, message: String) {
    val containerColor = when (type) {
        SnackbarType.SUCCESS -> Green600
        SnackbarType.WARNING -> Amber600
        SnackbarType.ERROR -> MaterialTheme.colorScheme.error
    }
    val contentColor = Color.White
    val icon = when (type) {
        SnackbarType.SUCCESS -> Icons.Filled.CheckCircle
        SnackbarType.WARNING -> Icons.Filled.Warning
        SnackbarType.ERROR -> Icons.Filled.Error
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
