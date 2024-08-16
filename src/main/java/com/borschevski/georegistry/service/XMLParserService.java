package com.borschevski.georegistry.service;

import com.borschevski.georegistry.entity.Obec;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

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

    private final String OBI_PREFIX = "obi";
    private final String VF_PREFIX = "vf";
    private final String OBEC_ELEMENT = "Obec";
    private final String KOD_ELEMENT = "Kod";
    private final String NAZEV_ELEMENT = "Nazev";

    private final DatabaseService databaseService;

    public XMLParserService(DatabaseService databaseService) {
        this.databaseService = databaseService;
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
        if (!isStartElementWithName(reader, VF_PREFIX, OBEC_ELEMENT)) return;

        Obec obec = new Obec();
        boolean foundKod = false, foundNazev = false;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (reader.getLocalName().equals(KOD_ELEMENT) && reader.getPrefix().equals(OBI_PREFIX)) {
                    foundKod = parseKodElement(reader, obec::setKod);
                } else if (reader.getLocalName().equals(NAZEV_ELEMENT) && reader.getPrefix().equals(OBI_PREFIX)) {
                    foundNazev = parseElement(reader, obec::setNazev);
                }
            }
            if (event == XMLStreamConstants.END_ELEMENT && isEndElementWithName(reader, VF_PREFIX, OBEC_ELEMENT)) {
                break;
            }
        }

        if (foundKod && foundNazev) {
            databaseService.saveObec(obec);
        }
    }

    private boolean isStartElementWithName(@NotNull XMLStreamReader reader, String prefix, String localName) {
        return reader.getEventType() == XMLStreamConstants.START_ELEMENT &&
                reader.getPrefix().equals(prefix) &&
                reader.getLocalName().equals(localName);
    }

    private boolean isEndElementWithName(@NotNull XMLStreamReader reader, String prefix, String localName) {
        return reader.getEventType() == XMLStreamConstants.END_ELEMENT &&
                reader.getPrefix().equals(prefix) &&
                reader.getLocalName().equals(localName);
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