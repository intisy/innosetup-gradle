package io.github.intisy.gradle.inno;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.Task;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class InnoSetupTask extends DefaultTask {
    private String fileName;
    private String name;
    private File icon;

    @Input
    public @NotNull String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Input
    public File getIcon() {
        return icon;
    }

    public void setIcon(File icon) {
        this.icon = icon;
    }

    @Input
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @TaskAction
    public void createExe() {
        if (fileName != null && name != null) {
            try {
                new InnoSetup(getProject().getBuildDir(), fileName, name, icon, true).buildInstaller();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}