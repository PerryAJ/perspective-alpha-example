
plugins {
    java
    id("com.github.ben-manes.versions") version("0.18.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    //we have a compile dependency on the common project
    compileOnly(project(":common"))

    compileOnly ("com.inductiveautomation.ignitionsdk:ignition-common:8.0.0-SNAPSHOT")
    compileOnly ("com.inductiveautomation.ignitionsdk:gateway-api:8.0.0-SNAPSHOT")
    compileOnly ("com.inductiveautomation.ignitionsdk:perspective-gateway:8.0.0-SNAPSHOT")
}
