# GPhoto2 Server

## Versions 1.0+
This is a Jetty-powered WebApp that serves as a web-interface for GPhoto2 program.
It can be put on any machine that has libgphoto2 and Java 1.5 or above. The calls to GPhoto2 are done using JNA, via https://github.com/mvmn/jlibgphoto2

Command-line arguments:
- port <port> - TCP port to listen on. 
Optional. Default port is 8080.
- logLevel <level> - set logging level (one of TRACE, DEBUG, INFO, WARN, ERROR, SEVERE, FATAL). 
Optional. Default is INFO.
- auth <username:password>
Optional. Enables basic HTTP authentication with provided credentials.
- imgfolder <path>
Optional. Path to store captured images.
- autoconnect
Optional. If present, GPhoto2Server will try to automatically connect to first available camera on startup.

Java System properties:
- -Djna.library.path=/path/to/folder/with/libgphoto2.dylib/or/such - in case you get errors like "Unable to load library 'gphoto2': Native library (darwin/libgphoto2.dylib) not found in resource path" you can specify a path to folder that contains gphoto2 library folder. 
The library path depends on your platform and method of installation (e.g. on OS X you can install gphoto2 library via Homebrew, and use custom folder for all Homebrew installations - then you would have to provide a path like ~/homebrew/lib, given that ~/homebrew is your custom folder for Homebrew installations).

Build dependencies:
- LibGPhoto2 - http://www.gphoto.org
- https://github.com/mvmn/jlibgphoto2 with transitive dependency https://github.com/mvmn/gphoto2-jna
- JDK 8+
- Maven 3.3+

Runtime dependencies:
- LibGPhoto2 - http://www.gphoto.org
- Java 8+ (use Java5 compatibility builds if you need to run on earlier java versions - see in releases).

Example GPhoto2Server script for day-to-night timelapse shooting (on Canon 600D)
- https://gist.github.com/mvmn/e54f0336cfca7de5b24a632266a9f3d6

Known issues: 
On Canon (and possibly other) cameras browsing files requires viewfinder to be closed. Thus one has to close viewfinder (which gets opened by LiveView) before going to browse page.
I will try to address this issue in the future. 

## Versions before 1.0
This is a Jetty-powered WebApp that serves as a web-interface for GPhoto2 program.
It can be put on any machine that has GPhoto2 (usually in PATH) and Java 1.5 or above. The calls to GPhoto2 are done using simple exec.

The program is distributed as an executable JAR file + lib folder with dependencies (few more JAR files). 
Once started, it listens on port 8080 by default (use port command line parameter to set different value).

Command-line arguments:
- gphoto2path - specify path to gphoto2 executable. 
Optional. Location of gphoto2 will be searched in PATH by default.
- port - TCP port to listen on. 
Optional. Default port is 8080.
- logLevel - logging level (one of TRACE, DEBUG, INFO, WARN, ERROR, SEVERE, FATAL). 
Optional. Default is INFO.
- usemocks - do not execute gphoto2 but use in-built mock gphoto2 responses (which cover around 10% of available action).
Optional. Used for development purpopses only.

Dependencies:
- GPhoto2 - http://www.gphoto.org
- Java version 1.5 or above

Video demonstration using Raspberry Pi: https://www.youtube.com/watch?v=_aXn34VpjB8

Notes:
- See also GPhoto2 for Raspberry Pi - https://github.com/gonzalo/gphoto2-updater
