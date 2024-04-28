import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevtoolsKsp)
    id(libs.plugins.daggerHiltAndroidPlugin.get().pluginId)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {

    namespace = "com.imp.data"
    compileSdk = 34

    defaultConfig {

        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "KAKAO_REST_API_KEY", properties["kakao_rest_api_key"].toString())
        buildConfigField("String", "SERVICE_SERVER_HOST", properties["service_server_host"].toString())
        buildConfigField("String","DEV_SERVER_HOST", properties["dev_server_host"].toString())
        buildConfigField("String","CHAT_SERVER_HOST", properties["chat_server_host"].toString())
    }

    buildTypes {

        debug {

            isShrinkResources = false
            isMinifyEnabled = false
        }

        release {

            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {

        resources.excludes.add("META-INF/*")
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/ASL2.0")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/services/javax.annotation.processing.Processor")
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }
}

dependencies {

    /** Multi Module */
    implementation(project(":domain"))

    /** Android */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /** Hilt */
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    /** lifeCycle */
    implementation(libs.bundles.lifecycle)

    /** RxJava3 */
    implementation(libs.bundles.rxjava3)

    /** RxJava2 */
    implementation(libs.bundles.rxjava2)

    /** Coroutine Core */
    implementation(libs.bundles.coroutine)

    /** Retrofit */
    implementation(libs.bundles.retrofit)

    /** OkHttp */
    implementation(libs.bundles.okhttp)

    /** moshi */
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin.codegen)

    /** Gson */
    implementation(libs.gson)

    /** WorkManager */
    implementation(libs.androidx.work.runtime)

    /** DataStore */
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.datastore.preferences.core)

    /** GPS */
    implementation(libs.play.services.location)
}