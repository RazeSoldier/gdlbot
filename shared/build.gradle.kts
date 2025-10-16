plugins {
    id("java-library")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.annotations)
    api(libs.lightbend.config)
}
