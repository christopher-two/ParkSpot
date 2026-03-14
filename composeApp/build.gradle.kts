import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.androidx.room)
    kotlin("plugin.serialization") version "2.3.10"
}

kotlin {
    androidLibrary {
        namespace = "org.christophertwo.car"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            // Koin
            api(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.android)

            api(project.dependencies.platform(libs.ktor.bom))
            api(libs.ktor.client.cio)

            api(libs.androidx.room.sqlite.wrapper)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)

            implementation(libs.coil.okhttp)

            implementation(libs.android.ndk27)
            implementation(libs.maps.compose.ndk27)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Data
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            // Navigation 3
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.material3.adaptiveNavigation3)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)

            // Koin
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose.navigation3)
            implementation(libs.koin.annotations)

            // Kotlin
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            // Ui Utils
            implementation(libs.material.icons.core)
            implementation(libs.material.kolor)
            implementation(libs.qrose)
            implementation(libs.font.awesome)

            // Coil3 — carga de imágenes
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // CameraK
            implementation(libs.camerak)
        }
        iosMain.dependencies {
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    listOf(
        "kspAndroid",
        "kspIosSimulatorArm64",
        "kspIosArm64"
    ).forEach {
        add(it, libs.androidx.room.compiler)
    }

    commonMainApi("dev.icerock.moko:permissions:0.20.1")
    commonMainImplementation("dev.icerock.moko:permissions-location:0.20.1")
    commonMainApi("dev.icerock.moko:permissions-compose:0.20.1")
}

room {
    schemaDirectory("$projectDir/schemas")
}

koinCompiler {
    userLogs = true
    debugLogs = false
    dslSafetyChecks = true
}
