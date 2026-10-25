import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

fun apiUrl(propKey: String, envKey: String): String =
    (localProps.getProperty(propKey) ?: System.getenv(envKey) ?: "").trim()

android {
    namespace = "com.example.aplicacionvinilos"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.aplicacionvinilos"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "backend"
    productFlavors {
        create("local") {
            dimension = "backend"
            buildConfigField(
                "String", "API_BASE_URL",
                "\"${apiUrl("api.url.local", "VINYLS_API_URL_LOCAL")}\""
            )
        }
        create("remote") {
            dimension = "backend"
            buildConfigField(
                "String", "API_BASE_URL",
                "\"${apiUrl("api.url.public", "VINYLS_API_URL_PUBLIC")}\""
            )
        }
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.volley)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

gradle.taskGraph.whenReady {
    val names = allTasks.filter { it.project == project }.map { it.name.lowercase() }
    if (names.any { it.contains("local") } && apiUrl("api.url.local", "VINYLS_API_URL_LOCAL").isEmpty() ||
        names.any { it.contains("remote") } && apiUrl("api.url.public", "VINYLS_API_URL_PUBLIC").isEmpty()
    ) {
        throw GradleException("Missing API URL. See local.properties.example")
    }
}
