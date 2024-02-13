import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.desktop)
    alias(libs.plugins.detekt)
}

group = "fr.cedriccreusot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(libs.jewel.standalone)
    implementation(libs.jewel.decorated)
    implementation(libs.material.icons.extended)
    implementation(libs.coroutines.core)

    // Detekt
    detektPlugins(libs.detekt.compose.rules)

    // Tests
    testImplementation(libs.kotest)
    testImplementation(libs.turbine)
}

detekt {
    toolVersion = libs.versions.detekt.version.get()

    config.setFrom("$rootDir/config/detekt/detekt.yml")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Komodoro"
            packageVersion = "1.0.0"
        }
    }
}
