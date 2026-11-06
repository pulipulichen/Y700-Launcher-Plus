package info.pulipuli.y700launcherplus

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

internal data class LauncherLayout(
    val pages: List<List<String>>,
    val dock: List<String> = emptyList(),
    val hidden: Set<String> = emptySet(),
    val columns: Int = 0,
    val showLabels: Boolean = true,
    val defaultPage: Int = 0,
    val drawerColumns: Int = 0
) {
    fun movePage(from: Int, to: Int): LauncherLayout {
        require(from in pages.indices && to in pages.indices)
        val order = pages.indices.toMutableList().apply { add(to, removeAt(from)) }
        return copy(pages = order.map(pages::get), defaultPage = order.indexOf(defaultPage))
    }

    fun removePage(index: Int): LauncherLayout {
        require(pages.size > 1 && index in pages.indices)
        val nextDefault = when {
            defaultPage > index -> defaultPage - 1
            defaultPage == index -> index.coerceAtMost(pages.lastIndex - 1)
            else -> defaultPage
        }
        return copy(pages = pages.filterIndexed { i, _ -> i != index }, defaultPage = nextDefault)
    }

    fun save(preferences: SharedPreferences) {
        preferences.edit().putString("layout_v1", toJson()).apply()
    }

    fun toJson(): String = JSONObject()
            .put("version", 2)
            .put("pages", JSONArray(pages.map { JSONArray(it) }))
            .put("dock", JSONArray(dock))
            .put("hidden", JSONArray(hidden))
            .put("columns", columns)
            .put("showLabels", showLabels)
            .put("defaultPage", defaultPage)
            .put("drawerColumns", drawerColumns)
            .toString()

    companion object {
        const val MAX_PAGES = 12
        const val MAX_DOCK_APPS = 8

        fun load(preferences: SharedPreferences, apps: List<LauncherApp>): LauncherLayout {
            val saved = preferences.getString("layout_v1", null)
            if (saved != null) return fromJson(saved)
            val oldPins = preferences.getStringSet("pinned_packages", null)
            val pins = if (oldPins == null) {
                apps.take(8).map { it.packageName }
            } else {
                oldPins.sortedWith(compareBy<String> { id ->
                    apps.indexOfFirst { it.packageName == id }.takeIf { it >= 0 } ?: Int.MAX_VALUE
                }.thenBy { it })
            }
            return LauncherLayout(pages = listOf(pins)).also { it.save(preferences) }
        }

        fun fromJson(json: String): LauncherLayout {
            val data = JSONObject(json)
            require(!data.has("version") || data.getInt("version") in 1..2)
            val pages = data.getJSONArray("pages")
            require(pages.length() in 1..MAX_PAGES)
            val dock = data.getJSONArray("dock")
            require(dock.length() <= MAX_DOCK_APPS)
            val columns = data.getInt("columns")
            require(columns == 0 || columns in 4..8)
            val defaultPage = data.optInt("defaultPage", 0)
            require(defaultPage in 0 until pages.length())
            val drawerColumns = data.optInt("drawerColumns", 0)
            require(drawerColumns == 0 || drawerColumns in 4..8)
            return LauncherLayout(
                pages = List(pages.length()) { pages.getJSONArray(it).packages() },
                dock = dock.packages(),
                hidden = data.getJSONArray("hidden").packages().toSet(),
                columns = columns,
                showLabels = data.getBoolean("showLabels"),
                defaultPage = defaultPage,
                drawerColumns = drawerColumns
            )
        }

        private fun JSONArray.packages(): List<String> =
            List(length(), ::getString).onEach { require(it.isNotBlank() && it.length <= 255) }.distinct()
    }
}
