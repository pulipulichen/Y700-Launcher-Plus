package info.pulipuli.y700launcherplus

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun LayoutSettings(
    layout: LauncherLayout,
    update: (LauncherLayout) -> Unit,
    showHidden: () -> Unit,
    addPage: () -> Unit,
    showDock: () -> Unit,
    managePages: (LauncherLayout) -> Unit,
    restore: (LauncherLayout) -> Unit
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
        PageSettings(layout, managePages)
        OutlinedButton(onClick = showDock) { Text(stringResource(R.string.launcher_dock)) }
        ColumnSetting(layout.drawerColumns) { update(layout.copy(drawerColumns = it)) }
        OutlinedButton(onClick = showHidden) { Text(stringResource(R.string.hidden_apps)) }
        BackupControls(layout, restore)
    }
}

@Composable
private fun columnLabel(columns: Int): String = if (columns == 0) {
    stringResource(R.string.automatic)
} else {
    stringResource(R.string.column_count, columns)
}


@Composable
private fun BackupControls(layout: LauncherLayout, restore: (LauncherLayout) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingRestore by remember { mutableStateOf<LauncherLayout?>(null) }

    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) scope.launch {
            try {
                withContext(Dispatchers.IO) { writeBackup(context.contentResolver, uri, layout) }
                Toast.makeText(context, R.string.backup_saved, Toast.LENGTH_SHORT).show()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) scope.launch {
            try {
                pendingRestore = withContext(Dispatchers.IO) { readBackup(context.contentResolver, uri) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                Toast.makeText(context, R.string.restore_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    OutlinedButton(onClick = { export.launch("Y700-Launcher-Plus-layout.json") }) {
        Text(stringResource(R.string.export_backup))
    }
    OutlinedButton(onClick = { importer.launch(arrayOf("application/json", "application/octet-stream")) }) {
        Text(stringResource(R.string.import_backup))
    }
    pendingRestore?.let { imported ->
        AlertDialog(
            onDismissRequest = { pendingRestore = null },
            title = { Text(stringResource(R.string.import_backup)) },
            text = { Text(stringResource(R.string.confirm_restore)) },
            confirmButton = {
                TextButton(onClick = {
                    pendingRestore = null
                    restore(imported)
                }) { Text(stringResource(R.string.restore)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingRestore = null }) { Text(stringResource(android.R.string.cancel)) }
            }
        )
    }
}
