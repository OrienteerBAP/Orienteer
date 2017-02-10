package org.orienteer.core.loader.util.metadata;

import com.google.common.base.Optional;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.orienteer.core.loader.ODependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.orienteer.core.loader.util.metadata.MetadataUtil.*;

/**
 * @author Vitaliy Gonchar
 */
abstract class UpdateMetadata {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateMetadata.class);

    static Optional<Path> updateMetadata(OModuleMetadata moduleMetadata, Path metadataPath) {
        if (metadataPath == null) return Optional.absent();

        List<OModuleMetadata> metadata = readMetadata();
        if (metadata.contains(moduleMetadata)) {
            LOG.info("Module is already in metadata.xml: " + moduleMetadata);
            return Optional.of(metadataPath);
        }
        if (isExistsOModuleMetadata(moduleMetadata, metadata)) {
            modifyExistsOModuleMetadata(moduleMetadata, metadataPath);
        } else createNewOModuleMetadata(moduleMetadata, metadataPath);

        return Optional.of(metadataPath);
    }

    private static void createNewOModuleMetadata(OModuleMetadata moduleMetadata, Path metadataPath) {
        try {
            Document document = getDocument(metadataPath);
            Element rootElement = document.getRootElement();
            rootElement.addContent(createModule(moduleMetadata));
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, Files.newOutputStream(metadataPath));
        } catch (IOException | JDOMException e) {
            LOG.error("Cannot open file to read: " + metadataPath);
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    private static void modifyExistsOModuleMetadata(OModuleMetadata moduleMetadata, Path metadataPath) {
        try {
            Document document = getDocument(metadataPath);
            Element rootElement = document.getRootElement();
            List<Element> children = rootElement.getChildren(MODULE);
            ODependency dependency = moduleMetadata.getDependency();
            for (Element element : children) {
                Element child = element.getChild(ID);
                String value = child.getValue();
                if (value != null && value.equals(Integer.toString(moduleMetadata.getId()))) {
                    element.removeChild(INITIALIZER);
                    element.removeChild(TRUSTED);
                    element.removeChild(LOAD);
                    element.removeChild(MAVEN);
                    element.addContent(new Element(INITIALIZER).setText(moduleMetadata.getInitializerName()));
                    element.addContent(new Element(TRUSTED).setText(Boolean.toString(moduleMetadata.isTrusted())));
                    element.addContent(new Element(LOAD).setText(Boolean.toString(moduleMetadata.isLoad())));
                    element.addContent(new Element(MAVEN)
                            .addContent(new Element(GROUP_ID).setText(dependency.getGroupId()))
                            .addContent(new Element(ARTIFACT_ID).setText(dependency.getArtifactId()))
                            .addContent(new Element(VERSION).setText(dependency.getArtifactVersion()))
                    );
                    break;
                }
            }
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, Files.newOutputStream(metadataPath));
        } catch (IOException | JDOMException e) {
            LOG.error("Cannot open file to read: " + metadataPath);
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    private static Element createModule(OModuleMetadata moduleMetadata) {
        ODependency dependency = moduleMetadata.getDependency();
        Element element = new Element(MODULE);
        element.addContent(new Element(INITIALIZER).setText(moduleMetadata.getInitializerName()));
        element.addContent(new Element(TRUSTED).setText(Boolean.toString(moduleMetadata.isTrusted())));
        element.addContent(new Element(LOAD).setText(Boolean.toString(moduleMetadata.isLoad())));
        element.addContent(new Element(MAVEN)
                .addContent(new Element(GROUP_ID).setText(dependency.getGroupId()))
                .addContent(new Element(ARTIFACT_ID).setText(dependency.getArtifactId()))
                .addContent(new Element(VERSION).setText(dependency.getArtifactVersion()))
        );
        return element;
    }

    private static boolean isExistsOModuleMetadata(OModuleMetadata moduleMetadata, List<OModuleMetadata> metadata) {
        boolean modify = false;
        for (OModuleMetadata m : metadata) {
            String initializerName = m.getInitializerName();
            if (initializerName.equals(moduleMetadata.getInitializerName())) {
                modify = true;
                break;
            }
        }
        return modify;
    }

    private static Document getDocument(Path metadataXml) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(Files.newInputStream(metadataXml));
    }



//    private static XMLStreamWriter getStreamWriter(Path metadataXml) throws IOException, XMLStreamException {
//        XMLOutputFactory factory = XMLOutputFactory.newFactory();
//        if (!Files.exists(metadataXml)) Files.createFile(metadataXml);
//        return factory.createXMLStreamWriter(Files.newOutputStream(metadataXml, StandardOpenOption.WRITE));
//    }
//
//    private static XMLEventWriter getWriter(Path metadataXml) throws IOException, XMLStreamException {
//        XMLOutputFactory factory = XMLOutputFactory.newFactory();
//        if (!Files.exists(metadataXml)) Files.createFile(metadataXml);
//        OutputStream outputStream = Files.newOutputStream(metadataXml, StandardOpenOption.WRITE);
//        return factory.createXMLEventWriter(outputStream);
//    }
//
//    private static XMLEventReader getReader(Path metadataXml) throws IOException, XMLStreamException {
//        XMLInputFactory factory = XMLInputFactory.newFactory();
//        return factory.createXMLEventReader(Files.newInputStream(metadataXml));
//    }
//    private static void update(OModuleMetadata metadata, Path metadataPath) {
//        try {
//            Path temp = Paths.get("tmp/metadata-temp.xml");
//            if (!Files.exists(temp)) Files.createFile(temp);
//            XMLEventReader xmlReader = getReader(metadataPath);
//            XMLEventWriter xmlWriter = getWriter(temp);
//            XMLEventFactory ef = XMLEventFactory.newFactory();
//
//            XMLEvent changedData = null;
//            boolean modify = false;
//            boolean isId = false;
//            while (xmlReader.hasNext()) {
//                XMLEvent xmlEvent = xmlReader.nextEvent();
//                if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().toString().equals(ID)) {
//                    isId = true;
//                } else if (modify && xmlEvent.isEndElement()
//                        && xmlEvent.asEndElement().getName().toString().equals(MAVEN)) {
//                    modify = false;
//                }
//
//                if (modify && xmlEvent.isStartElement()) {
//                    String moduleValue = getModuleValue(xmlEvent.asStartElement().getName().toString(), metadata);
//                    if (moduleValue != null) changedData = ef.createCharacters(moduleValue);
//                }
//                if (isId && xmlEvent.isCharacters()) {
//                    String data = xmlEvent.asCharacters().getData();
//                    if (data.equals(Integer.toString(metadata.getId()))) {
//                        modify = true;
//                        isId = false;
//                    }
//                } else if (changedData != null && xmlEvent.isCharacters()) {
//                    Characters characters = xmlEvent.asCharacters();
//                    if (!characters.equals(changedData.asCharacters())) xmlEvent = changedData;
//                    changedData = null;
//                }
//                xmlWriter.add(xmlEvent);
//            }
//            xmlReader.close();
//            if (xmlWriter != null) xmlWriter.close();
//        } catch (IOException | XMLStreamException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String getModuleValue(String type, OModuleMetadata moduleMetadata) {
//        String metadataValue = null;
//        switch (type) {
//            case INITIALIZER:
//                metadataValue = moduleMetadata.getInitializerName();
//                break;
//            case TRUSTED:
//                metadataValue = Boolean.toString(moduleMetadata.isTrusted());
//                break;
//            case LOAD:
//                metadataValue = Boolean.toString(moduleMetadata.isLoad());
//                break;
//            case GROUP_ID:
//                metadataValue = moduleMetadata.getDependency().getGroupId();
//                break;
//            case ARTIFACT_ID:
//                metadataValue = moduleMetadata.getDependency().getArtifactId();
//                break;
//            case VERSION:
//                metadataValue = moduleMetadata.getDependency().getArtifactVersion();
//                break;
//        }
//        return metadataValue;
//    }
}
