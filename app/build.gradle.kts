plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "cl.duoc.appcorpaliv"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.appcorpaliv"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // reporte de cobertura de las pruebas unitarias
            enableUnitTestCoverage = true
        }
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3.windowsize)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")


    implementation("androidx.navigation:navigation-compose:2.10.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.datastore:datastore-preferences:1.1.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // conexion con el microservicio de registros
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // CameraX: cámara propia dentro de la app
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")

    // Pruebas unitarias (Guía 15)
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    // MockK
    testImplementation("io.mockk:mockk:1.13.10")
    // Corrutinas en pruebas (runTest, Dispatchers.setMain)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//    implementation(libs.androidx.navigation.runtime.android)
//    implementation(libs.androidx.navigation. compose. jvmstubs)
//    implementation(libs.androidx.compose.material3.window.size.class1.android)

}

// Obligatorio para usar JUnit 5 (Guía 15)
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}