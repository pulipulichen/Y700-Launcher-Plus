package info.pulipuli.y700launcherplus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var status by remember { mutableStateOf("Ready to test HOME replacement") }
                Scaffold { padding ->
                    Column(Modifier.padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Y700 Launcher Plus", style = MaterialTheme.typography.headlineMedium)
                        Text("Phase 0 · ZUI taskbar compatibility")
                        Text("Select this app as HOME, then inspect whether the original ZUI taskbar remains visible. No privileged ZUI APIs are used.")
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                            status = if (intent.resolveActivity(packageManager) != null) "HOME handler available" else "No HOME handler found"
                        }) { Text("Check HOME intent") }
                        Text(status)
                    }
                }
            }
        }
    }
}
