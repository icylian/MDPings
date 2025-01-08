plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sekusarisu.mdpings"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sekusarisu.mdpings"
        minSdk = 26
        targetSdk = 35
        versionCode = 13
        versionName = "1.1.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEYSTORE_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material3.adaptive.navigation)

    // Preferences DataStore (SharedPreferences like APIs)
    dependencies {
        implementation(libs.androidx.datastore.preferences)
        // optional - RxJava2 support
        implementation(libs.androidx.datastore.preferences.rxjava2)
        // optional - RxJava3 support
        implementation(libs.androidx.datastore.preferences.rxjava3)
    }
    // Alternatively - use the following artifact without an Android dependency.
    dependencies {
        implementation(libs.androidx.datastore.preferences.core)
    }

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jetbrains.kotlinx.collections.immutable)

    implementation(libs.androidx.foundation)

    implementation(libs.jline.terminal)
    implementation(libs.jline.reader)

    dependencies {
        val vico_version = "2.0.0-beta.2"
        // For Jetpack Compose.
        implementation("com.patrykandpatrick.vico:compose:$vico_version")
        // For `compose`. Creates a `ChartStyle` based on an M2 Material Theme.
        implementation("com.patrykandpatrick.vico:compose-m2:$vico_version")
        // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
        implementation("com.patrykandpatrick.vico:compose-m3:$vico_version")
        // Houses the core logic for charts and other elements. Included in all other modules.
        implementation("com.patrykandpatrick.vico:core:$vico_version")
        // For the view system.
        implementation("com.patrykandpatrick.vico:views:$vico_version")
    }

    dependencies {
        val nav_version = "2.8.5"
        // Jetpack Compose integration
        implementation("androidx.navigation:navigation-compose:$nav_version")
        // Views/Fragments integration
        implementation("androidx.navigation:navigation-fragment:$nav_version")
        implementation("androidx.navigation:navigation-ui:$nav_version")
        // Feature module support for Fragments
        implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")
        // Testing Navigation
        androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")
    }

    implementation(libs.androidx.material3.windowsizeclass)

    implementation (libs.androidx.appcompat)
    implementation (libs.androidx.core)


    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tasks.register("printVersionName") {
    doLast {
        println("v" + android.defaultConfig.versionName)
    }
}