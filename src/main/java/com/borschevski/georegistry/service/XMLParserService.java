package com.borschevski.georegistry.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

@Slf4j
@Service
public class XMLParserService {

    public void parseAndSave(File xmlFile) {
        log.info("Starting to parse the XML file: {}", xmlFile.getAbsolutePath());

        try {
            Document document = parseXMLFile(xmlFile);
            NodeList obecList = document.getElementsByTagName("vf:Obec");
            NodeList castObceList = document.getElementsByTagName("vf:CastObce");

            parseAndSaveObec(obecList);
            parseAndSaveCastObce(castObceList);

            log.info("Successfully parsed and saved data from the XML file: {}", xmlFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to parse the XML file: {}. Error: {}", xmlFile.getAbsolutePath(), e.getMessage(), e);
        }
    }

    private Document parseXMLFile(File xmlFile) throws Exception {
        log.debug("Initializing XML DocumentBuilder for file: {}", xmlFile.getAbsolutePath());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
        log.debug("XML Document successfully parsed and normalized.");
        return document;
    }

    private void parseAndSaveObec(NodeList obecList) {
        log.debug("Parsing and saving 'Obec' elements.");
        for (int i = 0; i < obecList.getLength(); i++) {
            Element obecElement = (Element) obecList.item(i);
            String kod = obecElement.getElementsByTagName("vf:Kod").item(0).getTextContent();
            String nazev = obecElement.getElementsByTagName("vf:Nazev").item(0).getTextContent();
            log.info("Saving 'Obec' - Kod: {}, Nazev: {}", kod, nazev);
            // Save obec to the database
        }
    }

    private void parseAndSaveCastObce(NodeList castObceList) {
        log.debug("Parsing and saving 'CastObce' elements.");
        for (int i = 0; i < castObceList.getLength(); i++) {
            Element castObceElement = (Element) castObceList.item(i);
            String kod = castObceElement.getElementsByTagName("vf:Kod").item(0).getTextContent();
            String nazev = castObceElement.getElementsByTagName("vf:Nazev").item(0).getTextContent();
            String kodObec = castObceElement.getElementsByTagName("vf:KodObec").item(0).getTextContent();
            log.info("Saving 'CastObce' - Kod: {}, Nazev: {}, Obec Kod: {}", kod, nazev, kodObec);
            // Save cast obce to the database
        }
    }
}
