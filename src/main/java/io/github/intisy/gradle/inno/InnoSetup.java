package io.github.intisy.gradle.inno;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class InnoSetup {
    File path;
    String fileName;
    String iconPath;
    String name;
    String jreName;

    public InnoSetup(File path, String fileName, String name, File icon, boolean copyIcon) throws IOException {
        this.path = path;
        this.fileName = "libs\\" + fileName;
        this.name = name;
        this.iconPath = name.toLowerCase().replace(" ", "-") + ".ico";
        if (copyIcon) {
            path.toPath().resolve(iconPath).toFile().delete();
            Files.copy(icon == null ? path.getParentFile().toPath().resolve(iconPath) : icon.toPath(), path.toPath().resolve(iconPath));
        }
        this.jreName = "libs\\jre";
    }

    public void buildInstaller() throws IOException, InterruptedException {
        File innoSetupCompiler = GitHub.download().resolve("ISCC.exe").toFile();
        File scriptPath = path.toPath().resolve("build.iss").toFile();
        createInnoSetupScript(scriptPath);
        ProcessBuilder processBuilder = new ProcessBuilder(innoSetupCompiler.getAbsolutePath(), scriptPath.getAbsolutePath());
        processBuilder.directory(path);
        Process process = processBuilder.start();
//        int exitCode = process.waitFor();
//        if (exitCode == 0) {
//            System.out.println("Installer created successfully!");
//        } else {
//            System.err.println("Inno Setup compilation failed with exit code: " + exitCode);
//        }
    }

    public void createInnoSetupScript(File scriptPath) throws IOException {
        String scriptContent = "[Setup]\n" +
                "AppName=" + name + "\n" +
                "AppVersion=1.0\n" +
                "DefaultDirName={pf}\\" + name.replace(" ", "") + "\n" +
                "DefaultGroupName=" + name.replace(" ", "") + "\n" +
                "OutputDir=libs\n" +
                "OutputBaseFilename=" + name.toLowerCase() + "-installer\n" +
                "SetupIconFile=" + iconPath + "\n" +
                "Compression=lzma\n" +
                "SolidCompression=yes\n" +
                "\n" +
                "[Files]\n" +
                "; Add executable and JRE files\n" +
                "Source: \"" + fileName + "\"; DestDir: \"{app}\"; Flags: ignoreversion\n" +
                "Source: \"" + jreName + "\\*\"; DestDir: \"{app}\\jre\"; Flags: recursesubdirs\n" +
                "\n" +
                "[Icons]\n" +
                "; Create desktop shortcut\n" +
                "Name: \"{commondesktop}\\" + name + "\"; Filename: \"{app}\\" + name.replace(" ", "") + ".exe\"\n" +
                "\n" +
                "[Run]\n" +
                "; Run the application after installation\n" +
                "Filename: \"{app}\\" + name.replace(" ", "") + ".exe\"; Description: \"Launch " + name + "\"; Flags: nowait postinstall skipifsilent\n";
        scriptPath.delete();
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write(scriptContent);
        }
        System.out.println("Inno Setup script created at: " + scriptPath);
    }
}
