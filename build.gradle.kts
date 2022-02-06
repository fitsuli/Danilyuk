import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0-beta01")
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
        classpath(kotlin("serialization", version = "1.6.10"))
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://jitpack.io") }
    }
}

subprojects {
    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-Xjvm-default=all", "-Xopt-in=kotlin.RequiresOptIn", "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
                )
                jvmTarget = "11"
            }
        }
    }
}

