package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.text_click_left_nav
import linttoolkit.app.generated.resources.text_home
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
class HomeScreen(
    val name: String,
) : FeatureScreen(
    label = name,
    path = arrayOf(
        Res.string.text_home
    ),
    icon = {
        Icon(imageVector = Icons.Default.Home, contentDescription = null)
    }
) {

    var screens: ArrayList<FeatureScreen> = arrayListOf()

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                screens.filter { it.recommendText != null }.forEach {
                    RecommendItem(it) { navigator.push(it) }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun RecommendItem(screen: FeatureScreen, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(4.dp))
            .clickable {
                onClick()
            }.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = screen.recommendText?.let { stringResource(it) } ?: "",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.text_click_left_nav),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
            )
            Text(
                text = screen.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}