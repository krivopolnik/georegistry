package com.borschevski.georegistry.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileDownloadServiceTest {

    @InjectMocks
    private FileDownloadService fileDownloadService;

    @Mock
    private URLFactory mockURLFactory;

    @Mock
    private Path mockPath;

    @Mock
    private URL mockURL;

    @BeforeEach
    public void setup() throws Exception {
        // Mocking URLFactory to return mock URL
        when(mockURLFactory.createURL(anyString())).thenReturn(mockURL);
    }

    @Test
    public void testDownloadFileSuccess() throws Exception {
        String testUrl = "https://www.smartform.cz/download/kopidlno.xml.zip";

        // Create a mock InputStream for testing
        byte[] mockData = "mock data".getBytes();
        InputStream mockInputStream = new ByteArrayInputStream(mockData);

        // Mock URL.openStream to return the mock InputStream
        when(mockURL.openStream()).thenReturn(mockInputStream);

        // Mock the static method Files.copy
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.copy(any(InputStream.class), eq(mockPath), eq(StandardCopyOption.REPLACE_EXISTING)))
                    .thenReturn(1L); // Assuming one file was copied

            fileDownloadService.downloadFile(testUrl, mockPath);

            // Verify that the input stream is opened and the file is copied
            verify(mockURL).openStream();
            mockedFiles.verify(() -> Files.copy(eq(mockInputStream), eq(mockPath), eq(StandardCopyOption.REPLACE_EXISTING)));
        }
    }

    @Test
    public void testDownloadFileIOException() throws Exception {
        String testUrl = "https://www.smartform.cz/download/kopidlno.xml.zip";

        // Mock InputStream to throw IOException
        when(mockURL.openStream()).thenThrow(new IOException("Failed to open stream"));

        assertThrows(IOException.class, () -> fileDownloadService.downloadFile(testUrl, mockPath));
        verify(mockURL).openStream();
    }

    @Test
    public void testInvalidURL() throws MalformedURLException {
        String invalidUrl = "invalid-url";
        when(mockURLFactory.createURL(invalidUrl)).thenThrow(new MalformedURLException("Invalid URL"));

        assertThrows(IOException.class, () -> fileDownloadService.downloadFile(invalidUrl, mockPath));
        verify(mockURLFactory).createURL(invalidUrl);
    }
}
