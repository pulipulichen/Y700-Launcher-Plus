package info.pulipuli.y700launcherplus

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class LauncherApp(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LauncherScreen()
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun LauncherScreen() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf(emptyList<LauncherApp>()) }
    var pinnedPackages by remember { mutableStateOf(emptySet<String>()) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var showAllApps by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val loadedApps = withContext(Dispatchers.IO) {
            loadLaunchableApps(context)
        }
        apps = loadedApps
        val preferences = context.getSharedPreferences("launcher", Context.MODE_PRIVATE)
        val savedPins = preferences.getStringSet("pinned_packages", null)
        pinnedPackages = savedPins?.toSet()
            ?: loadedApps.take(8).map { it.packageName }.toSet()
        if (savedPins == null) {
            preferences.edit().putStringSet("pinned_packages", pinnedPackages).apply()
        }
    }

    val normalizedQuery = searchText.trim()
    val visibleApps = apps
        .filter { app ->
            (showAllApps || normalizedQuery.isNotEmpty() || app.packageName in pinnedPackages) &&
                (normalizedQuery.isEmpty() || app.label.contains(normalizedQuery, ignoreCase = true))
        }
        .sortedBy { it.label.lowercase() }

    fun togglePin(app: LauncherApp) {
        val updatedPins = pinnedPackages.toMutableSet()
        if (!updatedPins.add(app.packageName)) updatedPins.remove(app.packageName)
        pinnedPackages = updatedPins
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE)
            .edit()
            .putStringSet("pinned_packages", updatedPins)
            .apply()
    }

    fun launchApp(app: LauncherApp) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        if (launchIntent != null) context.startActivity(launchIntent)
    }

    Scaffold { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = stringResource(R.string.launcher_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (showAllApps) {
                    OutlinedButton(onClick = { showAllApps = false }) {
                        Text(stringResource(R.string.home))
                    }
                } else {
                    Button(onClick = { showAllApps = true }) {
                        Text(stringResource(R.string.all_apps))
                    }
                }
            }

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.search_apps)) },
                placeholder = { Text(stringResource(R.string.search_hint)) },
                colors = TextFieldDefaults.colors()
            )

            Text(
                text = if (showAllApps || normalizedQuery.isNotEmpty()) {
                    stringResource(R.string.all_apps)
                } else {
                    stringResource(R.string.pinned_apps)
                },
                style = MaterialTheme.typography.titleLarge
            )

            if (apps.isEmpty()) {
                Text(
                    text = stringResource(R.string.loading_apps),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (visibleApps.isEmpty()) {
                Text(
                    text = if (normalizedQuery.isNotEmpty()) {
                        stringResource(R.string.no_search_results)
                    } else {
                        stringResource(R.string.no_pinned_apps)
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 124.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(visibleApps, key = { it.packageName }) { app ->
                        AppTile(
                            app = app,
                            isPinned = app.packageName in pinnedPackages,
                            onLaunch = { launchApp(app) },
                            onTogglePin = { togglePin(app) }
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.taskbar_hint),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun AppTile(
    app: LauncherApp,
    isPinned: Boolean,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                onClick = onLaunch,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        bitmap = app.icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
            OutlinedButton(
                onClick = onTogglePin,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Text(
                    stringResource(
                        if (isPinned) R.string.remove_from_home else R.string.add_to_home
                    )
                )
            }
        }
    }
}

private fun loadLaunchableApps(context: Context): List<LauncherApp> {
    val packageManager = context.packageManager
    val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    val activities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.queryIntentActivities(
            launcherIntent,
            PackageManager.ResolveInfoFlags.of(0L)
        )
    } else {
        @Suppress("DEPRECATION")
        packageManager.queryIntentActivities(launcherIntent, 0)
    }

    return activities.mapNotNull { resolveInfo ->
        val packageName = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
        if (packageName == context.packageName) return@mapNotNull null
        val label = resolveInfo.loadLabel(packageManager)?.toString()?.takeIf { it.isNotBlank() }
            ?: return@mapNotNull null
        val bitmap = resolveInfo.loadIcon(packageManager).toBitmap().asImageBitmap()
        LauncherApp(packageName, label, bitmap)
    }.distinctBy { it.packageName }
}

private fun Drawable.toBitmap(): Bitmap {
    val width = intrinsicWidth.coerceAtLeast(1)
    val height = intrinsicHeight.coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
