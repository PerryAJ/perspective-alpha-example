# Perspective Component Module Example 

This is a simple example module which adds components to the Perspective module's set of components.

## Quick Tool Overview

This project uses a number of build tools in order to complete the various parts of its assembly.  Namely:

* Gradle - the primary build tool. Most tasks executed in a typical workflow are gradle tasks.  
* lerna.js - is a javascript build-orchestration tool.  It allows us to have independent 'modules' and 'packages' in 
the same git/hg repository without having to do a lot of complicated symlinking/publishing to pull in changes from one
 project to another.
* yarn - is a javascript dependency (package) manager that provides a number of improvements over npm, though it shares
 much of the same commands and api.  Much like Ivy or Maven, yarn is used to resolve and download dependencies hosted
 on repositories.  Inductive Automation publishes our own dependencies through the same nexus repo we use for other sdk
 artifacts.  To correctly resolve them, an `.npmrc` file needs to be added to the front end projects to tell
 yarn/npm where to find packages in the `@inductiveautomation` namespace.  
* Typescript - the language used to write the front end parts.  Typescript can be thought of as javascript with types
 added (though this is a simplification), giving better runtime safety and, most importantly, much better tooling
  support to improve maintainability, refactoring, code navigation, etc.  Typescript has its own compiler which emits
  javascript.  This compiler is frequently paired with other build tools in a way that it emits the javascript, but
  other tools handle the actual bundling of assets, css, and other supporting dependencies.  Think of typescript as the
  java compiler without jar bundling.  It just takes typescript files in, and emits the javascript files. 
* Webpack - the 'bundler' that we use to take the javascript emitted by the typescript compiler and turn it into an
  actual package that includes necessary dependencies, minifies production builds, adds sourcemaps, etc.
* tslint - a 'linter', which basically is a configurable code-format checker.  Tslinter acts much like checkstyle for
  java.  You configure the code formatting, and the linter will complain if your code doesn't fit the settings.
  
  
More documentation incoming, and the web/README.md contains a lot of information about the typescript build process.

## Getting Started

This is a quick-start set of requirements/commands to get this project built.

### Requirements


1. Need npm/node installed, which will allow the installation of yarn, typescript, webpack, etc.  MacOs and Linux can
install via package managers, or they and Windows can be installed via the downloads at the 
[NodeJS Website](https://nodejs.org/).   We recommend sticking with the LTS versions (actual versions used by the build)
can be seen in the `./web/build.gradle.kts` file, within the `node` configuration block.

2. With npm installed, install the global dev-dependency tools.  While it's possible to make gradle handle all these,
it's useful to have them installed locally to speed build times and run local checks and commands without gradle.  In
general, you want these to be the same (or very close) version as those defined in your package.json files.  
    1. `npm install -g typescript`
    2. `npm install -g webpack@3.10.1`   // or whatever version you want
    3. `npm install -g tslint`
    4. `npm install -g lerna`
    5. `npm install -g yarn`
    
3. Gradle - gradle does not need to be installed if commands are executed through the gradle wrapper (see 
[Gradle Wrapper Docs](https://docs.gradle.org/current/userguide/gradle_wrapper.html) for details).


Quick Note:  This example is built using a custom gradle module plugin.  This plugin was originally intended for 
internal use and, as a result, it makes some assumptions about project structure and dependencies.  A full, open-source
 and public gradle plugin is in progress and will be published publicly in late June/Early July timeline.  The new 
 plugin contains updates to support newer gradle features, improved build times, a simpler configuration API, 
 and will be useable in both groovy and kotlinscript gradle buildscript files.
 
### Project structure & Layout

This example module has a fairly traditional layout with one key difference.  It has a `common` subproject which is 
shared between gateway and designer scopes.  What it does NOT have is a `client` scope.  Instead we have a `web` 
subproject which contains the source code, assets, and build configuration used to build the html/js/etc.  

Within the `web` directory is a _lerna workspace_, which is simply a javascript corollary to a 'maven multi-module
 project',  or a 'multi-project gradle build'.  Meaning, there are more than one 'build' configured.  We have stuck 
 with the lerna default of using `packages` directory that has our two builds - one targeting a perspective client
 at runtime in a browser, and a second targeting perspective in the designer.  As in Vision, the perspective designer
 scoped assets frequently extend the perspective client scoped assets (again, remembering that this is client in the
  context of the web, meaning executing in a web browser.  Nothing to do with _vision clients_).  Ultimately the output
  of both web/ packages ends up in our `gateway` scoped java project as resources, as the gateway is where they get
  served from.  That they are named `client` or `designer` is unimportant.  They could be `browser` and `designer` or
  whatever you choose.  The important part is making sure the files are appropriately registered in the appropriate
  registries. 
  
  ```
  ├── build.gradle.kts                     // root build configuration, like a root pom.xml file
  ├── common                                
  │   ├── build.gradle.kts                 // configuration for common scoped build
  │   └── src
  │       └── main/java                    // where source files live
  ├── designer
  │   ├── build.gradle.kts
  │   └── src
  │       └── main/java
  ├── gateway
  │   ├── build.gradle.kts
  │   └── src
  │       └── main/java
  ├── gradle                              // gradle wrapper assets to allow wrapper functionality - should be commited
  │   └── wrapper
  │       ├── gradle-wrapper.jar
  │       └── gradle-wrapper.properties
  ├── gradlew                             // gradle build script for linux/osx
  ├── gradlew.bat                         // gradle build script for windows
  ├── settings.gradle.kts                 // Gradle project structure/global configuration.
  └── web                                 // parent directory for the web assets we build
      ├── README.md
      ├── build.gradle.kts
      ├── lerna.json                      // lerna configuration file
      │
      ├── package.json                    
      ├── packages
      │   ├── client
      │   │    ├── package.json
      │   │    ├── webpack.config.json     // webpack build configuration
      │   │    └── typescript/             // typescript source files
      │   │
      │   └── designer
      │        ├── package.json
      │        ├── webpack.config.json     // webpack build configuration
      │        └── typescript/             // typescript source files
      └── yarn.lock                        // lock file describes the dependencies/versions of front-end resources

```
 
### Building

Building this module requires a couple one-time setup commands, and then the build command which is repeated as needed.
 
1.  First, we want to link up our front end resources, so in the command prompt, navigate to the `web` folder and run
`lerna bootstrap`, which will configure the sub-projects so that `web/packages/designer` can depend on 
`web/packages/client`, even though it's not a published package in a repository.  This command should be re-run when
a project is checked out for the first time, or whenever versions have changed for the front end packages (as in 
the top level `package.json` version property, not versions of dependencies or dev-dependencies)

2. Run `yarn` in the `web` directory.  This downloads the necessary dependencies for our front end assets.  This is 
also done automatically by the gradle build, but we do it here initially as a sanity check.

3. Navigate back to the root of the project, and in the terminal execute `./gradlew build` (linux/osx), or 
`gradle.bat build` (windows).  This will result in the appropriate gradle binaries being downloaded (match the version
 and info provided by our `wrapper` task and committed `gradle/` directory).  This will compile and assemble all jars,
 as well as execute the webpack.
 
 All three steps above are typically executed as part of the command `./gradlew buildSignedModule`, which is the 
 main task that creates a .modl file and signs it.
 
 
 ### Configuring/Customizing
 
 The gradle module plugin configuration should be understandable looking at the root build.gradle.kts.  Important
 things to note with this:
 
 1. Any java dependencies you want to include in your module should be defined with the `toModl` configuration, rather
 than `compile`, `compileOnly` (or `implementation` or `api` if looking at most recent gradle docs).  This should
 be defined in the appropriate scope.
 

