package com.borschevski.georegistry.service;

import com.borschevski.georegistry.entity.Obec;
import com.borschevski.georegistry.repository.ObecRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@Service
public class XMLParserService {

    private final ObecRepository obecRepository;

    public XMLParserService(ObecRepository obecRepository) {
        this.obecRepository = obecRepository;
    }

    /**
     * Parses the provided XML file and saves the extracted Obec entities to the database.
     *
     * @param xmlFile The XML file to be parsed.
     * @throws IllegalArgumentException if the file does not exist.
     * @throws RuntimeException if there is an error during XML processing.
     */
    public void parseAndSave(File xmlFile) {
        XMLInputFactory factory = XMLInputFactory.newInstance();

        try (FileInputStream fileInputStream = new FileInputStream(xmlFile)) {
            XMLStreamReader reader = factory.createXMLStreamReader(fileInputStream);

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    processStartElement(reader);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("File not found: {}", xmlFile.getPath(), e);
            throw new IllegalArgumentException("Provided file does not exist.", e);
        } catch (XMLStreamException | IOException e) {
            log.error("Error processing XML file: {}", xmlFile.getPath(), e);
            throw new RuntimeException("Error processing XML file", e);
        }
    }

    /**
     * Processes a start element, specifically looking for "Obec" elements and extracts data.
     *
     * @param reader The XMLStreamReader currently being used for reading the XML file.
     * @throws XMLStreamException if an error occurs during XML processing.
     */
    private void processStartElement(XMLStreamReader reader) throws XMLStreamException {
        if (!isStartElementWithName(reader)) return;

        Obec obec = new Obec();
        boolean foundKod = false, foundNazev = false;

        while (reader.hasNext() && !(foundKod && foundNazev)) {
            if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                foundKod = parseKodElement(reader, obec::setKod);
                foundNazev = parseElement(reader, obec::setNazev);
            }
            if (endOfElement(reader)) break;
        }

        if (foundKod && foundNazev) saveObec(obec);
    }

    @Transactional
    public void saveObec(@NotNull Obec currentObec) {
        if (currentObec.getKod() != null) {
            if (obecRepository.existsById(currentObec.getKod())) {
                log.info("Updating existing entity: {}", currentObec);
            } else {
                log.info("Creating new entity: {}", currentObec);
            }
        } else {
            log.info("Saving new entity without kod: {}", currentObec);
        }
        obecRepository.save(currentObec);
    }

    private boolean isStartElementWithName(@NotNull XMLStreamReader reader) {
        return reader.getPrefix().equals("vf") && reader.getLocalName().equals("Obec");
    }

    private boolean parseElement(@NotNull XMLStreamReader reader, Consumer<String> setter) throws XMLStreamException {
        if (reader.getPrefix().equals("obi") && reader.getLocalName().equals("Nazev")) {
            setter.accept(reader.getElementText().trim());
            return true;
        }
        return false;
    }

    private boolean parseKodElement(@NotNull XMLStreamReader reader, Consumer<Integer> setter) throws XMLStreamException {
        if (reader.getPrefix().equals("obi") && reader.getLocalName().equals("Kod")) {
            String text = reader.getElementText().trim();
            try {
                Integer value = Integer.valueOf(text);
                setter.accept(value);
            } catch (NumberFormatException e) {
                log.error("Failed to parse 'kod' as integer: {}", text, e);
                return false; // Return false if parsing fails
            }
            return true;
        }
        return false;
    }

    private boolean endOfElement(@NotNull XMLStreamReader reader) {
        return reader.getEventType() == XMLStreamConstants.END_ELEMENT &&
                reader.getLocalName().equals("Obec") && reader.getPrefix().equals("vf");
    }
}