plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.annotations)
    implementation(libs.fastjson)
    implementation(libs.bundles.reactorNetty)
    implementation(project(":shared"))
}
