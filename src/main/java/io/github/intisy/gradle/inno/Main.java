package io.github.intisy.gradle.inno;

import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class.
 */
class Main implements org.gradle.api.Plugin<Project> {
	static final boolean auto = false;
	static final boolean debug = true;
	static final List<File> files = new ArrayList<File>();
	/**
	 * Applies all the project stuff.
	 */
    public void apply(Project project) {
		InnoExtension innoExtension = project.getExtensions().create("inno", InnoExtension.class);
		if (auto)
			project.afterEvaluate(proj -> innoExtension.run(project));
		Task task = project.task("processInnoValues");
		task.doLast(proj -> innoExtension.run(project));
		task = project.task("waitForInnoValues");
		task.doLast(proj -> {
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            for (File file : files) {
//				Main.log("Waiting for output to be written to " + file);
//				try {
//					FileUtils.waitForFile(file, 1000);
//				} catch (InterruptedException | IOException e) {
//					throw new RuntimeException(e);
//				}
//				Main.log("Finished Inno Setup to " + file);
//				files.remove(file);
//			}
		});
    }

	public static void main(String[] args) {
		InnoExtension innoExtension = new InnoExtension();
		innoExtension.setFilename("blizzity-standalone.exe");
		innoExtension.setName("Blizzity");
		innoExtension.run(new File("test"));
	}
	
	public static void log(String log) {
		if (debug)
			System.out.println(log);
	}
}
