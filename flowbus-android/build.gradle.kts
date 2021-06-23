import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.github.dcendents.android-maven")
}

group = "com.github.lwj1994"


tasks.withType(KotlinCompile::class.java).all {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=com.lwjlol.flowbus.InternalFlowBusApi",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
}

android {
    buildFeatures {
        buildConfig = false
    }
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
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
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha02")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation(project(mapOf("path" to ":flowbus")))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
