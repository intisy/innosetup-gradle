package io.github.intisy.gradle.inno;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

public class InnoSetupTask extends DefaultTask {
    String fileName;
    String name;
    String icon;

    @TaskAction
    public void createExe() {
        if (fileName != null && name != null) {
            try {
                new InnoSetup(getProject().getBuildDir(), fileName, name, icon != null ? new File(icon) : null, true).buildInstaller();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            Main.log("Please define 'fileName' and 'name'");
        }
    }
}