package com.borschevski.georegistry.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileUnzipServiceTest {

    private FileUnzipService fileUnzipService;

    @BeforeEach
    void setUp() {
        fileUnzipService = new FileUnzipService();
    }

    @Test
    void testUnzipFileSuccess(@TempDir Path tempDir) throws IOException {
        // Setup a sample zip file in memory
        Path zipFilePath = tempDir.resolve("test.zip");
        Path fileInZipPath = Paths.get("test.txt");
        byte[] fileContent = "Hello, world!".getBytes();

        // Create a zip file with one text file inside
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            ZipEntry entry = new ZipEntry(fileInZipPath.toString());
            zos.putNextEntry(entry);
            zos.write(fileContent);
            zos.closeEntry();
        }

        // Destination directory
        Path destDir = tempDir.resolve("output");
        Files.createDirectories(destDir);

        // Execute the unzip method
        fileUnzipService.unzipFile(zipFilePath, destDir);

        // Verify that the file was unzipped correctly
        Path unzippedFilePath = destDir.resolve(fileInZipPath);
        assertTrue(Files.exists(unzippedFilePath), "File should have been unzipped");
        assertTrue(Files.isRegularFile(unzippedFilePath), "Unzipped file should be a regular file");
        assertEquals(Files.readAllBytes(unzippedFilePath).length, fileContent.length, "File content should match");
    }

    @Test
    void testUnzipFileWithIOException(@TempDir Path tempDir) throws IOException {
        Path zipFilePath = tempDir.resolve("test.zip");
        Path destDir = tempDir.resolve("output");

        // Create a mock static method Files.newInputStream so that it throws IOException
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.newInputStream(zipFilePath)).thenThrow(new IOException("Failed to open stream"));

            // Check that our service throws IOException when calling unzipFile
            assertThrows(IOException.class, () -> fileUnzipService.unzipFile(zipFilePath, destDir));
        }
    }
}
