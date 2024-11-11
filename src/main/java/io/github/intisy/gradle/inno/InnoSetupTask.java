package io.github.intisy.gradle.inno;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class InnoSetupTask extends DefaultTask {
    String fileName;
    String name;
    String icon;
    boolean debug;

    @TaskAction
    public void createExe() {
        if (fileName != null && name != null) {
            try {
                new InnoSetup(getProject().getBuildDir(), fileName, name, icon != null ? new File(icon) : null, true, debug).buildInstaller();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (debug) {
            System.out.println("Please define 'fileName' and 'name'");
        }
    }
}