plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.room")
}



android {
    namespace = "com.openclassrooms.p8vitesse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.openclassrooms.p8vitesse"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        kapt {
            arguments {
                arg("room.incremental", "true")
            }
        }
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

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

}


dependencies {

    //Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    testImplementation("junit:junit:4.13.2")
    implementation("org.mockito:mockito-core:5.12.0")
    //Tests de routines avec des Mocks
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0-RC2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion") // TODO : Warning:(58, 5) This library supports using KSP instead of kapt, which greatly improves performance. Learn more: https://developer.android.com/studio/build/migrate-to-ksp

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    // Moshi JSON Library
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}

kapt {
    correctErrorTypes = true
}