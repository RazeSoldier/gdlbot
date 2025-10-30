import org.gradle.kotlin.dsl.named
import org.gradle.jvm.application.tasks.CreateStartScripts

plugins {
    id("application")
}

group = "razesoldier"
version = "1.13.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.overflow)
    implementation(libs.bundles.slf4j)
    implementation(libs.fastjson)
    implementation(libs.discord4j)
    implementation(libs.jsoup)
    implementation(project(":shared"))
    implementation(project(":util"))
    implementation(project(":translation"))
    implementation(project(":ph-util"))
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.named<Jar>("jar") {
    manifest.attributes(mapOf("Manifest-Version" to archiveVersion,  "Main-Class" to "razesoldier.gdlbot.Entry"))
    archiveBaseName.set("gdlbot") // 自定义jar任务创建的jar文件前缀
}

tasks.named<CreateStartScripts>("startScripts") {
    applicationName = "gdlbot" // 自定义installDist任务创建的启动脚本名字
}

application {
    mainClass = "razesoldier.gdlbot.Entry"
    applicationDefaultJvmArgs = listOf("-Dmirai.no-desktop", "-Xmx128m", "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
}

tasks.named<JavaExec>("run") {
    workingDir = parent!!.projectDir // 指定工作目录为根目录
}