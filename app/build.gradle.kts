

plugins {

    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")

}

android {
    namespace = "com.myapp.greetingcard"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.myapp.greetingcard"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    ksp {

        arg("room.schemaLocation", "$projectDir/schemas")

    }

    buildTypes {

        release {

            isMinifyEnabled = false

            isDebuggable = false

            proguardFiles(

                getDefaultProguardFile("proguard-android-optimize.txt"),

                "proguard-rules.pro"

            )

        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {

        unitTests {

            isIncludeAndroidResources = true

        }

    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    androidResources{
        generateLocaleConfig = true
    }
}
configurations {

    create("cleanedAnnotations")

    implementation {

//        //exclude(group = "org.jetbrains", module = "annotations")

        exclude(group = "com.intellij", module = "annotations")

    }

}

dependencies {

    implementation(libs.androidx.material.icons.extended) // Add this line
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.testing)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.compose.ui)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    ksp(libs.androidx.room.compiler)
    // Needed for createComposeRule(), but not for createAndroidComposeRule<YourActivity>():
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.9.3")
// Needed for createComposeRule(), but not for createAndroidComposeRule<YourActivity>():
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.9.3")
}
// Test rules and transitive dependencies:
