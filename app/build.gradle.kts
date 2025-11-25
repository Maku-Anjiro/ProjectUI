

plugins {
    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)

}

android {
    namespace = "com.example.uidesign"
    compileSdk = 36

    buildFeatures{
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.example.uidesign"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    kotlinOptions {
        jvmTarget = "11"
    }


    buildTypes {
        debug{
            buildConfigField("boolean", "IS_DEBUG", "true")
        }
        release {

            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // For API Handling
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.opencsv:opencsv:5.9")

    // Firebase BoM for version management
    implementation (platform("com.google.firebase:firebase-bom:34.6.0"))

    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth")

    // Credential Manager and Google ID
    implementation ("androidx.credentials:credentials:1.3.0")
    implementation ("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")




    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)




}
