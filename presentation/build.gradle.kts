plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevtoolsKsp)
    id(libs.plugins.daggerHiltAndroidPlugin.get().pluginId)
}

android {
    namespace = "com.imp.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
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
        dataBinding = true
        viewBinding = true
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /** Kakao Map */
    implementation(files("../app/libs/libDaumMapAndroid.jar"))

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

    /** Glide */
    implementation(libs.glide)
    implementation(libs.glide.transformations)

    /** Screen Size */
    implementation(libs.screen.easy)

    /** Splash */
    implementation(libs.androidx.core.splashscreen)

    /** MP Chart */
    implementation(libs.mpandroidchart)

    /** Lottie */
    implementation(libs.lottie)

    /** WorkManager */
    implementation(libs.androidx.work.runtime)

    /** DataStore */
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.datastore.preferences.core)
}