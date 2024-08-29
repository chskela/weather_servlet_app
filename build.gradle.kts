plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"

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
    // https://mvnrepository.com/artifact/jakarta.servlet.jsp.jstl/jakarta.servlet.jsp.jstl-api
    implementation(libs.jstl.api)
    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core
    implementation(libs.hibernate.core)
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation(libs.postgresql)
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-slf4j-impl
    implementation(libs.slf4j.log4j12)
    // https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf
    implementation(libs.thymeleaf)

    implementation(libs.serialization)

    implementation(libs.coroutines)

    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/com.h2database/h2
    testImplementation(libs.h2)
    // https://mvnrepository.com/artifact/org.mockito.kotlin/mockito-kotlin
    testImplementation(libs.mockito)
}

tasks.test {
    useJUnitPlatform()
}