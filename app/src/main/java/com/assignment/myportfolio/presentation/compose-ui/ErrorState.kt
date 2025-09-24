package com.assignment.myportfolio.presentation.compose_ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.assignment.myportfolio.R
import com.assignment.myportfolio.ui.theme.AppTheme

@Composable
fun ErrorState(
    isOnline: Boolean,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isOnline) {
            // No internet connection
            Text(
                text = stringResource(id = R.string.error_no_internet_no_data_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.error_no_internet_no_data_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.tap_to_retry))
        }
    }
}

@Preview(name = "No Internet", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ErrorStateNoInternetPreview() {
    AppTheme {
        ErrorState(isOnline = false, onRetry = {})
    }
}

@Preview(name = "Loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorStateLoadingPreview() {
    AppTheme {
        ErrorState(isOnline = true, onRetry = {})
    }
}
