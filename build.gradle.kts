import java.util.concurrent.TimeUnit
import java.util.stream.Collectors


buildscript {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        jcenter()
        maven {
            name = "snapshots"
            url = uri("http://nexus.inductiveautomation.com/repository/inductiveautomation-snapshots/")
        }

        maven {
            name = "thirdparty"
            url = uri("http://nexus.inductiveautomation.com/repository/inductiveautomation-thirdparty/")
        }

        maven {
            name = "releases"
            url = uri("http://nexus.inductiveautomation.com/repository/inductiveautomation-releases/")
        }
    }

    dependencies {
        classpath("com.inductiveautomation.gradle:ignition-module-plugin:1.2.8")
    }
}

plugins {
    base
    id("ignition-module-plugin").version("1.2.8")  // same version as that used above in buildscript config
}

version = "1.0.0-SNAPSHOT"
group = "org.fakester"


allprojects {
    apply<com.inductiveautomation.gradle.IgnitionModlPlugin>()

    ignitionModule {
        // name of the .modl file to build
        fileName = "RadComponents"

        // what is the name of the "root" gradle project for this module.  In this case, it"s "this", aka, the project
        // specified as <repoPath>/rad-perspective-components/build.gradle
        moduleRoot = "perspective-alpha-example"

        // module xml configuration
        moduleName = "RadComponents"
        moduleId = "org.fakester.radcomponent"
        moduleVersion = "${project.version}"
        moduleDescription = "A module that adds components to the Perspective module."
        requiredIgnitionVersion = "8.0.0"
        requiredFrameworkVersion = "8"
        isFree = true
        license = "license.html"
        moduleDependencies = listOf(
            mapOf("scope" to "G", "moduleId" to "com.inductiveautomation.perspective"),
            mapOf("scope" to "D", "moduleId" to "com.inductiveautomation.perspective")
        )
        // map our projects to the scopes their jars should apply
        projectScopes = listOf(
            mapOf("name" to "gateway", "scope" to "G"),
            mapOf("name" to "designer", "scope" to "D"),
            mapOf("name" to "common", "scope" to "GD")
        )
        hooks = listOf(
            mapOf("scope" to "G", "hookClass" to "org.fakester.gateway.RadGatewayHook"),
            mapOf("scope" to "D", "hookClass" to "org.fakester.designer.RadDesignerHook")
        )
    }
    if (project.plugins.hasPlugin("java")) {
        java {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    // check for new versions of dependencies no more than every minute
    configurations.all {
        resolutionStrategy {
            cacheChangingModulesFor(10, TimeUnit.SECONDS)
        }
    }

    // where should we try to resolve maven artifacts from?
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        jcenter()

        maven {
            name = "teamdev"
            url = uri("http://maven.teamdev.com/repository/products")

        }
        maven {
            name = "snapshots"
            url = uri("http://nexus.inductiveautomation.com/repository/inductiveautomation-snapshots/")
        }

        maven {
            name = "thirdparty"
            url = uri("http://nexus.inductiveautomation.com/repository/inductiveautomation-thirdparty/")
        }

        maven {
            name = "releases"
            url = uri("http://nexus.inductiveautomation.com/repository/inductiveautomation-releases/")
        }
       }
    }


tasks {
    val wrapper by getting(Wrapper::class) {
        gradleVersion = "4.8"
        distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-all.zip"
    }
}
