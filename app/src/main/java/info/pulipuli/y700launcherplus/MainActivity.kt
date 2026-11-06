package info.pulipuli.y700launcherplus

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private var catalogRevision by mutableIntStateOf(0)
    private var homeRevision by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dark = isSystemInDarkTheme()
            val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (dark) dynamicDarkColorScheme(this) else dynamicLightColorScheme(this)
            } else {
                if (dark) darkColorScheme() else lightColorScheme()
            }
            MaterialTheme(colorScheme = colors) {
                LauncherScreen(catalogRevision, homeRevision)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        catalogRevision++
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasCategory(Intent.CATEGORY_HOME)) homeRevision++
    }
}

private enum class Panel { HOME, DRAWER, SETTINGS, HIDDEN, DOCK }

@Composable
private fun LauncherScreen(catalogRevision: Int, homeRevision: Int) {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE)
    }
    var catalog by remember { mutableStateOf(emptyList<LauncherApp>()) }
    var storedLayout by remember { mutableStateOf<LauncherLayout?>(null) }
    var panel by rememberSaveable { mutableStateOf(Panel.HOME) }
    var query by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(catalogRevision) {
        catalog = withContext(Dispatchers.IO) { loadLaunchableApps(context) }
        if (storedLayout == null) {
            val firstLaunch = !preferences.contains("layout_v1") && !preferences.contains("pinned_packages")
            storedLayout = LauncherLayout.load(preferences, catalog)
            if (firstLaunch) panel = Panel.DOCK
        }
    }
    LaunchedEffect(homeRevision) {
        if (homeRevision > 0) {
            panel = Panel.HOME
            query = ""
        }
    }
    BackHandler(panel != Panel.HOME) {
        panel = if (panel == Panel.HIDDEN) Panel.SETTINGS else Panel.HOME
        query = ""
    }

    Scaffold { insets ->
        Column(
            Modifier.fillMaxSize().padding(insets).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val layout = storedLayout
            if (layout == null) {
                Text(stringResource(R.string.loading_apps), Modifier.padding(16.dp))
                return@Column
            }
            val pager = rememberPagerState(initialPage = layout.defaultPage) { layout.pages.size }
            LaunchedEffect(homeRevision) {
                if (homeRevision > 0) pager.scrollToPage(layout.defaultPage)
            }
            val scope = rememberCoroutineScope()
            val apps = remember(catalog) { catalog.associateBy { it.packageName } }

            fun update(next: LauncherLayout) {
                next.save(preferences)
                storedLayout = next
            }

            fun setPageApps(index: Int, packages: List<String>) {
                update(layout.copy(pages = layout.pages.toMutableList().apply { set(index, packages) }))
            }

            fun open(app: LauncherApp) {
                val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                try {
                    if (intent == null) throw ActivityNotFoundException()
                    context.startActivity(intent)
                    panel = Panel.HOME
                    query = ""
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(context, R.string.app_unavailable, Toast.LENGTH_SHORT).show()
                } catch (_: SecurityException) {
                    Toast.makeText(context, R.string.app_unavailable, Toast.LENGTH_SHORT).show()
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (panel == Panel.HOME) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = { panel = Panel.SETTINGS }) {
                        Text(stringResource(R.string.settings))
                    }
                } else {
                    TextButton(onClick = {
                        panel = if (panel == Panel.HIDDEN) Panel.SETTINGS else Panel.HOME
                        query = ""
                    }) {
                        Text(stringResource(if (panel == Panel.HIDDEN) R.string.settings else R.string.home))
                    }
                    Text(stringResource(when (panel) {
                        Panel.DRAWER -> R.string.all_apps
                        Panel.HIDDEN -> R.string.hidden_apps
                        Panel.DOCK -> R.string.launcher_dock
                        else -> R.string.settings
                    }))
                }
            }

            when (panel) {
                Panel.HOME -> {
                    HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
                        val packages = layout.pages[page]
                        AppGrid(packages.mapNotNull(apps::get), layout.columns, Modifier.fillMaxSize(),
                            R.string.no_pinned_apps) { app ->
                            AppTile(app, layout.showLabels, { open(app) }) { close ->
                                AppMenuItem(R.string.remove_from_home, close) {
                                    setPageApps(page, packages - app.packageName)
                                }
                                val destinations = listOf(
                                    page - 1 to R.string.move_previous_page,
                                    page + 1 to R.string.move_next_page
                                )
                                for ((destination, label) in destinations) {
                                    if (destination !in layout.pages.indices) continue
                                    AppMenuItem(label, close) {
                                        val pages = layout.pages.toMutableList()
                                        pages[page] = packages - app.packageName
                                        if (app.packageName !in pages[destination]) {
                                            pages[destination] = pages[destination] + app.packageName
                                        }
                                        update(layout.copy(pages = pages))
                                    }
                                }
                            }
                        }
                    }

                    Row(Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(enabled = pager.currentPage > 0, onClick = {
                            scope.launch { pager.animateScrollToPage(pager.currentPage - 1) }
                        }) { Text(stringResource(R.string.previous_page)) }
                        Text(stringResource(R.string.page_counter, pager.currentPage + 1, layout.pages.size))
                        TextButton(enabled = pager.currentPage + 1 < layout.pages.size, onClick = {
                            scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                        }) { Text(stringResource(R.string.next_page)) }
                    }

                    Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceVariant) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LazyRow(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(layout.dock.mapNotNull(apps::get), key = { it.packageName }) { app ->
                                    AppTile(app, false, { open(app) }, Modifier.size(48.dp)) { close ->
                                        AppMenuItem(R.string.remove_from_dock, close) {
                                            update(layout.copy(dock = layout.dock - app.packageName))
                                        }
                                    }
                                }
                            }
                            Button(onClick = { panel = Panel.DRAWER }) {
                                Text(stringResource(R.string.all_apps))
                            }
                        }
                    }
                }
                Panel.DRAWER, Panel.HIDDEN -> {
                    val hiddenPanel = panel == Panel.HIDDEN
                    OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.search_apps)) }, singleLine = true)
                    val search = query.trim()
                    val filtered = catalog.filter { app ->
                        (app.packageName in layout.hidden) == hiddenPanel &&
                            (app.label.contains(search, ignoreCase = true) ||
                                app.packageName.contains(search, ignoreCase = true))
                    }
                    AppGrid(filtered, layout.drawerColumns, Modifier.weight(1f), R.string.no_search_results) { app ->
                        val packageName = app.packageName
                        val page = pager.currentPage
                        val packages = layout.pages[page]
                        AppTile(app, true, { open(app) }) { close ->
                            if (hiddenPanel) {
                                AppMenuItem(R.string.show_in_drawer, close) {
                                    update(layout.copy(hidden = layout.hidden - packageName))
                                }
                            } else {
                                if (packageName !in packages) {
                                    AppMenuItem(R.string.add_to_home, close) {
                                        setPageApps(page, packages + packageName)
                                    }
                                }
                                if (packageName !in layout.dock) {
                                    AppMenuItem(R.string.add_to_dock, close,
                                        enabled = layout.dock.size < LauncherLayout.MAX_DOCK_APPS) {
                                        update(layout.copy(dock = layout.dock + packageName))
                                    }
                                }
                                AppMenuItem(R.string.hide_from_drawer, close) {
                                    update(layout.copy(hidden = layout.hidden + packageName))
                                }
                            }
                        }
                    }
                }
                Panel.DOCK -> DockSettings(catalog, layout, ::update) { panel = Panel.HOME }
                Panel.SETTINGS -> {
                    LayoutSettings(
                        layout = layout,
                        update = ::update,
                        showHidden = { panel = Panel.HIDDEN },
                        showDock = { panel = Panel.DOCK },
                        managePages = { next ->
                            pager.requestScrollToPage(next.defaultPage)
                            update(next)
                        },
                        addPage = {
                            val nextPage = layout.pages.size
                            update(layout.copy(pages = layout.pages + listOf(emptyList())))
                            panel = Panel.HOME
                            pager.requestScrollToPage(nextPage)
                        },
                        restore = { restored ->
                            pager.requestScrollToPage(restored.defaultPage)
                            update(restored)
                        }

                    )
                }
            }
        }
    }
}

