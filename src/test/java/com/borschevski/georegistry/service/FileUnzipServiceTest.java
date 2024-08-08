package com.borschevski.georegistry.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class FileUnzipServiceTest {

    private FileUnzipService fileUnzipService;

    @BeforeEach
    public void setup() {
        fileUnzipService = new FileUnzipService();
    }

    @Test
    public void testUnzipFileSuccess() throws Exception {
        Path mockZipPath = Mockito.mock(Path.class);
        Path mockDestDir = Mockito.mock(Path.class);
        Path mockNewFilePath = Mockito.mock(Path.class);

        // Create a test ZIP file in memory
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            zos.putNextEntry(new ZipEntry("testFile.txt"));
            zos.write("test content".getBytes());
            zos.closeEntry();
        }
        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipBytes);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            // Mocking Files.newInputStream to return a ByteArrayInputStream
            mockedFiles.when(() -> Files.newInputStream(eq(mockZipPath))).thenReturn(byteArrayInputStream);

            // Mocking Files.createDirectories and Files.copy
            mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(mockNewFilePath);
            mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING))).thenReturn(1L);

            // Execute the unzipFile method
            fileUnzipService.unzipFile(mockZipPath, mockDestDir);

            // Verify that the input stream is opened and files are created/copied
            mockedFiles.verify(() -> Files.newInputStream(eq(mockZipPath)));
            mockedFiles.verify(() -> Files.createDirectories(any(Path.class)));
            mockedFiles.verify(() -> Files.copy(any(InputStream.class), eq(mockNewFilePath), eq(StandardCopyOption.REPLACE_EXISTING)));
        }
    }

    @Test
    public void testUnzipFileIOException() throws Exception {
        Path mockZipPath = Mockito.mock(Path.class);
        Path mockDestDir = Mockito.mock(Path.class);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.newInputStream(any(Path.class))).thenThrow(new IOException("Failed to open stream"));

            // Verify that IOException is thrown
            assertThrows(IOException.class, () -> fileUnzipService.unzipFile(mockZipPath, mockDestDir));
        }
    }
}
