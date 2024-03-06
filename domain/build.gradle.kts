plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevtoolsKsp)
    id(libs.plugins.daggerHiltAndroidPlugin.get().pluginId)
}

android {
    namespace = "com.imp.domain"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    /** Hilt */
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}