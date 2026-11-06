package info.pulipuli.y700launcherplus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun PageSettings(layout: LauncherLayout, update: (LauncherLayout) -> Unit) {
    var deleting by rememberSaveable { mutableStateOf<Int?>(null) }
    Text(stringResource(R.string.manage_pages), style = MaterialTheme.typography.titleMedium)
    layout.pages.forEachIndexed { index, packages ->
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(stringResource(R.string.page_summary, index + 1, packages.size))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(enabled = index > 0, onClick = { update(layout.movePage(index, index - 1)) }) {
                    Text(stringResource(R.string.move_previous_page))
                }
                TextButton(enabled = index < layout.pages.lastIndex,
                    onClick = { update(layout.movePage(index, index + 1)) }) {
                    Text(stringResource(R.string.move_next_page))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(enabled = index != layout.defaultPage,
                    onClick = { update(layout.copy(defaultPage = index)) }) {
                    Text(stringResource(if (index == layout.defaultPage) R.string.default_page else R.string.set_default_page))
                }
                TextButton(enabled = layout.pages.size > 1, onClick = { deleting = index }) {
                    Text(stringResource(R.string.delete_page))
                }
            }
        }
    }
    deleting?.takeIf { it in layout.pages.indices }?.let { index ->
        AlertDialog(onDismissRequest = { deleting = null },
            title = { Text(stringResource(R.string.delete_page)) },
            text = { Text(stringResource(R.string.confirm_delete_page, index + 1, layout.pages[index].size)) },
            confirmButton = {
                TextButton(onClick = {
                    deleting = null
                    if (layout.pages.size > 1) update(layout.removePage(index))
                }) { Text(stringResource(R.string.delete_page)) }
            },
            dismissButton = {
                TextButton(onClick = { deleting = null }) { Text(stringResource(android.R.string.cancel)) }
            })
    }
}

@Composable
internal fun ColumnSetting(columns: Int, update: (Int) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Text(stringResource(R.string.drawer_columns))
    Column {
        OutlinedButton(onClick = { expanded = true }) {
            Text(if (columns == 0) stringResource(R.string.automatic) else stringResource(R.string.column_count, columns))
        }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            for (value in listOf(0, 4, 5, 6, 7, 8)) {
                DropdownMenuItem(text = {
                    Text(if (value == 0) stringResource(R.string.automatic) else stringResource(R.string.column_count, value))
                }, onClick = { expanded = false; update(value) })
            }
        }
    }
}

@Composable
internal fun DockSettings(
    catalog: List<LauncherApp>, layout: LauncherLayout,
    update: (LauncherLayout) -> Unit, done: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val apps = catalog.associateBy { it.packageName }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.dock_explanation))
        Text(stringResource(R.string.dock_count, layout.dock.size, LauncherLayout.MAX_DOCK_APPS))
        Button(onClick = done) { Text(stringResource(R.string.done)) }
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true,
            label = { Text(stringResource(R.string.search_apps)) })
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            item { Text(stringResource(R.string.dock_selected), style = MaterialTheme.typography.titleMedium) }
            items(layout.dock, key = { "selected:$it" }) { id ->
                val index = layout.dock.indexOf(id)
                Column {
                    Text(apps[id]?.label ?: id)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(enabled = index > 0, onClick = {
                            val next = layout.dock.toMutableList().apply { add(index - 1, removeAt(index)) }
                            update(layout.copy(dock = next))
                        }) { Text(stringResource(R.string.move_earlier)) }
                        TextButton(enabled = index < layout.dock.lastIndex, onClick = {
                            val next = layout.dock.toMutableList().apply { add(index + 1, removeAt(index)) }
                            update(layout.copy(dock = next))
                        }) { Text(stringResource(R.string.move_later)) }
                        TextButton(onClick = { update(layout.copy(dock = layout.dock - id)) }) {
                            Text(stringResource(R.string.remove_from_home))
                        }
                    }
                }
            }
            item { Text(stringResource(R.string.all_apps), style = MaterialTheme.typography.titleMedium) }
            items(catalog.filter {
                it.label.contains(query.trim(), true) || it.packageName.contains(query.trim(), true)
            }, key = { "app:${it.packageName}" }) { app ->
                val selected = app.packageName in layout.dock
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = selected,
                        enabled = selected || layout.dock.size < LauncherLayout.MAX_DOCK_APPS,
                        onCheckedChange = { checked ->
                            update(layout.copy(dock = if (checked) (layout.dock + app.packageName).distinct()
                                else layout.dock - app.packageName))
                        }, modifier = Modifier.semantics { contentDescription = app.label })
                    Text(app.label, Modifier.weight(1f))
                }
            }
        }
    }
}
