import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val signingProps = Properties().apply {
    val file = rootProject.file("signing/signing.properties")
    if (file.isFile) file.inputStream().use { load(it) }
}
val releaseStore = providers.environmentVariable("ANDROID_KEYSTORE_PATH").orNull
    ?.let { file(it) } ?: rootProject.file("signing/release.keystore")
val releaseStorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
    ?: signingProps.getProperty("storePassword")
val releaseKeyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull
    ?: signingProps.getProperty("keyPassword")
val releaseAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
    ?: signingProps.getProperty("keyAlias")
val permanentSigning = releaseStore.isFile &&
    listOf(releaseStorePassword, releaseKeyPassword, releaseAlias).all { !it.isNullOrBlank() }

android {
    namespace = "info.pulipuli.y700launcherplus"
    compileSdk = 36
    defaultConfig {
        applicationId = "info.pulipuli.y700launcherplus"
        minSdk = 29
        targetSdk = 35
        versionCode = providers.gradleProperty("appVersionCode").orNull?.toInt() ?: 3
        versionName = providers.gradleProperty("appVersionName").orNull ?: "0.2.1"
    }
    signingConfigs {
        create("permanent") {
            if (permanentSigning) {
                storeFile = releaseStore
                storeType = "PKCS12"
                storePassword = releaseStorePassword
                keyPassword = releaseKeyPassword
                keyAlias = releaseAlias
            }
        }
        getByName("debug") {
            if (!permanentSigning) {
                providers.environmentVariable("Y700_TEST_KEYSTORE_PATH").orNull?.let { path ->
                    storeFile = file(path)
                    storeType = "PKCS12"
                    storePassword = "android"
                    keyAlias = "androiddebugkey"
                    keyPassword = "android"
                }
            }
        }
    }
    buildTypes {
        getByName("debug") {
            if (permanentSigning) signingConfig = signingConfigs.getByName("permanent")
        }
        getByName("release") {
            if (permanentSigning) signingConfig = signingConfigs.getByName("permanent")
        }
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.10.01"))
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
}
