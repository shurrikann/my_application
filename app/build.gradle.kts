import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
// 添加这一行来启用序列化插件
    alias(libs.plugins.kotlin.serialization)
}

val buildTime = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())

android {
    namespace = "com.shurrikann.myapplication"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.shurrikann.myapplication"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    composeOptions {
        // 2. 指定 Kotlin 编译器扩展的版本（建议与你的 Kotlin 版本匹配）
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("com.tencent:mmkv:1.3.9")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.google.gson)
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    // 2. uCrop 裁剪库 (核心)
    implementation("com.github.yalantis:ucrop:2.2.11")
    // 需要额外添加转换器
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Jake Wharton 的转换器
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    // Kotlinx Serialization 核心库
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // OkHttp (Retrofit 的基石)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Ktor 是插件化的，需要什么功能装什么
    implementation("io.ktor:ktor-client-android:3.0.0") // 引擎
    implementation("io.ktor:ktor-client-content-negotiation:3.0.0") // 序列化插件
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
    // 3. 引入 Compose 核心库
    val composeBom = platform("androidx.compose:compose-bom:2024.04.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3:1.3.0") // 使用你熟悉的 Material 3
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
}