package info.pulipuli.y700launcherplus

import android.content.ContentResolver
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.IOException

private const val MAX_BACKUP_BYTES = 1024 * 1024

internal fun readBackup(resolver: ContentResolver, uri: Uri): LauncherLayout {
    val input = resolver.openInputStream(uri) ?: throw IOException("Backup is unavailable")
    val contents = input.use {
        val buffer = ByteArray(8192)
        val output = ByteArrayOutputStream()
        while (true) {
            val count = it.read(buffer)
            if (count < 0) break
            require(output.size() + count <= MAX_BACKUP_BYTES)
            output.write(buffer, 0, count)
        }
        output.toString(Charsets.UTF_8.name())
    }
    return LauncherLayout.fromJson(contents)
}

internal fun writeBackup(resolver: ContentResolver, uri: Uri, layout: LauncherLayout) {
    val output = resolver.openOutputStream(uri, "wt") ?: throw IOException("Backup destination is unavailable")
    output.use { it.write(layout.toJson().toByteArray(Charsets.UTF_8)) }
}
