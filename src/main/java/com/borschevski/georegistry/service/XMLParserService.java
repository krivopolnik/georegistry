package com.borschevski.georegistry.service;

import com.borschevski.georegistry.entity.CastObce;
import com.borschevski.georegistry.entity.Obec;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

/**
 * Service class for parsing XML files and storing the parsed objects into a database.
 */
@Slf4j
@Service
public class XMLParserService {

    private static final String OBI_PREFIX = "obi";
    private static final String VF_PREFIX = "vf";
    private static final String COI_PREFIX = "coi";
    private static final String OBEC_ELEMENT = "Obec";
    private static final String CASTOBCE_ELEMENT = "CastObce";
    private static final String KOD_ELEMENT = "Kod";
    private static final String NAZEV_ELEMENT = "Nazev";

    private final DatabaseService databaseService;

    public XMLParserService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Parses an XML file and saves its content to the database.
     * @param xmlFile The XML file to parse.
     */
    public void parseAndSave(File xmlFile) {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try (FileInputStream fileInputStream = new FileInputStream(xmlFile)) {
            final XMLStreamReader reader = factory.createXMLStreamReader(fileInputStream);
            while (reader.hasNext()) {
                final int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    handleStartElement(reader);
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
     * Handles the start of an XML element and determines which entity to parse and save.
     * @param reader The XML stream reader.
     */
    private void handleStartElement(XMLStreamReader reader) throws XMLStreamException {
        if (isStartElementWithName(reader, OBEC_ELEMENT)) {
            final Obec obec = parseObec(reader);
            if (obec != null) {
                databaseService.saveObec(obec);
            }
        } else if (isStartElementWithName(reader, CASTOBCE_ELEMENT)) {
            final CastObce castObce = parseCastObce(reader);
            if (castObce != null) {
                databaseService.saveCastObce(castObce);
            }
        }
    }

    /**
     * Parses the 'Obec' XML element and extracts its properties.
     * @param reader The XML stream reader.
     * @return An instance of Obec or null if not fully parsed.
     */
    private @Nullable Obec parseObec(@NotNull XMLStreamReader reader) throws XMLStreamException {
        final Obec obec = new Obec();
        while (reader.hasNext()) {
            final int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                updateObecFromElement(reader, obec);
            }
            if (event == XMLStreamConstants.END_ELEMENT && isEndElementWithName(reader, OBEC_ELEMENT)) {
                break;
            }
        }
        return (obec.getKod() != null && obec.getNazev() != null) ? obec : null;
    }

    /**
     * Updates the Obec entity with values from the XML element.
     * @param reader The XML stream reader.
     * @param obec The Obec entity to update.
     */
    private void updateObecFromElement(XMLStreamReader reader, Obec obec) throws XMLStreamException {
        if (isElementWithName(reader, OBI_PREFIX, KOD_ELEMENT)) {
            parseIntegerElement(reader, obec::setKod);
        } else if (isElementWithName(reader, OBI_PREFIX, NAZEV_ELEMENT)) {
            parseStringElement(reader, obec::setNazev);
        }
    }

    /**
     * Parses the 'CastObce' XML element and extracts its properties.
     * @param reader The XML stream reader.
     * @return An instance of CastObce or null if not fully parsed.
     */
    private @Nullable CastObce parseCastObce(@NotNull XMLStreamReader reader) throws XMLStreamException {
        final CastObce castObce = new CastObce();
        while (reader.hasNext()) {
            final int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                updateCastObceFromElement(reader, castObce);
            }
            if (event == XMLStreamConstants.END_ELEMENT && isEndElementWithName(reader, CASTOBCE_ELEMENT)) {
                break;
            }
        }
        return (castObce.getKod() != null && castObce.getNazev() != null && castObce.getObecKod() != null) ? castObce : null;
    }

    /**
     * Updates the CastObce entity with values from the XML element.
     * @param reader The XML stream reader.
     * @param castObce The CastObce entity to update.
     */
    private void updateCastObceFromElement(XMLStreamReader reader, CastObce castObce) throws XMLStreamException {
        if (isElementWithName(reader, COI_PREFIX, KOD_ELEMENT)) {
            parseIntegerElement(reader, castObce::setKod);
        } else if (isElementWithName(reader, COI_PREFIX, NAZEV_ELEMENT)) {
            parseStringElement(reader, castObce::setNazev);
        } else if (isElementWithName(reader, COI_PREFIX, OBEC_ELEMENT)) {
            parseIntegerElement(reader, castObce::setObecKod);
        }
    }

    /**
     * Parses text elements into strings and sets them on the specified setter.
     * @param reader The XML stream reader.
     * @param setter The consumer that sets the parsed text on the entity.
     */
    private void parseStringElement(@NotNull XMLStreamReader reader, Consumer<String> setter) throws XMLStreamException {
        if (reader.next() == XMLStreamConstants.CHARACTERS) {
            setter.accept(reader.getText().trim());
        }
    }

    /**
     * Parses integer elements from the XML and sets them using the provided setter.
     * @param reader The XML stream reader.
     * @param setter The consumer that sets the parsed integer on the entity.
     */
    private void parseIntegerElement(@NotNull XMLStreamReader reader, Consumer<Integer> setter) throws XMLStreamException {
        final StringBuilder textBuilder = new StringBuilder();
        if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
            while (reader.hasNext()) {
                final int event = reader.next();
                if (event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA) {
                    textBuilder.append(reader.getText().trim());
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    break;
                }
            }
        }

        String text = textBuilder.toString();
        try {
            final Integer value = Integer.valueOf(text);
            setter.accept(value);
        } catch (NumberFormatException e) {
            log.error("Failed to parse 'kod' as integer: {}", text, e);
        }
    }

    /**
     * Checks if the current XML start element matches the given name.
     * @param reader The XML stream reader.
     * @param element The element name to match.
     * @return True if matches, otherwise false.
     */
    private boolean isStartElementWithName(@NotNull XMLStreamReader reader, String element) {
        return reader.getEventType() == XMLStreamConstants.START_ELEMENT &&
                reader.getPrefix().equals(XMLParserService.VF_PREFIX) &&
                reader.getLocalName().equals(element);
    }

    /**
     * Checks if the current XML end element matches the given name.
     * @param reader The XML stream reader.
     * @param element The element name to match.
     * @return True if matches, otherwise false.
     */
    private boolean isEndElementWithName(@NotNull XMLStreamReader reader, String element) {
        return reader.getEventType() == XMLStreamConstants.END_ELEMENT &&
                reader.getPrefix().equals(XMLParserService.VF_PREFIX) &&
                reader.getLocalName().equals(element);
    }

    /**
     * Checks if the current XML element's name and prefix match the given criteria.
     * @param reader The XML stream reader.
     * @param prefix The prefix to match.
     * @param element The element name to match.
     * @return True if both prefix and name match, otherwise false.
     */
    private boolean isElementWithName(@NotNull XMLStreamReader reader, String prefix, String element) {
        return reader.getPrefix().equals(prefix) && reader.getLocalName().equals(element);
    }
}