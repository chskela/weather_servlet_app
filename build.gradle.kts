plugins {
    kotlin("jvm") version "1.9.23"
    war
}

group = "com.chskela"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/jakarta.servlet/jakarta.servlet-api
    compileOnly(libs.jakarta.servlet.api)
    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core
    implementation(libs.hibernate.core)
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation(libs.postgresql)
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-slf4j-impl
    implementation(libs.slf4j.log4j12)
    // https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf
    implementation(libs.thymeleaf)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}