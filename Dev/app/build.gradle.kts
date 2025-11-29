import org.gradle.kotlin.dsl.annotationProcessor

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.dev"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.dev"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.material)

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)

    //AWS Storage
    implementation("com.amazonaws:aws-android-sdk-core:2.81.1")
    implementation("com.amazonaws:aws-android-sdk-s3:2.81.1")

    //Image loading
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation(libs.google.firebase.storage)
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.5")

    //QR Code
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")

    implementation("com.google.android.gms:play-services-location:21.3.0")

    //Testing/Requirements
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    //Geolocation
    implementation("com.google.android.gms:play-services-location:21.0.1")


    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}