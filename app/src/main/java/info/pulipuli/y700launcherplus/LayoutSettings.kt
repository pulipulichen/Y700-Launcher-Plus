package info.pulipuli.y700launcherplus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun LayoutSettings(
    layout: LauncherLayout,
    update: (LauncherLayout) -> Unit,
    showHidden: () -> Unit,
    addPage: () -> Unit,
    removeLastPage: () -> Unit
) {
    var columnsExpanded by remember { mutableStateOf(false) }
    val labelSetting = stringResource(R.string.show_labels)
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.home_columns))
            Column {
                OutlinedButton(onClick = { columnsExpanded = true }) {
                    Text(columnLabel(layout.columns))
                }
                DropdownMenu(columnsExpanded, onDismissRequest = { columnsExpanded = false }) {
                    for (columns in listOf(0, 4, 5, 6, 7, 8)) {
                        DropdownMenuItem(text = { Text(columnLabel(columns)) }, onClick = {
                            columnsExpanded = false
                            update(layout.copy(columns = columns))
                        })
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(labelSetting)
            Switch(
                checked = layout.showLabels,
                onCheckedChange = { update(layout.copy(showLabels = it)) },
                modifier = Modifier.semantics { contentDescription = labelSetting }
            )
        }
        Button(onClick = addPage, enabled = layout.pages.size < LauncherLayout.MAX_PAGES) {
            Text(stringResource(R.string.add_page))
        }
        OutlinedButton(onClick = removeLastPage, enabled = layout.pages.size > 1 && layout.pages.last().isEmpty()) {
            Text(stringResource(R.string.remove_empty_last_page))
        }
        OutlinedButton(onClick = showHidden) { Text(stringResource(R.string.hidden_apps)) }
    }
}

@Composable
private fun columnLabel(columns: Int): String = if (columns == 0) {
    stringResource(R.string.automatic)
} else {
    stringResource(R.string.column_count, columns)
}
