package com.borschevski.georegistry.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Service for unzipping files.
 * Provides functionality to unzip a file to a specified directory.
 */
@Slf4j
@Service
public class FileUnzipService {

    /**
     * Unzips the specified zip file into the provided destination directory.
     *
     * @param zipFilePath the path to the zip file.
     * @param destDir the directory where the contents will be extracted.
     * @throws IOException if an I/O error occurs.
     */
    public void unzipFile(Path zipFilePath, Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                Path newFilePath = destDir.resolve(zipEntry.getName());

                // Check for Zip Slip vulnerability: ensure extracted files are within the target directory
                if (!newFilePath.normalize().startsWith(destDir.normalize())) {
                    throw new IOException("Bad zip entry: " + zipEntry.getName());
                }

                // Create directories for entries that represent directories
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newFilePath);
                } else {
                    // Ensure parent directory exists as zipEntry could be a file in a non-existing directory
                    if (newFilePath.getParent() != null) {
                        Files.createDirectories(newFilePath.getParent());
                    }
                    // Copy the file content from zip
                    Files.copy(zis, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            log.error("Failed to unzip file: {}. Error: {}", zipFilePath, e.getMessage(), e);
            throw e; // Rethrow to allow the caller to handle it
        }
    }
}
