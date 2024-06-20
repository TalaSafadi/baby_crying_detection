plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.baby_cry_identfication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.baby_cry_identfication"
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

    packagingOptions {
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation("de.gerdi-project:GSON:6.0.6")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation ("com.google.firebase:firebase-firestore:25.0.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.0.3")
    implementation("com.sun.mail:android-mail:1.6.7") {
        exclude(group = "javax.activation", module = "activation")
    }
    implementation("com.sun.mail:android-activation:1.6.7") {
        exclude(group = "javax.activation", module = "activation")

    }
    implementation ("com.google.api-client:google-api-client:1.32.1")
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.36.0")
    implementation ("com.google.http-client:google-http-client-jackson2:1.40.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:4.9.3")


}
