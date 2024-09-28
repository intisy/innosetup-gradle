package io.github.intisy.gradle.inno;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    public static void waitForFile(File file, long checkIntervalMillis) throws InterruptedException, IOException {
        while (true) {
            if (file.exists()) {
                Main.log("File size: " + Files.size(file.toPath()));
                if (isFileAccessible(file) && Files.size(file.toPath()) > 0) {
                    Main.log("File is available and not in use: " + file);
                    break;
                } else {
                    Main.log("File exists but is in use: " + file);
                }
            } else {
                Main.log("File does not exist yet: " + file);
            }
            Thread.sleep(checkIntervalMillis);
        }
    }

    public static void copyFolder(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = target.resolve(source.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static boolean isFileAccessible(File file) {
        try (FileOutputStream ignored = new FileOutputStream(file, true)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
