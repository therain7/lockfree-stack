plugins {
    alias(libs.plugins.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.kandy)
}

application {
    mainClass = "benchmark.MainKt"
}
