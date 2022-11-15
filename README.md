<p align="center">
 <img src="https://raw.githubusercontent.com/gaborbata/jpass/master/resources/bannerReadMe.png" width=750 align="center">
</p>

JPass [![Java CI with Maven](https://github.com/gaborbata/jpass/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/gaborbata/jpass/actions/workflows/maven.yml) [![Java CI with Gradle](https://github.com/gaborbata/jpass/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/gaborbata/jpass/actions/workflows/gradle.yml)
=====

Overview
--------
JPass is a simple, small, portable password manager application with strong encryption. It allows you to store user names, passwords, URLs and generic notes in an encrypted file protected by one master password.

Features:

* Strong encryption - AES-256-CBC algorithm (PBKDF2-HMAC-SHA-256 is used as password-based key derivation function)
* Portable - single jar file which can be carried on a USB stick
* Built-in random password generator
* Organize all your user name, password, URL and notes information in one file
* Data import/export in XML format

![JPass](https://raw.githubusercontent.com/gaborbata/jpass/master/resources/jpass-capture.png)

Usage
-----
Java 8 or later is recommended to run JPass.
You can run the application from the command line by typing (the password file is optional):

    java -jar jpass-1.0.2-SNAPSHOT.jar [password_file]

For convenience, batch/shell scripts are also available for launching JPass for various platforms (i.e. `jpass.bat` for Windows, `jpass.sh` for Linux, `jpass.command` for macOS).
Please make sure `PATH`, or `JAVA_HOME` environment variables point to a valid Java installation.

Download
--------
You can find the latest distribution packages under the [releases](https://github.com/gaborbata/jpass/releases) link.

For Windows, you can also install JPass using [scoop](https://scoop.sh/) from the `extras` bucket:

    scoop bucket add extras
    scoop install jpass

How to compile
--------------
* Gradle: `gradle clean build` (preferred)
* Maven: `mvn clean package`
* sbt: `sbt clean package`

Configuration
-------------
Default configurations can be overridden in `jpass.properties` file:

| Configuration key                  | Value type | Default value    |
| ---------------------------------- | ---------- | ---------------- |
| ui.theme.dark.mode.enabled         | boolean    | `false`          |
| clear.clipboard.on.exit.enabled    | boolean    | `false`          |
| default.password.generation.length | integer    | `14`             |
| date.format                        | string     | `yyyy-MM-dd`     |
| entry.details                      | list       | `TITLE,MODIFIED` |
| file.chooser.directory             | string     | `./`             |

Contributors
------------
Thanks for Jibbie R. Eguna (@jbeguna04) for the nice application logo.
