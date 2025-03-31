plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.viabus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.viabus"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding=true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //for sdp
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    //// lottie animations
    implementation ("com.airbnb.android:lottie:6.3.0")
    ///for  circle image
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    /// bar code scanning
    implementation ("com.google.mlkit:barcode-scanning:17.3.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0") // ZXing for QR Code scanning

    ///razorpay
    implementation ("com.razorpay:checkout:1.6.40")
}