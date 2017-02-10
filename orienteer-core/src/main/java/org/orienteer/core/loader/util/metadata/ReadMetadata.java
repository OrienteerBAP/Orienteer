package org.orienteer.core.loader.util.metadata;

import com.google.common.collect.Lists;
import org.orienteer.core.loader.ODependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.orienteer.core.loader.util.metadata.MetadataUtil.*;

/**
 * @author Vitaliy Gonchar
 */
abstract class ReadMetadata {
    private static final Logger LOG = LoggerFactory.getLogger(ReadMetadata.class);

    public static List<OModuleMetadata> readMetadata(Path metadataPath) {
        if (!Files.exists(metadataPath)) return Lists.newArrayList();
        List<OModuleMetadata> modules = Lists.newArrayList();
        try {
            XMLStreamReader xmlReader = XMLInputFactory.newFactory()
                    .createXMLStreamReader(Files.newInputStream(metadataPath));
            while (xmlReader.hasNext()) {
                int next = xmlReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    String localName = xmlReader.getLocalName();
                    if (localName.equalsIgnoreCase(MODULE)) {
                        OModuleMetadata optionalMetadata = getModuleMetadata(xmlReader);
                        if (optionalMetadata != null) modules.add(optionalMetadata);
                    }
                }
            }
            xmlReader.close();
        } catch (IOException e) {
            LOG.error("Cannot open file to read: " + metadataPath);
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }catch (XMLStreamException e) {
            LOG.error("Cannot read file: " + metadataPath);
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return modules;
    }

    private static OModuleMetadata getModuleMetadata(XMLStreamReader xmlReader) throws XMLStreamException {
        OModuleMetadata moduleMetadata = new OModuleMetadata();
        boolean run = true;
        String groupId      = null;
        String artifactId   = null;
        String version      = null;
        while (xmlReader.hasNext() && run) {
            int next = xmlReader.next();
            if (next == XMLStreamReader.START_ELEMENT) {
                String entryName = xmlReader.getLocalName();
                switch (entryName) {
                    case ID:
                        moduleMetadata.setId(Integer.parseInt(xmlReader.getElementText()));
                        break;
                    case INITIALIZER:
                        moduleMetadata.setInitializerName(xmlReader.getElementText());
                        break;
                    case TRUSTED:
                        String trusted = xmlReader.getElementText();
                        moduleMetadata.setTrusted(Boolean.parseBoolean(trusted));
                        break;
                    case LOAD:
                        String load = xmlReader.getElementText();
                        moduleMetadata.setLoad(Boolean.parseBoolean(load));
                        break;
                    case GROUP_ID:
                        groupId = xmlReader.getElementText();
                        break;
                    case ARTIFACT_ID:
                        artifactId = xmlReader.getElementText();
                        break;
                    case VERSION:
                        version = xmlReader.getElementText();
                        run = false;
                        break;
                }
            } else if (next == XMLStreamReader.END_ELEMENT && xmlReader.getLocalName().equals(MODULE)) {
                run = false;
            }
        }
        moduleMetadata.setDependency(new ODependency(groupId, artifactId, version));
        return moduleMetadata;
    }
}
