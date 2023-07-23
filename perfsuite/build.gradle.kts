plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(17)
    explicitApi()
}

android {
    namespace = "com.booking.perfsuite"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.ktx)
}
