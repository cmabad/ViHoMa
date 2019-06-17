# ViHoMa
<img src="https://raw.githubusercontent.com/cmabad/ViHoMa/master/src/resources/ico-big.png" alt="Vihoma logo" height="130em"/>
Hi! You have reached the GIT repository for Vihoma, a Visual Hosts file Manager. 

### What is Vihoma?
Vihoma is a program that runs on your computer and helps the operating system to block connections to malicious websites. It's like an adblocker, but system-wide. You may manually block domains through its graphical interface, or let the program automatically do it for you.

For more advanced users, Vihoma also lets adding domains with custom addresses.
![Vihoma main tab](https://raw.githubusercontent.com/cmabad/ViHoMa/master/src/resources/main-tab.PNG)

### How does it protect me?
This app will download a list of hosts that are considered malicious, and tells your computer to avoid the connection to them by adding that list to a local file called `hosts`.
It uses a consolidated hosts list provided by **Steven Black**. You can check his [Github repository](https://github.com/StevenBlack/hosts) for more information.

### What do I need to run it?
Vihoma is built with Java 8 and JavaFX. You'll need to have **JRE 8** installed on your computer. Given that a system file is modified, **administration privileges** are required too.

### Where can I download it?
You will find .zip files containing the program executable on [this](https://www.dropbox.com/sh/fs6pz5nhim62oj0/AAAvIwUTKPrRaSUB5iNtcGLIa?dl=0) dropbox folder.

### How do i run it?
If you are on **Windows**, double click on the vihoma.bat file.
If a **GNU/Linux**-based system is used, execute vihoma.sh. Make sure that you have an OpenJRE 8 instance installed. The JavaFX library is not included, so you must manually install openjfx too. *For example, using apt install openjfx*.

*NOTE 2: Vihoma overwrites your system's hosts file. If you have previously modified it with custom hosts, you may want to backup the file first.*