plugins {
    id("com.android.application") version "8.11.0"
    id("org.jetbrains.kotlin.android") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

android {
    namespace = "ru.steps"
    compileSdk = 35
    defaultConfig {
        minSdk = 25
        targetSdk = 35
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {
    implementation("androidx.work:work-runtime-ktx:2.10.2")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("io.ktor:ktor-client-android:3.2.3")
    implementation("io.ktor:ktor-client-cio:3.2.3")
    implementation("io.ktor:ktor-client-websockets:3.2.3")
    implementation("androidx.compose.ui:ui:1.8.3")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.runtime:runtime:1.8.3")
    implementation("androidx.compose.foundation:foundation:1.8.3")
    implementation("androidx.compose.animation:animation:1.8.3")
    implementation("androidx.compose.material:material:1.8.3")
    implementation("androidx.compose.material3:material3:1.3.2")
}
