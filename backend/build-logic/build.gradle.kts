plugins {
    kotlin("jvm") version "2.3.20"
    `java-gradle-plugin`
}

group = "com.lisovskyi"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        create("todoPlugin") {
            id = "com.wteam.todo-plugin"
            implementationClass = "com.wteam.plugins.TodoPlugin"
        }
    }
}

kotlin {
    jvmToolchain(24)
}