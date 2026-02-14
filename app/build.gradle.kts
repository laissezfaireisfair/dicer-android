import arrow.optics.plugin.arrowOptics
import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.arrow.optics)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    arrowOptics()
}

configure<ApplicationExtension> {
    namespace = "laiss.dicer.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "laiss.dicer.android"
        minSdk = 28
        targetSdk = 36
        versionCode = 4
        versionName = "0.22"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project.dependencies.platform(libs.arrow.stack))
    implementation(libs.arrow.core)
    implementation(libs.arrow.core.serialization)
    implementation(libs.arrow.fxCoroutines)
    implementation(libs.arrow.optics)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material.icons.core)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.compose.ui.text.google.fonts)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)
    implementation(libs.koin.androidx.workmanager)
    debugImplementation(libs.compose.ui.tooling)
}