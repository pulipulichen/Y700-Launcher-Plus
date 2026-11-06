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
        val data = JSONObject()
            .put("pages", JSONArray(pages.map { JSONArray(it) }))
            .put("dock", JSONArray(dock))
            .put("hidden", JSONArray(hidden))
            .put("columns", columns)
            .put("showLabels", showLabels)
        preferences.edit().putString("layout_v1", data.toString()).apply()
    }

    companion object {
        const val MAX_PAGES = 12
        const val MAX_DOCK_APPS = 8

        fun load(preferences: SharedPreferences, apps: List<LauncherApp>): LauncherLayout {
            val saved = preferences.getString("layout_v1", null)
            if (saved != null) {
                val data = JSONObject(saved)
                val pages = data.getJSONArray("pages")
                return LauncherLayout(
                    pages = List(pages.length()) { pages.getJSONArray(it).packages() },
                    dock = data.getJSONArray("dock").packages(),
                    hidden = data.getJSONArray("hidden").packages().toSet(),
                    columns = data.getInt("columns"),
                    showLabels = data.getBoolean("showLabels")
                )
            }
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

        private fun JSONArray.packages(): List<String> = List(length(), ::getString)
    }
}
