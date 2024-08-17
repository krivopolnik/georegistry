package com.borschevski.georegistry.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Service for downloading files from URLs.
 */
@Slf4j
@Service
public class FileDownloadService {

    private final URLFactory urlFactory;

    /**
     * Constructs a new FileDownloadService with the provided URLFactory.
     * @param urlFactory A factory for creating URL instances from strings.
     */
    public FileDownloadService(URLFactory urlFactory) {
        this.urlFactory = urlFactory;
    }

    /**
     * Downloads a file from the specified URL and saves it to the specified path on the file system.
     *
     * @param urlString URL of the file to be downloaded as a String.
     * @param destPath Path where the file will be saved.
     * @throws IOException if an error occurs during file download or saving.
     */
    public void downloadFile(String urlString, Path destPath) throws IOException {
        URL url;
        try {
            url = urlFactory.createURL(urlString); // Attempt to create a URL from the provided string.
        } catch (MalformedURLException e) {
            log.error("Invalid URL: {}. Error: {}", urlString, e.getMessage(), e);
            throw new MalformedURLException("Invalid URL: " + urlString);
        }

        // Ensure the directory for the destination path exists before starting the download.
        ensureDirectoryExists(destPath);

        // Open a stream to the URL and download the file to the destination path.
        try (InputStream in = url.openStream()) {
            Files.copy(in, destPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File successfully downloaded and saved to {}", destPath);
        } catch (IOException e) {
            log.error("Failed to download the file from URL: {}. Error: {}", urlString, e.getMessage(), e);
            throw new IOException("Failed to download the file: " + urlString, e);
        }
    }

    /**
     * Ensures that the directory for the given file path exists.
     * @param destPath The path where the file will be saved.
     * @throws IOException if an error occurs while creating the directory.
     */
    private void ensureDirectoryExists(Path destPath) throws IOException {
        final Path parentDir = destPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
            log.info("Created directory {}", parentDir);
        }
    }
}
