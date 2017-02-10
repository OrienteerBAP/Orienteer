package org.orienteer.core.loader.util.metadata;

import com.google.common.base.Optional;
import org.orienteer.core.loader.ODependency;
import org.orienteer.core.loader.util.JarUtils;
import org.orienteer.core.loader.util.PomXmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.orienteer.core.loader.util.metadata.MetadataUtil.*;

/**
 * @author Vitaliy Gonchar
 */
abstract class CreateMetadata {
    private static final int METADATA_SPACES            = 2;
    private static final int MODULE_SPACES              = 4;
    private static final int MAVEN_SPACES               = 6;
    private static final int MAVEN_DEPENDENCY_SPACES    = 8;

    private static final Logger LOG = LoggerFactory.getLogger(CreateMetadata.class);

    static Optional<Path> createMetadata(Path metadata) {
        try {
            XMLStreamWriter xmlWriter = getWriter(metadata);
            if (xmlWriter == null) return Optional.absent();
            Set<Path> modulesInFolder = MetadataUtil.getModulesInFolder();
            xmlWriter.writeStartDocument("utf-8", "1.0");
            xmlWriter.writeCharacters(System.getProperty("line.separator"));
            writeModulesMetadata(xmlWriter, modulesInFolder);
            xmlWriter.writeEndDocument();
            xmlWriter.close();
        } catch (IOException ex) {
            LOG.error("Cannot open file to write: " + metadata);
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        } catch (XMLStreamException ex) {
            LOG.error("Cannot write data to file: " + metadata);
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
        return Optional.of(metadata);
    }

    private static XMLStreamWriter writeSpaces(XMLStreamWriter xmlWriter, final int spaces) throws XMLStreamException {
        for (int i = 0; i < spaces; i++) {
            xmlWriter.writeCharacters(" ");
        }
        return xmlWriter;
    }
    private static XMLStreamWriter writeModulesMetadata(XMLStreamWriter xmlWriter, Set<Path> modules) throws XMLStreamException {
        xmlWriter.writeStartElement(METADATA);
        xmlWriter.writeCharacters(System.getProperty("line.separator"));
        writeModulesDescription(xmlWriter, modules);
        xmlWriter.writeEndElement();
        xmlWriter.writeCharacters(System.getProperty("line.separator"));
        return xmlWriter;
    }

    private static XMLStreamWriter writeModulesDescription(XMLStreamWriter xmlWriter, Set<Path> modules) throws XMLStreamException {
        int id = 0;
        for (Path module : modules) {
            writeModule(xmlWriter, module, id);
            id++;
        }
        return xmlWriter;
    }

    private static XMLStreamWriter writeModule(XMLStreamWriter xmlWriter, Path module, int id) throws XMLStreamException {
        Optional<Path> pomOptional = JarUtils.getPomFromJar(module);
        Optional<String> initClassOptional = JarUtils.searchOrienteerInitModule(module);
        if (!pomOptional.isPresent()) return xmlWriter;
        if (!initClassOptional.isPresent()) return xmlWriter;
        Optional<ODependency> gavOptional = PomXmlUtils.readGroupArtifactVersionInPomXml(pomOptional.get());
        if (!gavOptional.isPresent()) return xmlWriter;
        writeSpaces(xmlWriter, METADATA_SPACES);
        xmlWriter.writeStartElement(MODULE);
        xmlWriter.writeCharacters(System.getProperty("line.separator"));
        write(xmlWriter, ID, Integer.toString(id), MODULE_SPACES);
        write(xmlWriter, INITIALIZER, initClassOptional.get(), MODULE_SPACES);
        write(xmlWriter, TRUSTED, TRUSTED_DEFAULT, MODULE_SPACES);
        write(xmlWriter, LOAD, LOAD_DEFAULT, MODULE_SPACES);
        writeMavenDependency(xmlWriter, gavOptional.get());

        writeSpaces(xmlWriter, METADATA_SPACES);
        xmlWriter.writeEndElement();
        xmlWriter.writeCharacters(System.getProperty("line.separator"));
        return xmlWriter;
    }

    private static XMLStreamWriter writeMavenDependency(XMLStreamWriter xmlWriter, ODependency dependency)
            throws XMLStreamException {
        writeSpaces(xmlWriter, MAVEN_SPACES);
        xmlWriter.writeStartElement(MAVEN);
        xmlWriter.writeCharacters(System.getProperty("line.separator"));

        write(xmlWriter, GROUP_ID, dependency.getGroupId(), MAVEN_DEPENDENCY_SPACES);
        write(xmlWriter, ARTIFACT_ID, dependency.getArtifactId(), MAVEN_DEPENDENCY_SPACES);
        write(xmlWriter, VERSION, dependency.getArtifactVersion(), MAVEN_DEPENDENCY_SPACES);

        writeSpaces(xmlWriter, MAVEN_SPACES);
        xmlWriter.writeEndElement();
        xmlWriter.writeCharacters(System.getProperty("line.separator"));
        return xmlWriter;
    }

    private static XMLStreamWriter write(XMLStreamWriter xmlWriter, String element, String data, int spaces)
            throws XMLStreamException {
        writeSpaces(xmlWriter, spaces);
        xmlWriter.writeStartElement(element);
        xmlWriter.writeCharacters(data);
        xmlWriter.writeEndElement();
        xmlWriter.writeCharacters(System.getProperty("line.separator"));
        return xmlWriter;
    }


    private static XMLStreamWriter getWriter(Path metadataXml) throws IOException, XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newFactory();
        return factory.createXMLStreamWriter(Files.newOutputStream(metadataXml));
    }
}
