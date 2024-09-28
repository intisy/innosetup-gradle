package io.github.intisy.gradle.inno;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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
        Path innoFolder = path.toPath().resolve("inno");
        innoFolder.toFile().delete();
        FileUtils.copyFolder(Objects.requireNonNull(GitHub.download()), innoFolder);
        File innoSetupCompiler = innoFolder.resolve("ISCC.exe").toFile();
        File scriptPath = path.toPath().resolve("build.iss").toFile();
        createInnoSetupScript(scriptPath);
        Main.log("Starting Inno Setup script " + scriptPath.getAbsolutePath() + " using " + innoSetupCompiler.getAbsolutePath());
        File output = path.toPath().resolve("libs").resolve(name.toLowerCase().replace(" ", "-") + "-installer.exe").toFile();
        output.delete();
        String[] command = {innoSetupCompiler.getAbsolutePath(), scriptPath.getAbsolutePath()};
        Runtime.getRuntime().exec(command, null, path);
        Main.files.add(output);
    }

    public void createInnoSetupScript(File scriptPath) throws IOException {
        String scriptContent = "[Setup]\n" +
                "AppName=" + name + "\n" +
                "AppVersion=1.0\n" +
                "DefaultDirName={pf}\\" + name.replace(" ", "") + "\n" +
                "DefaultGroupName=" + name.replace(" ", "") + "\n" +
                "OutputDir=libs\n" +
                "OutputBaseFilename=" + name.toLowerCase().replace(" ", "-") + "-installer\n" +
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
        Main.log("Inno Setup script created at: " + scriptPath);
    }
}
