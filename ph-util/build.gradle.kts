plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.annotations)
    implementation(libs.methanol)
    implementation(libs.jsoup)
    implementation(libs.guava)
    implementation(libs.fastjson)
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}