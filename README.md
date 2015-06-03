# GPhoto2 Server

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
- Java 1.5 and above

Video demonstration using Raspberry Pi: https://www.youtube.com/watch?v=_aXn34VpjB8

Notes:
- See also GPhoto2 for Raspberry Pi - https://github.com/gonzalo/gphoto2-updater