@Composable
private fun AppGrid(
    apps: List<LauncherApp>,
    columns: Int,
    modifier: Modifier,
    emptyText: Int,
    tile: @Composable (LauncherApp) -> Unit
) {
    if (apps.isEmpty()) {
        Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(stringResource(emptyText), textAlign = TextAlign.Center)
        }
    } else {
        LazyVerticalGrid(
            columns = if (columns == 0) GridCells.Adaptive(96.dp) else GridCells.Fixed(columns),
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(apps, key = { it.packageName }) { tile(it) }
        }
    }
}

@Composable
private fun AppTile(
    app: LauncherApp,
    showLabel: Boolean,
    open: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    menu: @Composable (close: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = stringResource(R.string.app_options)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier.heightIn(min = 48.dp)
                .combinedClickable(onClick = open, onLongClickLabel = options, onLongClick = { expanded = true })
                .then(if (showLabel) Modifier else Modifier.semantics { contentDescription = app.label })
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(app.icon, contentDescription = null, modifier = Modifier.size(if (showLabel) 44.dp else 36.dp))
            if (showLabel) Text(app.label, style = MaterialTheme.typography.labelMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            menu { expanded = false }
        }
    }
}

@Composable
private fun AppMenuItem(label: Int, close: () -> Unit, enabled: Boolean = true, action: () -> Unit) {
    DropdownMenuItem(text = { Text(stringResource(label)) }, enabled = enabled, onClick = {
        close()
        action()
    })
}
