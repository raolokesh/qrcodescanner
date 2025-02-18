plugins {
    alias(libs.plugins.android.library)
//    id("com.android.library")
    `maven-publish`
}

android {
    namespace = "com.lokesh.barcodescanner"
    compileSdk = 34

    defaultConfig {
//        applicationId = "com.lokesh.barcodescanner"
        minSdk = 23
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.barcode.scanning)
    implementation(libs.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.camera.lifecycle)
    // If you want to additionally use the CameraX View class
    implementation(libs.camera.view)
}


//afterEvaluate{
//    publishing {
//        publications {
//            create("release", MavenPublication::class) {
//                groupId = "com.github.raolokesh"
//                artifactId = "barcode-scanner"
//                version = "1.0.0"
//
//                afterEvaluate {
//                    from(components["release"])
//                }
//            }
//        }
//    }
//}
publishing {
    publications {
        create("release", MavenPublication::class) {
            groupId = "com.github.raolokesh"
            artifactId = "barcode-scanner"
            version = "1.0.11"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

//tasks.register<Jar>("sourcesJar") {
//    archiveClassifier.set("sources")
//    from(android.sourceSets["main"].java.srcDirs)
//}
//
//tasks.register<Jar>("javadocJar") {
//    archiveClassifier.set("javadoc")
//    from(tasks["dokkaJavadoc"])
//}
