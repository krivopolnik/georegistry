package com.borschevski.georegistry.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;

@Slf4j
@Service
public class XMLParserService {

    public void parseAndSave(File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList obecList = doc.getElementsByTagName("vf:Obec");
            NodeList castObceList = doc.getElementsByTagName("vf:CastObce");

            // Example parsing logic
            for (int i = 0; i < obecList.getLength(); i++) {
                Element obecElement = (Element) obecList.item(i);
                String kod = obecElement.getElementsByTagName("vf:Kod").item(0).getTextContent();
                String nazev = obecElement.getElementsByTagName("vf:Nazev").item(0).getTextContent();
                // Save obec to database
            }

            for (int i = 0; i < castObceList.getLength(); i++) {
                Element castObceElement = (Element) castObceList.item(i);
                String kod = castObceElement.getElementsByTagName("vf:Kod").item(0).getTextContent();
                String nazev = castObceElement.getElementsByTagName("vf:Nazev").item(0).getTextContent();
                String kodObec = castObceElement.getElementsByTagName("vf:KodObec").item(0).getTextContent();
                // Save cast obce to database
            }

        } catch (Exception e) {
            log.error("Failed to parse XML file. Error: {}", e.getMessage());
        }
    }
}
