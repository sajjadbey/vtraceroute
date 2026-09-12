plugins {
    id("java")
    application
    id("com.gradleup.shadow") version "9.6.1"
}

group = "com.arazcode"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.arazcode.vtraceroute.Main")
}

sourceSets {
    main {
        resources {
            srcDir("databases")
        }
    }
}

tasks.shadowJar {
    archiveBaseName.set("vtraceroute")
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.maxmind.geoip2:geoip2:5.0.0")
}

tasks.test {
    useJUnitPlatform()
}