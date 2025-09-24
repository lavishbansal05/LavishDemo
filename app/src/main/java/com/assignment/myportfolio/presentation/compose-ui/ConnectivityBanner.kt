package com.assignment.myportfolio.presentation.compose_ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.assignment.myportfolio.R
import com.assignment.myportfolio.ui.theme.AppExtendedTheme
import com.assignment.myportfolio.ui.theme.AppTheme
import kotlinx.coroutines.delay

@Composable
fun ConnectivityBanner(isOnline: Boolean) {
	val show = remember { mutableStateOf(false) }
	LaunchedEffect(isOnline) {
		if (!isOnline) {
			show.value = true
		} else {
			show.value = true
			delay(2500)
			show.value = false
		}
	}
	AnimatedVisibility(
		visible = show.value,
		enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
		exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
	) {
		val ec = AppExtendedTheme.colors
		val bg = if (isOnline) ec.bannerOnlineBg else ec.bannerOfflineBg
		val dot = if (isOnline) ec.dotOnline else ec.dotOffline
		val text = if (isOnline) stringResource(id = R.string.connectivity_online) else stringResource(id = R.string.connectivity_offline)
		Row(
			Modifier
				.fillMaxWidth()
				.background(bg)
				.padding(horizontal = 16.dp, vertical = 8.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				Modifier
					.size(10.dp)
					.clip(CircleShape)
					.background(dot)
			)
			Text(
				text = text,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

@Preview(name = "Connectivity Online", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ConnectivityBannerOnlinePreview() {
	AppTheme {
		ConnectivityBanner(isOnline = true)
	}
}

@Preview(name = "Connectivity Offline", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ConnectivityBannerOfflinePreview() {
	AppTheme {
		ConnectivityBanner(isOnline = false)
	}
}
