plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {

    namespace = "br.com.leandro.fichaleitura"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.com.leandro.fichaleitura"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.mediarouter:mediarouter:1.6.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation ("androidx.activity:activity-ktx:1.8.2")

    //ROOM para acesso ao SQLite.
    implementation ("androidx.room:room-ktx:2.6.1")
    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    androidTestImplementation ("androidx.room:room-testing:2.6.1")
    androidTestImplementation ("android.arch.persistence.room:testing:1.1.1")

    //Hilt para injeção de dependências.
    implementation ("com.google.dagger:hilt-android:2.49")
    //implementation 'androidx.hilt:hilt-work:1.1.0'
    //annotationProcessor 'androidx.hilt:hilt-compiler:1.1.0'
    //kapt 'androidx.hilt:hilt-compiler:1.1.0'
    kapt ("com.google.dagger:hilt-compiler:2.49")

    //Ciclo de vida de componentes.
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.7.0")
    kapt ("androidx.lifecycle:lifecycle-compiler:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-service:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-reactivestreams-ktx:2.7.0")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    //IText para geração do PDF.
    implementation ("com.itextpdf:io:7.0.2")
    implementation ("com.itextpdf:kernel:7.0.2")
    implementation ("com.itextpdf:layout:7.0.2")

    implementation ("com.karumi:dexter:6.2.3")

}