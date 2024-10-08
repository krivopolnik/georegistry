package com.borschevski.georegistry.controller;

import com.borschevski.georegistry.service.FileDownloadService;
import com.borschevski.georegistry.service.FileUnzipService;
import com.borschevski.georegistry.service.XMLParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;

/**
 * Controller to manage the download and parsing of XML files.
 */
@Slf4j
@RestController
public class DataController {

    private final FileDownloadService fileDownloadService;
    private final XMLParserService xmlParsingService;
    private final FileUnzipService fileUnzipService;

    @Value("${app.download.url}")
    private String downloadUrl;

    @Value("${app.download.path}")
    private String downloadPath;

    @Value("${app.unzip.path}")
    private String unzipPath;

    public DataController(FileDownloadService fileDownloadService, XMLParserService xmlParsingService, FileUnzipService fileUnzipService) {
        this.fileDownloadService = fileDownloadService;
        this.xmlParsingService = xmlParsingService;
        this.fileUnzipService = fileUnzipService;
    }

    @GetMapping("/download-and-parse")
    public String downloadAndParse() {
        final String uniqueId = UUID.randomUUID().toString();
        final Path zipPath = Paths.get(downloadPath, "data-" + uniqueId + ".zip");
        final Path unzipDir = Paths.get(unzipPath, uniqueId);

        try {
            Files.createDirectories(unzipDir);

            log.info("Starting download of file from URL: {}", downloadUrl);
            fileDownloadService.downloadFile(downloadUrl, zipPath);

            log.info("Unzipping file: {}", zipPath);
            fileUnzipService.unzipFile(zipPath, unzipDir);

            Path xmlFile = findXmlFile(unzipDir);

            log.info("Parsing XML file: {}", xmlFile);
            xmlParsingService.parseAndSave(xmlFile.toFile());

            log.info("Data successfully downloaded and parsed.");
            return "Data successfully downloaded and parsed!\n";
        } catch (Exception e) {
            log.error("Failed to download and parse data: {}", e.getMessage(), e);
            return "Failed to download and parse data: " + e.getMessage();
        } finally {
            cleanUpTemporaryFiles(unzipDir);
        }
    }

    private Path findXmlFile(Path directory) throws IOException {
        return Files.walk(directory)
                .filter(path -> path.toString().endsWith(".xml"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No XML file found in the unzipped content"));
    }

    private void cleanUpTemporaryFiles(Path directory) {
        try {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            log.warn("Failed to delete file: {}", file);
                        }
                    });
        } catch (IOException e) {
            log.error("Failed to clean up temporary files: {}", e.getMessage(), e);
        }
    }
}