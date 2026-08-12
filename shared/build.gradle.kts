import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

// --- Gateway config (API Gateway base url + api key) -----------------------
// Read once from the root local.properties (gitignored — see
// local.properties.example) and written into commonMain as a generated Kotlin
// file. This is the multiplatform equivalent of Android's BuildConfig: it
// works identically for the Android target and the iOS/Kotlin-Native target,
// without needing a separate secrets mechanism per platform. Never hardcode
// the real api key anywhere in this file.
//
// Implemented as a proper task class (not an inline `doLast {}` closure) so
// it works with the configuration cache: a closure that references
// `rootProject`/`project` inside its action captures a live Project object,
// which the configuration cache can't serialize. Declaring `localPropertiesFile`
// and `outputDir` as task properties keeps the action itself free of any
// script/project references.
//
// `localPropertiesFile` is `@Internal` (not `@InputFile`) since local.properties
// is expected to sometimes be missing (fresh checkout before it's created) —
// declaring it `@InputFile` would make Gradle fail the task when the file
// doesn't exist yet, instead of falling back to the defaults below.
abstract class GenerateGatewayConfigTask : DefaultTask() {

    @get:Internal
    abstract val localPropertiesFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val props = Properties()
        localPropertiesFile.orNull?.asFile?.takeIf { it.exists() }?.inputStream()?.use {
            props.load(it)
        }

        val baseUrl = props.getProperty("gateway.baseUrl")?.takeIf { it.isNotBlank() }
            ?: "https://api.istore.dev"
        val apiKey = props.getProperty("gateway.apiKey")?.takeIf { it.isNotBlank() } ?: ""

        val packageDir = outputDir.get().asFile
            .resolve("io/github/gustavobarbosab/istore/data/remote/network")
        packageDir.mkdirs()
        packageDir.resolve("GeneratedGatewayConfig.kt").writeText(
            """
            package io.github.gustavobarbosab.istore.data.remote.network

            // Auto-generated at build time from local.properties. Do not edit or commit.
            internal object GeneratedGatewayConfig {
                const val BASE_URL: String = "$baseUrl"
                const val API_KEY: String = "$apiKey"
            }

            """.trimIndent()
        )
    }
}

val gatewayConfigOutputDir = layout.buildDirectory.dir("generated/gatewayConfig/commonMain/kotlin")

val generateGatewayConfig = tasks.register<GenerateGatewayConfigTask>("generateGatewayConfig") {
    localPropertiesFile.set(rootProject.layout.projectDirectory.file("local.properties"))
    outputDir.set(gatewayConfigOutputDir)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
       namespace = "io.github.gustavobarbosab.istore.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }

    sourceSets {
        commonMain {
            // .map on the TaskProvider (not a plain Provider) so Gradle wires an
            // implicit task dependency: anything compiling commonMain will first
            // run generateGatewayConfig to produce this source directory.
            kotlin.srcDir(generateGatewayConfig.map { gatewayConfigOutputDir.get() })
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // navigation
                implementation(libs.navigation.compose)

                // koin - dependency injection
                implementation(libs.koin.compose)
                implementation(libs.koin.viewmodel)
                implementation(libs.koin.navigation)

                // serialization - type-safe navigation routes + Ktor JSON codec
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)

                // Arrow (Ior) - functional error handling for repository return types
                implementation(libs.arrow.core)

                // ktor - HTTP client for the API Gateway (engine provided per-platform below)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
            }
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)

            // ktor engine for Android/JVM
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            // ktor engine for iOS (Kotlin/Native)
            implementation(libs.ktor.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
