
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.0-1.0.23"
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.kotlinx.serialization)

    id("com.google.gms.google-services")


}

android {
    namespace = "edu.ucne.skyplanerent"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.ucne.skyplanerent"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures {
        compose = true
    }

}


dependencies {
    //navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

   //Agregado para suar icono de calendarioen Ruta_Viaje_Screen
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

    //room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.benchmark.common)
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")


    implementation("androidx.compose.foundation:foundation:1.5.0")

    //optional
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.compose.material3:material3:1.1.0-alpha02")
    implementation ("androidx.compose.material:material:1.4.0")
    implementation("androidx.compose.material:material:1.3.1") // Usa la última versión


    //Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    ksp("com.google.dagger:hilt-android-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //Firebase
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation(platform("com.google.firebase:firebase-analytics"))



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}