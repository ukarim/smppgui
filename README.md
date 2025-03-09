![smppgui](src/main/resources/icon.png)

### smppgui

Simple SMPP GUI client written in java. No external dependencies. Works on Linux and Windows (should work on MacOS, not tested).

### Usage

You will need java 11 or higher preinstalled on your machine.

1. Download smppgui.jar from [https://github.com/ukarim/smppgui/releases](https://github.com/ukarim/smppgui/releases)
or build it manually using gradle: `gradle build`

2. Launch smppgui using following command: `java -jar smppgui.jar`

### Screenshot

![submit form](img/submit_form.png)

### Build custom java runtime with smppgui

1. Build custom java runtime `gradle jlink`
2. Launch smppgui using the generated bash script: `./smppgui/bin/smppgui`
3. Finally, archive the generated _smppgui_ directory and distribute it. Thus, end users do not need to pre-install java on their computers.

### Using custom Look&Feel

If the GUI looks ugly (this is the case in Linux environments), then you can apply custom Look&Feel.

For example, [FlatLaf](https://www.formdev.com/flatlaf/).

1. Download FlatLaf from MavenCentral [flatlaf-3.5.4.jar](https://repo1.maven.org/maven2/com/formdev/flatlaf/3.5.4/flatlaf-3.5.4.jar)
2. Use following command to launch smppgui
   ```
   java -cp flatlaf-3.5.4.jar:smppgui.jar -Dswing.defaultlaf=com.formdev.flatlaf.FlatLightLaf com.ukarim.smppgui.Main
   ```
