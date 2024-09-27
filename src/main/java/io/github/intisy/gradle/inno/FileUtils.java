package io.github.intisy.gradle.inno;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static void waitForFile(File file, long checkIntervalMillis) throws InterruptedException {
        while (true) {
            if (file.exists()) {
                if (isFileAccessible(file)) {
                    System.out.println("File is available and not in use: " + file);
                    break;
                } else {
                    System.out.println("File exists but is in use: " + file);
                }
            } else {
                System.out.println("File does not exist yet: " + file);
            }
            Thread.sleep(checkIntervalMillis);
        }
    }

    private static boolean isFileAccessible(File file) {
        try (FileOutputStream ignored = new FileOutputStream(file, true)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
