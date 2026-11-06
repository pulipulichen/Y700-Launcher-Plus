package info.pulipuli.y700launcherplus

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.text.Collator

internal data class LauncherApp(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap
)

internal fun loadLaunchableApps(context: Context): List<LauncherApp> {
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
    }.distinctBy { it.packageName }.sortedWith(compareBy(Collator.getInstance()) { it.label })
}

private fun Drawable.toBitmap(): Bitmap {
    val width = intrinsicWidth.coerceIn(1, 144)
    val height = intrinsicHeight.coerceIn(1, 144)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
