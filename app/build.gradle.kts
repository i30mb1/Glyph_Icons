import com.github.triplet.gradle.androidpublisher.ReleaseStatus

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.play.publisher)
}

// Google Play publishing: straight to production. Store listing graphics and release notes live in src/main/play/.
play {
    serviceAccountCredentials.set(rootProject.file("play-service-account.json"))
    defaultToAppBundles.set(true)
    track.set("production")
    releaseStatus.set(ReleaseStatus.COMPLETED)
}

android {
    namespace = "n7.glyphicons"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "n7.glyphicons"
        minSdk = 35
        targetSdk = 37
        versionCode = 7
        versionName = "4.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Upload key from ~/.gradle/gradle.properties (n7.upload.*, or the shared kcal.upload.* key).
    // Without it, release builds fall back to the debug key so a fresh clone still builds.
    fun uploadProp(name: String): String? =
        providers.gradleProperty("n7.upload.$name").orNull ?: providers.gradleProperty("kcal.upload.$name").orNull
    val uploadStore = uploadProp("storeFile")
    signingConfigs {
        if (uploadStore != null) {
            create("upload") {
                storeFile = file(uploadStore)
                storePassword = uploadProp("storePassword")
                keyAlias = uploadProp("keyAlias")
                keyPassword = uploadProp("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            // R8: shrinks, optimizes and renames the code and drops unused resources.
            // Keep rules: every file in src/main/keepRules/.
            optimization {
                enable = true
            }
            signingConfig = signingConfigs.findByName("upload") ?: signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation(files("../libs/glyph-matrix-sdk-2.0.aar"))
    implementation(libs.n7.glyph.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
