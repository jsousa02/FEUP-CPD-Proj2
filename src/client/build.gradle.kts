plugins {
    id("java")
    id("application")
}

version = "1.0-SNAPSHOT"
group = "pt.up.fe.cpd.g13.client"

application {
    mainClass.set("pt.up.fe.cpd.g13.client.Launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

dependencies {
    implementation(project(":shared"))
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}