plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.android.application)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.booking.perfsuite.app"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.booking.perfsuite.app"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":perfsuite"))

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
}
