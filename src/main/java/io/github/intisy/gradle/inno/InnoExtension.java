package io.github.intisy.gradle.inno;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;

public class InnoExtension {
    private String filename;
    private String name;
    private File icon;

    public String getFilename() {
        return filename;
    }

    public void run(Project project) {
        if (filename != null && name != null) {
            try {
                new InnoSetup(project.getBuildFile(), filename, name, icon, true).buildInstaller();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void run(File project) {
        try {
            new InnoSetup(project, filename, name, icon, false).buildInstaller();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIcon(File icon) {
        this.icon = icon;
    }

    public File getIcon() {
        return icon;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}