plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.secrets.gradle.plugin)
  alias(libs.plugins.ksp)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

android {
  namespace = "com.adyen.android.assignment"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.adyen.android.assignment"
    minSdk = 30
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    buildConfigField("String", "FOURSQUARE_BASE_URL", "\"https://api.foursquare.com/v3/\"")

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

secrets {
  propertiesFileName = "secrets.properties"
  defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.coil.compose)
  implementation(libs.coil.network.okhttp)
  implementation(libs.play.services.location)

  implementation(libs.retrofit)
  implementation(libs.converter.moshi)
  implementation(libs.maps.compose)

  ksp(libs.moshi.kotlin.codegen)
  ksp(libs.androidx.room.compiler)

  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.kotlinx.coroutines.test)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
