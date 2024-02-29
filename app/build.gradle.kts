plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinParcelize)
    alias(libs.plugins.googleDevtoolsKsp)
}

android {
    namespace = "com.imp.fluffymood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.imp.fluffymood"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        applicationVariants.all {

            val variant = this
            variant.outputs
                .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                .forEach { output ->
                    val outputFileName = "sample-app-v${versionName}.apk"
                    println("OutputFileName: $outputFileName")
                    output.outputFileName = outputFileName
                }
        }

        buildTypes {

            debug {

                isDebuggable = true
                isShrinkResources = false
                isMinifyEnabled = false
            }

            release {

                isDebuggable = false
                isShrinkResources = false
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        kotlinOptions {
            jvmTarget = "1.8"
        }

        buildFeatures {
            buildConfig = true
        }

        lint {

            abortOnError = false
            checkReleaseBuilds = false
            htmlReport = true
            htmlOutput = file("$project.buildDir/reports/lint/lint.html")
            disable.add("MissingTranslation")
            disable.add("GradleDependency")
            disable.add("VectorPath")
            disable.add("IconMissingDensityFolder")
            disable.add("IconDensities")
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
}

dependencies {

    /** Multi Module */
    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))

    /** Android */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /** WorkManager */
    implementation(libs.androidx.work.runtime.ktx)

    /** Hilt */
    implementation(libs.bundles.hilt)
}