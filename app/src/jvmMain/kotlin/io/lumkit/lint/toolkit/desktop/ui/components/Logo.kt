package io.lumkit.lint.toolkit.desktop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.lumkit.lint.desktop.app.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Preview
@Composable
private fun LogoPreview() {
    Logo(modifier = Modifier.size(400.dp))
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Logo(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.logo_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary
        )
        Icon(
            painter = painterResource(Res.drawable.logo_mid),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = Color.White
        )
        Icon(
            painter = painterResource(Res.drawable.logo_foreground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = .95f),
        )
        Icon(
            painter = painterResource(Res.drawable.logo_android),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = Color.White,
        )
        Icon(
            painter = painterResource(Res.drawable.logo_eyes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = .95f),
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier,
    durationMillis: Int = 2000,
    isStart: Boolean,
) {
    var isRotating by remember { mutableStateOf(isStart) }
    var rotation by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()

    LaunchedEffect(isStart) {
        isRotating = isStart
    }

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.logo_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary
        )
        Icon(
            painter = painterResource(Res.drawable.logo_mid),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().rotate(
                if (isRotating) {
                    rotation = angle
                    angle
                } else {
                    rotation
                }
            ),
            tint = Color.White
        )
        Icon(
            painter = painterResource(Res.drawable.logo_foreground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = .95f),
        )
        Icon(
            painter = painterResource(Res.drawable.logo_android),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = Color.White,
        )
        Icon(
            painter = painterResource(Res.drawable.logo_eyes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = .95f),
        )
    }
}