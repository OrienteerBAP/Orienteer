package org.orienteer.core.service.loader.util.metadata;

import com.google.common.collect.Lists;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 * Utility class for read metadata.xml
 */
class OModuleReader {
    private final Path pathToMetadataXml;
    private final XMLEventReader xmlReader;
    private final boolean load;
    private final boolean allModules;

    private static final Logger LOG = LoggerFactory.getLogger(OModuleReader.class);

    private OModuleReader(OModuleReaderBuilder builder) {
        this.pathToMetadataXml = builder.pathToMetadataXml;
        this.xmlReader = builder.xmlReader;
        this.load = builder.load;
        this.allModules = builder.allModules;
    }

    static class OModuleReaderBuilder {
        private final Path pathToMetadataXml;
        private XMLEventReader xmlReader = null;
        private boolean load             = true;
        private boolean allModules       = false;

        OModuleReaderBuilder(Path pathToMetadataXml) {
            this.pathToMetadataXml = pathToMetadataXml;
        }

        OModuleReader build() {
            try {
                xmlReader = getReader();
                return new OModuleReader(this);
            } catch (NoSuchFileException ex) {
                LOG.warn("Cannot open file. File does not exists! File: " + pathToMetadataXml.toAbsolutePath());
            } catch (XMLStreamException | IOException e) {
                LOG.error("Cannot open file to read: " + pathToMetadataXml);
                if (LOG.isDebugEnabled()) e.printStackTrace();
            }
            return null;
        }

        OModuleReaderBuilder setLoad(boolean load) {
            this.load = load;
            return this;
        }

        OModuleReaderBuilder setAllModules(boolean allModules) {
            this.allModules = allModules;
            return this;
        }

        private XMLEventReader getReader() throws XMLStreamException, IOException {
            XMLInputFactory factory = XMLInputFactory.newFactory();
            InputStream in = Files.newInputStream(pathToMetadataXml);
            XMLEventReader reader = factory.createXMLEventReader(in);
            return reader;
        }
    }


    List<OModuleMetadata> read() {
        List<OModuleMetadata> metadata = Lists.newArrayList();
        try {
            if (allModules) {
                metadata = readAllModules();
            } else if (load) {
                metadata = readLoadedModules(load);
            }
        } catch (XMLStreamException ex) {
            LOG.error("Cannot read file: " + pathToMetadataXml.toAbsolutePath());
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
        return metadata;
    }

    private List<OModuleMetadata> readAllModules() throws XMLStreamException {
        List<OModuleMetadata> metadata = Lists.newArrayList();
        while (xmlReader.hasNext()) {
            XMLEvent xmlEvent = xmlReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                String tag = xmlEvent.asStartElement().getName().toString();
                if (tag.equals(MetadataUtil.MODULE)) {
                    metadata.add(getModuleMetadata());
                }
            }
        }
        return metadata;
    }


    private List<OModuleMetadata> readLoadedModules(boolean load) throws XMLStreamException {
        List<OModuleMetadata> metadata = readAllModules();
        Iterator<OModuleMetadata> iterator = metadata.iterator();
        while (iterator.hasNext()) {
            OModuleMetadata module = iterator.next();
            if (module.isLoad() != load) {
                iterator.remove();
            }
        }
        return metadata;
    }

    private OModuleMetadata getModuleMetadata() throws XMLStreamException {
        OModuleMetadata moduleMetadata = new OModuleMetadata();
        String groupId      = null;
        String artifactId   = null;
        String version      = null;
        String file         = null;
        while (xmlReader.hasNext()) {
            XMLEvent xmlEvent = xmlReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                String tag = xmlEvent.asStartElement().getName().toString();
                switch (tag) {
                    case MetadataUtil.ID:
                        moduleMetadata.setId(Integer.parseInt(xmlReader.getElementText()));
                        break;
                    case MetadataUtil.INITIALIZER:
                        moduleMetadata.setInitializerName(xmlReader.getElementText());
                        break;
                    case MetadataUtil.LOAD:
                        String load = xmlReader.getElementText();
                        moduleMetadata.setLoad(Boolean.parseBoolean(load));
                        break;
                    case MetadataUtil.GROUP_ID:
                        groupId = xmlReader.getElementText();
                        break;
                    case MetadataUtil.ARTIFACT_ID:
                        artifactId = xmlReader.getElementText();
                        break;
                    case MetadataUtil.VERSION:
                        version = xmlReader.getElementText();
                        break;
                    case MetadataUtil.JAR:
                        file = xmlReader.getElementText();
                        break;
                    case MetadataUtil.DEPENDENCIES:
                        moduleMetadata.setDependencies(getDependencies());
                        break;
                }
            } else if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().toString().equals(MetadataUtil.MODULE)) {
                break;
            }
        }
        moduleMetadata.setMainArtifact(getArtifact(groupId, artifactId, version, file));
        return moduleMetadata;
    }

    private List<Artifact> getDependencies() throws XMLStreamException {
        List<Artifact> dependencies = Lists.newArrayList();
        String file       = null;
        String groupId    = null;
        String artifactId = null;
        String versionId  = null;
        boolean depStart = false;
        boolean run = true;
        while (xmlReader.hasNext() && run) {
            XMLEvent xmlEvent = xmlReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                String tag = xmlEvent.asStartElement().getName().toString();
                switch (tag) {
                    case MetadataUtil.GROUP_ID:
                        groupId = xmlReader.getElementText();
                        break;
                    case MetadataUtil.ARTIFACT_ID:
                        artifactId = xmlReader.getElementText();
                        break;
                    case MetadataUtil.VERSION:
                        versionId = xmlReader.getElementText();
                        break;
                    case MetadataUtil.JAR:
                        file = xmlReader.getElementText();
                        break;
                    case MetadataUtil.DEPENDENCY:
                        depStart = true;
                        break;
                }
            } else if (xmlEvent.isEndElement()) {
                String tag = xmlEvent.asEndElement().getName().toString();
                switch (tag) {
                    case MetadataUtil.DEPENDENCY:
                        depStart = false;
                        break;
                    case MetadataUtil.DEPENDENCIES:
                        run = false;
                        break;
                }
            }

            if (!depStart && groupId != null && artifactId != null && versionId != null) {
                dependencies.add(getArtifact(groupId, artifactId, versionId, file));
                groupId    = null;
                artifactId = null;
                versionId  = null;
                file       = null;
            }
        }
        return dependencies;
    }

    private Artifact getArtifact(String groupId, String artifactId, String version, String file) {
        Artifact artifact = new DefaultArtifact(
                String.format("%s:%s:%s", groupId, artifactId, version));
        if (file != null) artifact = artifact.setFile(new File(file));
        return artifact;
    }
}
