import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.yarn.YarnTask

plugins {
    base
    id("com.moowork.node").version("1.2.0")
}


// configurations on which versions of Node, Npm, and Yarn the gradle build should use.  Configuration provided by/to
// the gradle node plugin that"s applied above (com.moowork.node)
node {
    version = "8.11.2"
    yarnVersion = "1.8.0"
    npmVersion = "5.6.0"
    download = true
    nodeModulesDir = File("${project.projectDir}")
}

//// define a variable that describes the path to the mounted gateway folder, where we want to put things eventually
val mountedJsDir: String = "$project.parent.projectDir/gateway/src/main/resources/mounted"

// define a gradle task that will install our npm dependencies, extends the YarnTask provided by the node gradle plugin
tasks {

    val yarnPackages by creating(YarnTask::class) {

        // group only used to categorize the task when running "gradle tasks" and viewing the list of possible tasks.
        group = "Web Build tasks"
        description = "Executes 'yarn' at the root of the web/ directory to install npm dependencies for the yarn workspace."
        // which yarn command to execute
       args = listOf("install")

        // set this tasks "inputs" to be any package.json files or yarn.lock files that are found in or below our current
        // folder (project.projectDir).  This lets the build system avoid repeatedly trying to reinstall dependencies
        // which have already been installed.  If changes to package.json or yarn.lock are detected, then it will execute
        // the install task again.
        inputs.files(
            fileTree(project.projectDir).matching {
                include("**/package.json", "**/yarn.lock")
        })

        dependsOn("${project.path}:yarn")
    }
//
//    yarnPackages.configure(closureOf<Task> {
//
//    })



    // define a gradle task that executes an npm script (defined in the package.json).
    val webpack by tasks.creating(NpmTask::class) {
        group = "Web Build Tasks"
        description = "Runs 'npm run build', executing the build script of the project root's package.json"

        // same as running "npm run build" in the ./web/ directory.
        this.setArgs(listOf("run", "build"))

        // we require the installPackages to be done before the npm build (which calls webpack) can run, as we need our dependencies!
        dependsOn(yarnPackages)

            // we should re-run this task on consecutive builds if we detect changes to any non-generated files, so here we
            // define that we wish to have all files -- except those excluded -- as input dependencies for this task.
            inputs.files(
                project.fileTree("packages").matching {
                    exclude("**/node_modules/**", "**/dist/**", "**/.awcache/**")
                }
            )

        // the outputs of this task include where we place the final files for use in the module, as well as the local
        // temporary "dist" folders.  Defining these outputs gives the build enough awareness to avoid running this
        // task if it"s already been executed, the outputs are where they are expected, and there have been no changes to
        // inputs.
        outputs.files(fileTree("${project.parent?.projectDir}/gateway/src/main/resources/mounted"))
    }


    // task to delete the dist folders
    val deleteDistFolders by creating(Delete::class) {
        delete(
            project.fileTree("packages").matching {
               include("**/dist/**", "**/.awcache/**")
            }.files
        )
    }


    // task to delete the output files from the gateway resources folder.
    val deleteGwJs by creating(Delete::class) {
        delete(fileTree(mountedJsDir) {
            include("**/Rad*.js", "**/Rad*.js.map")
        })
    }
    // makes the "built in" clean task execute the deletion tasks
    project.tasks.getByName("clean").dependsOn(deleteDistFolders, deleteGwJs)

    project(":gateway") {
        this.tasks.getByName("processResources").dependsOn(webpack)
    }
}


