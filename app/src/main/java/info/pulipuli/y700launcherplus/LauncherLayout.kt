package info.pulipuli.y700launcherplus

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

internal data class LauncherLayout(
    val pages: List<List<String>>,
    val dock: List<String> = emptyList(),
    val hidden: Set<String> = emptySet(),
    val columns: Int = 0,
    val showLabels: Boolean = true
) {
    fun save(preferences: SharedPreferences) {
        preferences.edit().putString("layout_v1", toJson()).apply()
    }

    fun toJson(): String = JSONObject()
            .put("version", 1)
            .put("pages", JSONArray(pages.map { JSONArray(it) }))
            .put("dock", JSONArray(dock))
            .put("hidden", JSONArray(hidden))
            .put("columns", columns)
            .put("showLabels", showLabels)
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
            require(!data.has("version") || data.getInt("version") == 1)
            val pages = data.getJSONArray("pages")
            require(pages.length() in 1..MAX_PAGES)
            val dock = data.getJSONArray("dock")
            require(dock.length() <= MAX_DOCK_APPS)
            val columns = data.getInt("columns")
            require(columns == 0 || columns in 4..8)
            return LauncherLayout(
                pages = List(pages.length()) { pages.getJSONArray(it).packages() },
                dock = dock.packages(),
                hidden = data.getJSONArray("hidden").packages().toSet(),
                columns = columns,
                showLabels = data.getBoolean("showLabels")
            )
        }

        private fun JSONArray.packages(): List<String> =
            List(length(), ::getString).onEach { require(it.isNotBlank() && it.length <= 255) }.distinct()
    }
}
