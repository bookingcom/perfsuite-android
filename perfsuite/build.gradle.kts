import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.android.library)
    id("com.vanniktech.maven.publish")
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
    implementation(libs.androidx.fragment)
}

// publishing

val libName = "PerfSuite"
val libDescription = "Lightweight library for collecting app performance metrics"
val libUrl = "https://github.com/bookingcom/perfsuite-android"


val groupId = project.group as String
val artifactId = project.name as String
val version = project.version as String

@Suppress("UnstableApiUsage")
mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT, true)
    signAllPublications()

    coordinates(groupId, artifactId, version)

    pom {
        name.set(libName)
        description.set(libDescription)
        url.set(libUrl)

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        scm {
            connection.set("scm:git:github.com/bookingcom/perfsuite-android.git")
            developerConnection.set("scm:git:ssh://github.com/bookingcom/perfsuite-android.git")
            url.set("https://github.com/bookingcom/perfsuite-android")
        }
        developers {
            developer {
                name.set("Vadim Chepovskii")
                email.set("smbduknow@gmail.com")
            }
        }
    }
}
