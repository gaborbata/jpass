JPass
=====

Overview
--------
JPass is a simple, small, portable password manager application with strong encryption. It allows you to store user names, passwords, URLs and generic notes in an encrypted file protected by one master password.

Features:

* Strong encryption - AES-256-CBC algorithm (SHA-256 is used as password hash)
* Portable - single jar file which can be carried on a USB stick
* Built-in random password generator
* Organize all your user name, password, URL and notes information in one file
* Data import/export in XML format

![JPass](https://raw.githubusercontent.com/gaborbata/jpass/master/resources/jpass-screenshot.png)

How to compile
--------------
* Maven: `mvn clean install`
* Gradle: `gradle clean build`

Usage
-----
Java 6 or later is recommended to run JPass. Most platforms have a mechanism to execute `.jar` files (e.g. double click the `jpass-0.1.12.jar`).
You can also run the application from the command line by typing (the password file is optional):

    java -jar jpass-0.1.12.jar [password_file]

License
-------
Copyright (c) 2009-2012 Gabor Bata

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.

This software is provided by the author "as is" and any express or implied warranties, including, but not limited to, the implied warranties of merchantability and fitness for a particular purpose are disclaimed. In no event shall the author be liable for any direct, indirect, incidental, special, exemplary, or consequential damages (including, but not limited to, procurement of substitute goods or services; loss of use, data, or profits; or business interruption) however caused and on any theory of liability, whether in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this software, even if advised of the possibility of such damage.

---

This software includes MicroCrypt 0.3, covered by the following license:

Copyright (c) 2007 Timm Knape

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3. Neither the name of Timm Knape nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

This software is provided by the copyright holders and contributors "as is" and any express or implied warranties, including, but not limited to, the implied warranties of merchantability and fitness for a particular purpose are disclaimed. In no event shall the copyright owner or contributors be liable for any direct, indirect, incidental, special, exemplary, or consequential damages (including, but not limited to, procurement of substitute goods or services; loss of use, data, or profits; or business interruption) however caused and on any theory of liability, whether in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this software, even if advised of the possibility of such damage.

---

This software includes SpringUtilities, covered by the following license:

Copyright (c) 1995-2008 Sun Microsystems, Inc.

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3. Neither the name of Sun Microsystems nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

This software is provided by the copyright holders and contributors "as is" and any express or implied warranties, including, but not limited to, the implied warranties of merchantability and fitness for a particular purpose are disclaimed. In no event shall the copyright owner or contributors be liable for any direct, indirect, incidental, special, exemplary, or consequential damages (including, but not limited to, procurement of substitute goods or services; loss of use, data, or profits; or business interruption) however caused and on any theory of liability, whether in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this software, even if advised of the possibility of such damage.

---

This software uses icons from the [Silk icon set by Mark James](http://www.famfamfam.com/lab/icons/silk/).

The Silk icon set is licensed under a [Creative Commons Attribution 2.5 License](http://creativecommons.org/licenses/by/2.5/).

---

This software uses icons from the [Tango base icon theme](http://tango.freedesktop.org/Tango_Desktop_Project).

The Tango base icon theme is licensed under the [Creative Commons Attribution Share-Alike license](http://creativecommons.org/licenses/by-sa/2.5/).
