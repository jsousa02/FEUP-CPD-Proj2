plugins {
    id("java")
    id("application")
}

version = "1.0-SNAPSHOT"
group = "pt.up.fe.cpd.g13.server"

application {
    mainClass.set("pt.up.fe.cpd.g13.server.Server")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mindrot:jbcrypt:0.4")
    implementation(project(":shared"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
