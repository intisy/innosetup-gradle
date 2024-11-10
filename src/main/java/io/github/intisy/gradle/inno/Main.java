package io.github.intisy.gradle.inno;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Main class.
 */
class Main implements org.gradle.api.Plugin<Project> {
	static final boolean debug = true;

	@Override
	public void apply(@NotNull Project target) {

	}
	
	public static void log(String log) {
		if (debug)
			System.out.println(log);
	}
}
