
plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // compileOnly is the gradle equivalent to "provided" scope.
    compileOnly("com.inductiveautomation.ignitionsdk:ignition-common:8.0.0-SNAPSHOT")
    compileOnly("com.inductiveautomation.ignitionsdk:perspective-common:8.0.0-SNAPSHOT")

    // this one uses the "shorthand" string notation instead of the map notation above.  Does same exact thing.
    compile("com.google.code.gson:gson:2.8.2")
}
