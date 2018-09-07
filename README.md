# Assignment - TO DO List

## Environment

* Language
    * JDK 1.8
    * javascript
    * html
* IDE
    * Intellij 
* Build Tool
    * Gradle
* Library
    * Common
        * Akka Actor
        * Akka Remote
        * Lombok
        * Jackson
    * Front-end Server
        * Spring Boot Webflux
        * Thyemeleaf Template Engine
        * JQuery
    * Proccess
        * Ignite Indexing
        * Akka test-kit
        
## Getting started

### Installation
#### Download the release files

Download the `front-end.zip` and 'process.zip' in the release page by the version of 2.0-SNAPSHOT. [PageLink](https://github.com/ztkmkoo/k_todo_list/releases)

#### Build with Gradle

First, git Clone the source file. Then in the project Root directory build with gradlew.
``` 
// windows

// build front-end server
gradlew assemble

// build process server
gradlew shadowJar


// linux or mac, similar to windows

// build front-end server
./gradlew assemble

// build process server
./gradlew shadowJar
```

### Execute the Jar File

If you download the release files or build with the  gradle, then in the path(`${ProjectRoot}/front-end/build/libs/` and `${ProjectRoot}/process/build/libs/`) you could find the jar file and runnable linux script and windows batch file.

Default port is 8187 and  the host is the local host. If you want to change it, please modify the script file or execute the jar file with the spring param(`-Dserver.port=8090`).

## Server Structer

