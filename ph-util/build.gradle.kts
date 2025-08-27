plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.methanol)
    implementation(libs.jsoup)
    implementation(libs.guava)
    implementation(libs.fastjson)
}
