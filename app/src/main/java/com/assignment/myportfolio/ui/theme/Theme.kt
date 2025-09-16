package com.assignment.myportfolio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
	primary = BluePrimary,
	primaryContainer = BluePrimaryLight,
	background = Color(0xFFFDFDFD),
	surface = Color(0xFFFFFFFF)
)
private val DarkColors = darkColorScheme(
	primary = BluePrimaryDark,
	primaryContainer = BluePrimary,
	background = Color(0xFF000000),
	surface = Color(0xFF121212)
)

// Extended app-specific semantic colors
data class ExtendedColors(
	val positive: Color,
	val negative: Color,
	val bannerOnlineBg: Color,
	val bannerOfflineBg: Color,
	val dotOnline: Color,
	val dotOffline: Color
)

private val LightExtendedColors = ExtendedColors(
	positive = Color(0xFF1CA57A),
	negative = Color(0xFFD32F2F),
	bannerOnlineBg = Color(0xFFE8F5E9),
	bannerOfflineBg = Color(0xFFFFEBEE),
	dotOnline = Color(0xFF2E7D32),
	dotOffline = Color(0xFFC62828)
)
private val DarkExtendedColors = ExtendedColors(
	positive = Color(0xFF1DBD88),
	negative = Color(0xFFFF6F6F),
	bannerOnlineBg = Color(0xFF1B5E20),
	bannerOfflineBg = Color(0xFF5D1F1F),
	dotOnline = Color(0xFF66BB6A),
	dotOffline = Color(0xFFEF5350)
)

private val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

object AppExtendedTheme {
	val colors: ExtendedColors
		@Composable get() = LocalExtendedColors.current
}

@Composable
fun AppTheme(useDarkThemeState: MutableState<Boolean>? = null, content: @Composable () -> Unit) {
	val dark = useDarkThemeState?.value ?: isSystemInDarkTheme()
	CompositionLocalProvider(LocalExtendedColors provides if (dark) DarkExtendedColors else LightExtendedColors) {
		MaterialTheme(
			colorScheme = if (dark) DarkColors else LightColors,
			typography = Typography,
			content = content
		)
	}
}