plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.projectsos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projectsos"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // AndroidX and Material dependencies
    implementation(libs.appcompat.v161) // Assuming libs.appcompat
    implementation(libs.material) // Assuming libs.material
    implementation(libs.activity.ktx.v172) // Assuming libs.activity
    implementation(libs.constraintlayout.v220) // Assuming libs.constraintlayout

    // Google Play Services for location
    implementation(libs.play.services.location) // Assuming libs.play.services.location

    // Room database dependencies
    implementation(libs.room.runtime.v250)
    annotationProcessor(libs.room.compiler.v250) // Room annotation processor

    // Android Image Cropper dependency
    //noinspection GradleDynamicVersion

    // Preference dependency for settings
    implementation(libs.preference.ktx) // Or latest version

    // Unit testing dependencies
    testImplementation(libs.junit)
    implementation("com.github.bumptech.glide:glide:4.15.1") // Latest version as of now
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")// Assuming libs.junit
    //noinspection UseTomlInstead

    // Android UI testing dependencies
    androidTestImplementation(libs.ext.junit) // Assuming libs.ext.junit
    androidTestImplementation(libs.espresso.core) // Assuming libs.espresso.core
}
