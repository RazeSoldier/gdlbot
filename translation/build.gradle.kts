plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.annotations)
    implementation(libs.guice.core)
    implementation(libs.guice.ext.throwingproviders)
    implementation(libs.tencentcloud.tmt.sdk)
    implementation(libs.evo.inflector)
    implementation(libs.fastjson)
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}