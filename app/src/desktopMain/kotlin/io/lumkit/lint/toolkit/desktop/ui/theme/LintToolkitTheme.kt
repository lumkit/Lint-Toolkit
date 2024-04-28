package io.lumkit.lint.toolkit.desktop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import io.github.lumkit.desktop.ui.theme.AnimatedLintTheme
import linttoolkit.app.generated.resources.GoogleSans_Regular
import linttoolkit.app.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun LintToolkitTheme(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedLintTheme(modifier) {
        val fontFamily = FontFamily(Font(Res.font.GoogleSans_Regular))
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography.copy(
                displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = fontFamily),
                displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = fontFamily),
                displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = fontFamily),
                headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = fontFamily),
                headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = fontFamily),
                headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = fontFamily),
                titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = fontFamily),
                titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = fontFamily),
                titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = fontFamily),
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = fontFamily),
                bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = fontFamily),
                bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = fontFamily),
                labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = fontFamily),
                labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = fontFamily),
            ),
            content = content
        )
    }
}