package io.github.intisy.gradle.inno;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;

public class InnoExtension {
    private String filename = "default.exe";
    private String name = "DefaultName";

    public String getFilename() {
        return filename;
    }

    public void run(Project project) {
        try {
            new InnoSetup(project.getBuildFile(), filename, name, true).buildInstaller();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(File project) {
        try {
            new InnoSetup(project, filename, name, false).buildInstaller();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}