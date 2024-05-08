package io.lumkit.lint.toolkit.desktop.ui.screen.splash

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.lumkit.desktop.ui.components.LintHorizontalScrollbar
import io.github.lumkit.desktop.ui.components.LintVerticalScrollBar
import io.github.lumkit.lint.desktop.app.generated.resources.Res
import io.github.lumkit.lint.desktop.app.generated.resources.text_components
import io.github.lumkit.lint.desktop.app.generated.resources.text_components_aapt
import io.github.lumkit.lint.desktop.app.generated.resources.text_components_aapt_license
import io.github.lumkit.lint.desktop.app.generated.resources.text_components_adb
import io.github.lumkit.lint.desktop.app.generated.resources.text_components_adb_license
import io.github.lumkit.lint.desktop.app.generated.resources.text_components_lint_toolkit_components
import io.github.lumkit.lint.desktop.app.generated.resources.text_components_lint_toolkit_components_license
import io.github.lumkit.lint.desktop.app.generated.resources.text_components_python
import io.github.lumkit.lint.desktop.app.generated.resources.text_components_python_license
import io.lumkit.lint.toolkit.desktop.model.LintRuntimeComponent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LicenseAgreementScreen(
    onAgree: (Boolean) -> Unit,
) {
    val components = rememberSaveable {
        mutableStateListOf(
            LintRuntimeComponent(
                name = Res.string.text_components_adb,
                license = Res.string.text_components_adb_license,
                selected = false,
            ),
            LintRuntimeComponent(
                name = Res.string.text_components_aapt,
                license = Res.string.text_components_aapt_license,
                selected = false,
            ),
            LintRuntimeComponent(
                name = Res.string.text_components_python,
                license = Res.string.text_components_python_license,
                selected = false,
            ),
            LintRuntimeComponent(
                name = Res.string.text_components_lint_toolkit_components,
                license = Res.string.text_components_lint_toolkit_components_license,
                selected = false,
            ),
        )
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ){
        var selectedComponent by rememberSaveable { mutableStateOf<LintRuntimeComponent?>(null) }

        Column(
            modifier = Modifier.fillMaxHeight().width(300.dp)
                .border(1.dp, DividerDefaults.color),
        ) {
            val verticalScrollState = rememberScrollState()
            val horizontalScrollState = rememberScrollState()

            val verticalScrollAdapter = rememberScrollbarAdapter(verticalScrollState)
            val horizontalScrollAdapter = rememberScrollbarAdapter(horizontalScrollState)

            Text(text = stringResource(Res.string.text_components), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
            Row(
                modifier = Modifier.fillMaxSize().weight(1f),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().weight(1f)
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState),
                ) {
                    components.forEachIndexed { index, component ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    components[index] = component.copy(selected = !component.selected)
                                    onAgree(components.map { it.selected }.none { !it })
                                    selectedComponent = component
                                }.padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Checkbox(component.selected, onCheckedChange = null)
                            Text(text = stringResource(component.name), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                LintVerticalScrollBar(verticalScrollAdapter)
            }
            Spacer(modifier = Modifier.width(4.dp))
            LintHorizontalScrollbar(horizontalScrollAdapter)
        }
        Row(
            modifier = Modifier.fillMaxSize()
                .border(1.dp, DividerDefaults.color),
        ) {
            val verticalScrollState = rememberScrollState()
            val verticalScrollAdapter = rememberScrollbarAdapter(verticalScrollState)

            selectedComponent?.let {
                Text(
                    text = stringResource(it.license),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                        .verticalScroll(verticalScrollState)
                )
            }

            LintVerticalScrollBar(verticalScrollAdapter)
        }
    }
}