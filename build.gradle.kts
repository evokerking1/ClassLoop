plugins {
    java
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.classloop"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.h2database:h2")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Task to install frontend dependencies
tasks.register<Exec>("npmInstall") {
    workingDir = file("frontend")
    commandLine("npm", "install", "--legacy-peer-deps")
}

// Task to build frontend
tasks.register<Exec>("npmBuild") {
    dependsOn("npmInstall")
    workingDir = file("frontend")
    commandLine("npm", "run", "build")
}

// Task to copy frontend build to static resources
tasks.register<Copy>("copyFrontend") {
    dependsOn("npmBuild")
    from("frontend/out")
    into("src/main/resources/static/app")
}

// Make processResources depend on copyFrontend
tasks.named("processResources") {
    dependsOn("copyFrontend")
}

// Task to build everything and create executable JAR
tasks.register("buildJar") {
    dependsOn("bootJar")
    group = "build"
    description = "Builds the frontend and packages everything into an executable JAR"
}
