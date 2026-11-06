plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android {
 namespace = "info.pulipuli.y700launcherplus"
 compileSdk = 36
 defaultConfig { applicationId = "info.pulipuli.y700launcherplus"; minSdk = 29; targetSdk = 35; versionCode = 3; versionName = "0.2.1" }
 // GitHub Actions supplies a stable, temporary key for sideloaded debug builds.
 val testKeystore = providers.environmentVariable("Y700_TEST_KEYSTORE_PATH")
 signingConfigs.getByName("debug") {
  testKeystore.orNull?.let { path ->
   storeFile = file(path)
   storeType = "PKCS12"
   storePassword = "android"
   keyAlias = "androiddebugkey"
   keyPassword = "android"
  }
 }
 buildFeatures { compose = true }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
}
dependencies {
 implementation(platform("androidx.compose:compose-bom:2025.10.01"))
 implementation("androidx.activity:activity-compose:1.11.0")
 implementation("androidx.compose.material3:material3")
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.foundation:foundation")
}
