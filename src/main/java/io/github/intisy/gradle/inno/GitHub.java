package io.github.intisy.gradle.inno;

import org.gradle.internal.impldep.com.google.gson.JsonObject;
import org.gradle.internal.impldep.com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GitHub {
    private static final String OWNER = "intisy";
    private static final String REPO = "InnoSetup";
    private static final String GITHUB_LATEST_RELEASE_API = "https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/latest";

    public static Path download() {
        try {
            JsonObject latestReleaseZip = getLatestReleaseZip(GITHUB_LATEST_RELEASE_API);
            if (latestReleaseZip != null) {
                System.out.println("Downloading from: " + latestReleaseZip);
                Path path = GradleUtils.getGradleHome().resolve("inno").resolve(latestReleaseZip.get("tag_name").getAsString());
                File output = path.resolve("inno.zip").toFile();
                if (!path.toFile().exists()) {
                    path.toFile().mkdirs();
                    downloadFile(latestReleaseZip.get("zipball_url").getAsString(), output);
                    System.out.println("Download completed.");
                    unzipAndFlatten(output, path.toFile());
                    System.out.println("Unzip completed to " + path);
                }
                return path;
            } else {
                System.out.println("Failed to get the latest release ZIP URL.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public static void unzipAndFlatten(File zipFilePath, File outputDirectory) throws IOException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String entryName = entry.getName();
                String prefix = entryName.split("/")[0];
                File file = new File(outputDirectory, entryName.replace(prefix, ""));
                if (entryName.split("/").length > 2) {
                    createDirectoriesForFile(file);
                    extractFile(zipFile, entry, file);
                } else {
                    extractFile(zipFile, entry, file);
                }
            }
        }
        zipFilePath.delete();
    }

    private static void createDirectoriesForFile(File file) {
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

    private static void extractFile(ZipFile zipFile, ZipEntry entry, File file) throws IOException {
        try (InputStream inputStream = zipFile.getInputStream(entry);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    public static JsonObject getLatestReleaseZip(String apiUrl) throws Exception {
        System.out.println("Calling API: " + apiUrl);
        URL url = new URL(apiUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("GET");

        int responseCode = httpConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return JsonParser.parseString(response.toString()).getAsJsonObject();
            }
        } else {
            System.out.println("Failed to get latest release info. HTTP Response Code: " + responseCode);
        }
        httpConnection.disconnect();
        return null;
    }

    public static void downloadFile(String fileUrl, File outputFile) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("GET");

        int responseCode = httpConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = new BufferedInputStream(httpConnection.getInputStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } else {
            System.out.println("Failed to download file. HTTP Response Code: " + responseCode);
        }
        httpConnection.disconnect();
    }
}