plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)  // Ini sudah benar
}

android {
    namespace = "com.cicisigit.projekpmob2"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.cicisigit.projekpmob2"
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("com.google.android.material:material:1.12.0")
    implementation("io.coil-kt:coil:2.7.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // === FIREBASE (INI YANG DIPERBAIKI) ===
    // Gunakan BOM untuk versi seragam + KTX untuk kode Kotlin-friendly
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-database-ktx")
    // Hapus baris ini karena sudah diganti BOM di atas:
    // implementation("com.google.firebase:firebase-database-ktx:21.0.0")
}