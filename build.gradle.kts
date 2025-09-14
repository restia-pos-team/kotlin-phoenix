//import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)
    id("kmp-feature")
//    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.restia.kotlinphoenix"
version = "1.0.0"

kotlin {
//    jvm()
//    androidTarget()

//    androidTarget {
//        publishLibraryVariants("release")
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }
//    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation("com.squareup.okhttp3:okhttp:4.12.0")
        }
    }
}

android {
    namespace = "io.restia.kotlinphoenix"

    compileSdk = 35

    dependencies {
        implementation(libs.kotlinInject.runtime)

        implementation(libs.ktor.client)
        implementation(libs.ktorfit.lib)
        implementation(libs.lifecycle.runtime.ktx)
        androidTestImplementation(libs.ui.test.junit4)
    }
}
//    compileSdk = libs.versions.android.compileSdk.get().toInt()
//    defaultConfig {
//        minSdk = libs.versions.android.minSdk.get().toInt()
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }

//mavenPublishing {
//    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
//
//    signAllPublications()
//
//    coordinates(group.toString(), "library", version.toString())
//
//    pom {
//        name = "My library"
//        description = "A library."
//        inceptionYear = "2024"
//        url = "https://github.com/kotlin/multiplatform-library-template/"
//        licenses {
//            license {
//                name = "XXX"
//                url = "YYY"
//                distribution = "ZZZ"
//            }
//        }
//        developers {
//            developer {
//                id = "XXX"
//                name = "YYY"
//                url = "ZZZ"
//            }
//        }
//        scm {
//            url = "XXX"
//            connection = "YYY"
//            developerConnection = "ZZZ"
//        }
//    }
//}
