plugins {
    java
    id("com.github.ben-manes.versions") version("0.18.0")
}


java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(project(":common"))
    compileOnly("com.inductiveautomation.ignitionsdk:designer-api:8.0.0-SNAPSHOT")
    compileOnly("com.inductiveautomation.ignitionsdk:perspective-common:8.0.0-SNAPSHOT")
    compileOnly("com.inductiveautomation.ignitionsdk:ignition-common:8.0.0-SNAPSHOT")
}


