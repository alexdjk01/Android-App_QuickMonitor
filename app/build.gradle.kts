plugins {
    id("com.android.application")
}

android {
    namespace = "ro.ase.dma.connectinfluxdb"
    compileSdk = 34

    defaultConfig {
        applicationId = "ro.ase.dma.connectinfluxdb"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions{
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/LICENSE.md")
    }
}

dependencies {


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.work:work-runtime:2.9.0")
    //chart
    implementation ("com.androidplot:androidplot-core:1.5.10")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    //mail service
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
    //update security providers
    implementation ("com.google.android.gms:play-services-auth:21.0.0")


    implementation ("com.airbnb.android:lottie:6.3.0") // lottie for gif in the start page

}