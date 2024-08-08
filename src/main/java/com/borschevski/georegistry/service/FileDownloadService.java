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

@Slf4j
@Service
public class FileDownloadService {

    private final URLFactory urlFactory;

    public FileDownloadService(URLFactory urlFactory) {
        this.urlFactory = urlFactory;
    }

    /**
     * Downloads a file from the specified URL and saves it to the specified path on the file system.
     *
     * @param urlString URL of the file to be downloaded as a String
     * @param destPath Path where the file will be saved to
     * @throws IOException if an error occurs while loading or saving a file
     */
    public void downloadFile(String urlString, Path destPath) throws IOException {
        log.info("Starting download of file from URL: {}", urlString);

        try {
            URL url = urlFactory.createURL(urlString); // Use URLFactory to create URL
            try (InputStream in = url.openStream()) {
                Files.copy(in, destPath, StandardCopyOption.REPLACE_EXISTING);
                log.info("File successfully downloaded and saved to {}", destPath);
            } catch (IOException e) {
                log.error("Failed to download the file from URL: {}. Error: {}", urlString, e.getMessage());
                throw new IOException("Failed to download the file: " + urlString, e);
            }
        } catch (MalformedURLException e) {
            log.error("Invalid URL: {}", urlString, e);
            throw new IOException("Invalid URL: " + urlString, e);
        }
    }
}
