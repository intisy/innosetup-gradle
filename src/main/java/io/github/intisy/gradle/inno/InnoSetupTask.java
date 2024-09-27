package io.github.intisy.gradle.inno;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

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
    public String getIcon() {
        return icon.getAbsolutePath();
    }

    public void setIcon(String icon) {
        this.icon = new File(icon);
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
        } else {
            System.out.println("Please define 'fileName' and 'name'");
        }
    }
}