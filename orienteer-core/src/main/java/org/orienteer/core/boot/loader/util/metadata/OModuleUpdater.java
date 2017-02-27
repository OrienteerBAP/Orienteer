package org.orienteer.core.boot.loader.util.metadata;

import com.google.common.collect.Lists;
import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 * Utility class for create or update metadata.xml
 */
class OModuleUpdater {
    private final XMLEventReader xmlReader;
    private final XMLEventWriter xmlWriter;
    private final List<OModuleMetadata> modulesToUpdate;
    private final XMLEventFactory ef;
    private final Path pathToMetadata;
    private final Path pathToTempMetadata;
    private final boolean delete;
    private static final Logger LOG = LoggerFactory.getLogger(OModuleUpdater.class);

    private OModuleUpdater(OModuleUpdaterBuilder builder) {
        this.xmlReader = builder.xmlReader;
        this.xmlWriter = builder.xmlWriter;
        this.modulesToUpdate = builder.modulesToUpdate;
        this.pathToMetadata = builder.pathToMetadata;
        this.pathToTempMetadata = builder.pathToTempMetadata;
        this.ef = XMLEventFactory.newFactory();
        this.delete = builder.delete;
    }

    static class OModuleUpdaterBuilder {
        private final Path pathToMetadata;
        private XMLEventReader xmlReader              = null;
        private XMLEventWriter xmlWriter              = null;
        private Path pathToTempMetadata               = null;
        private boolean delete                        = false;
        private List<OModuleMetadata> modulesToUpdate = Lists.newArrayList();

        OModuleUpdaterBuilder(Path pathToMetadata) {
            this.pathToMetadata = pathToMetadata;
        }

        /**
         * @return new instance of OModuleUpdater
         */
        OModuleUpdater build() {
            return new OModuleUpdater(this);
        }

        /**
         * Create new metadata.xml
         * @param modules modules for write in metadata.xml
         * @return modified OModuleUpdaterBuilder
         */
        OModuleUpdaterBuilder setCreateNewMetadata(List<OModuleMetadata> modules) {
            if (modules == null) throw new IllegalArgumentException("Modules cannot be null!");
            xmlWriter = getWriter(pathToMetadata, true);
            modulesToUpdate = modules;
            return this;
        }

        /**
         * Add modules in end of metadata.xml file
         * @param modules modules for add in metadata.xml
         * @return modified OModuleUpdaterBuilder
         */
        OModuleUpdaterBuilder setAddModulesToExistsMetadata(List<OModuleMetadata> modules) {
            if (modules == null) throw new IllegalArgumentException("Modules cannot be null!");
            xmlReader = getReader(pathToMetadata);
            xmlWriter = getWriter(pathToMetadata, false);
            modulesToUpdate = modules;
            return this;
        }

        /**
         * Delete modules with same id and write new modules.
         * @param modules modules for overwrite exists modules
         * @return modified OModuleUpdaterBuilder
         */
        OModuleUpdaterBuilder setOverwriteExistsModulesInMetadata(List<OModuleMetadata> modules) {
            if (modules == null) throw new IllegalArgumentException("Modules cannot be null!");
            xmlReader = getReader(pathToMetadata);
            pathToTempMetadata = pathToMetadata.getParent().resolve(MetadataUtil.METADATA_TEMP);
            xmlWriter = getWriter(pathToTempMetadata, true);
            modulesToUpdate = modules;
            return this;
        }

        /**
         * Delete modules.
         * @param modules modules for delete
         * @return modified OModuleUpdaterBuilder
         */
        OModuleUpdaterBuilder setDelete(List<OModuleMetadata> modules) {
            if (modules == null) throw new IllegalArgumentException("Modules cannot be null!");
            xmlReader = getReader(pathToMetadata);
            pathToTempMetadata = pathToMetadata.getParent().resolve(MetadataUtil.METADATA_TEMP);
            xmlWriter = getWriter(pathToTempMetadata, true);
            modulesToUpdate = modules;
            this.delete = true;
            return this;
        }

        private XMLEventReader getReader(Path pathToMetadataXml)  {
            if (!Files.exists(pathToMetadataXml)) return null;
            try {
                return XMLInputFactory.newFactory().createXMLEventReader(Files.newInputStream(pathToMetadataXml));
            } catch (XMLStreamException | IOException e) {
                LOG.error("Cannot open file to read: " + pathToMetadataXml.toAbsolutePath());
                if (LOG.isDebugEnabled()) e.printStackTrace();
            }
            return null;
        }

        private XMLEventWriter getWriter(Path pathToMetadataXml, boolean overwrite) {
            try {
                if (!Files.exists(pathToMetadataXml)) Files.createFile(pathToMetadataXml);
                OutputStream out = overwrite ? Files.newOutputStream(pathToMetadataXml) :
                        Files.newOutputStream(pathToMetadataXml, StandardOpenOption.WRITE);
                return XMLOutputFactory.newFactory().createXMLEventWriter(out);
            } catch (IOException | XMLStreamException e) {
                LOG.error("Cannot open file to write: " + pathToMetadataXml);
                if (LOG.isDebugEnabled()) e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * Start write to metadata.xml
     * @throws IOException
     * @throws XMLStreamException
     */
    void write() throws IOException, XMLStreamException {
        if (xmlWriter == null) return;
        if (xmlReader == null) {
            createNewMetadata();
        } else if (pathToTempMetadata == null && !delete) {
            addModulesToExistsMetadata();
        } else if (delete) {
            deleteModules();
        } else {
            updateExistsMetadataWithOverwrite();
        }
        close();
    }

    private void createNewMetadata() throws XMLStreamException {
        startNewDocument();
        writeModules();
        endNewDocument();
    }

    private void addModulesToExistsMetadata() {
        try {
            int idCounter = 0;
            boolean isId = false;
            boolean run = true;
            while (xmlReader.hasNext() && run) {
                XMLEvent xmlEvent = xmlReader.nextEvent();
                if (isEndTag(xmlEvent, MetadataUtil.METADATA)) {
                    for (OModuleMetadata module : modulesToUpdate) {
                        idCounter++;
                        module.setId(idCounter);
                        writeModule(module);
                    }
                    run = false;
                }
                if (isStartTag(xmlEvent, MetadataUtil.ID)) {
                    isId = true;
                } else if (isId && xmlEvent.isCharacters()) {
                    String data = xmlEvent.asCharacters().getData();
                    idCounter = Integer.parseInt(data);
                    isId = false;
                }
                xmlWriter.add(xmlEvent);
            }
        } catch (XMLStreamException ex) {
            LOG.error("Cannot write to file: " + pathToMetadata);
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
    }

    private void updateExistsMetadataWithOverwrite() {
        try {
            XMLEvent changedData = null;
            boolean modify = false;
            boolean isId = false;
            OModuleMetadata moduleToUpdate = null;
            while (xmlReader.hasNext()) {
                XMLEvent xmlEvent = xmlReader.nextEvent();
                if (isStartTag(xmlEvent, MetadataUtil.ID)) {
                    isId = true;
                } else if (isStartTag(xmlEvent, MetadataUtil.DEPENDENCIES)) {
                    modify = false;
                }

                if (modify && xmlEvent.isStartElement()) {
                    String moduleValue = getModuleValue(xmlEvent.asStartElement().getName().toString(), moduleToUpdate);
                    if (moduleValue != null) changedData = ef.createCharacters(moduleValue);
                }
                if (isId && xmlEvent.isCharacters()) {
                    String data = xmlEvent.asCharacters().getData();
                    moduleToUpdate = getModuleById(Integer.parseInt(data));
                    if (moduleToUpdate != null) {
                        modify = true;
                    }
                    isId = false;
                } else if (changedData != null && xmlEvent.isCharacters()) {
                    Characters characters = xmlEvent.asCharacters();
                    if (!characters.equals(changedData.asCharacters())) {
                        xmlEvent = changedData;
                    }
                    changedData = null;
                }
                xmlWriter.add(xmlEvent);
            }
        } catch (XMLStreamException e) {
            LOG.error("Cannot read/write file: " + pathToMetadata.toAbsolutePath());
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
    }

    private void deleteModules() {
        try {
            List<XMLEvent> eventsToWrite = Lists.newArrayList();
            boolean write = true;
            boolean deleteModule = false;
            boolean isId = false;
            while (xmlReader.hasNext()) {
                XMLEvent xmlEvent = xmlReader.nextEvent();
                if (isStartTag(xmlEvent, MetadataUtil.MODULE)) {
                    write = false;
                } else if (isStartTag(xmlEvent, MetadataUtil.ID)) {
                    isId = true;
                } else if (isId && xmlEvent.isCharacters()) {
                    String data = xmlEvent.asCharacters().getData();
                    OModuleMetadata module = getModuleById(Integer.parseInt(data));
                    isId = false;
                    if (module == null) {
                        write = true;
                        for (XMLEvent event : eventsToWrite) {
                            xmlWriter.add(event);
                        }
                        eventsToWrite.clear();
                    } else deleteModule = true;
                } else if (isEndTag(xmlEvent, MetadataUtil.MODULE) && deleteModule) {
                    eventsToWrite.clear();
                    deleteModule = false;
                    write = true;
                    xmlReader.nextEvent();
                    xmlEvent = xmlReader.nextEvent();
                }
                if (write) {
                    xmlWriter.add(xmlEvent);
                } else {
                    eventsToWrite.add(xmlEvent);
                }
            }
        } catch (XMLStreamException ex) {
            LOG.error("Cannot read file: " + pathToMetadata
            + "\nor write to file: " + pathToTempMetadata);
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
    }

    private String getModuleValue(String tag, OModuleMetadata moduleMetadata) {
        String metadataValue = null;

        switch (tag) {
            case MetadataUtil.INITIALIZER:
                metadataValue = moduleMetadata.getInitializerName();
                break;
            case MetadataUtil.LOAD:
                metadataValue = Boolean.toString(moduleMetadata.isLoad());
                break;
            case MetadataUtil.GROUP_ID:
                metadataValue = moduleMetadata.getMainArtifact().getGroupId();
                break;
            case MetadataUtil.ARTIFACT_ID:
                metadataValue = moduleMetadata.getMainArtifact().getArtifactId();
                break;
            case MetadataUtil.VERSION:
                metadataValue = moduleMetadata.getMainArtifact().getVersion();
                break;
            case MetadataUtil.JAR:
                metadataValue = moduleMetadata.getMainArtifact().getFile().getAbsolutePath();
                break;
        }
        return metadataValue;
    }

    private void startNewDocument() throws XMLStreamException {
        xmlWriter.add(ef.createStartDocument("utf-8", "1.0"));
    }

    private void endNewDocument() throws XMLStreamException {
        xmlWriter.add(ef.createEndDocument());
    }

    private void writeModules() throws XMLStreamException {
        startElement(MetadataUtil.METADATA, 0);
        for (OModuleMetadata module : modulesToUpdate) {
            writeModule(module);
        }
        endElement(MetadataUtil.METADATA, 0);
    }

    private void writeModule(OModuleMetadata module) throws XMLStreamException {
        startElement(MetadataUtil.MODULE, MetadataUtil.TWO_SPACES);
        write(MetadataUtil.ID, Integer.toString(module.getId()), MetadataUtil.FOUR_SPACES);
        write(MetadataUtil.INITIALIZER, module.getInitializerName(), MetadataUtil.FOUR_SPACES);
        write(MetadataUtil.LOAD, Boolean.toString(module.isLoad()), MetadataUtil.FOUR_SPACES);

        mavenDescription(module.getMainArtifact(), module.getDependencies());
        endElement(MetadataUtil.MODULE, MetadataUtil.TWO_SPACES);
    }

    private void mavenDescription(Artifact mainArtifact, List<Artifact> dependencies)
            throws XMLStreamException {
        startElement(MetadataUtil.MAVEN, MetadataUtil.FOUR_SPACES);
        startElement(MetadataUtil.MAIN_DEPENDENCY, MetadataUtil.SIX_SPACES);
        mavenDependency(mainArtifact, MetadataUtil.EIGHT_SPACES);
        write(MetadataUtil.JAR, mainArtifact.getFile().getAbsolutePath(), MetadataUtil.EIGHT_SPACES);
        endElement(MetadataUtil.MAIN_DEPENDENCY, MetadataUtil.SIX_SPACES);

        startElement(MetadataUtil.DEPENDENCIES, MetadataUtil.SIX_SPACES);
        mavenDependencies(dependencies);
        endElement(MetadataUtil.DEPENDENCIES, MetadataUtil.SIX_SPACES);
        endElement(MetadataUtil.MAVEN, MetadataUtil.FOUR_SPACES);
    }

    private void mavenDependencies(List<Artifact> dependencies) throws XMLStreamException {
        for (Artifact dependency : dependencies) {
            startElement(MetadataUtil.DEPENDENCY, MetadataUtil.EIGHT_SPACES);
            mavenDependency(dependency, MetadataUtil.TEN_SPACES);
            write(MetadataUtil.JAR, dependency.getFile().getAbsolutePath(), MetadataUtil.TEN_SPACES);
            endElement(MetadataUtil.DEPENDENCY, MetadataUtil.EIGHT_SPACES);
        }
    }

    private void mavenDependency(Artifact artifact, int spaces) throws XMLStreamException {
        write(MetadataUtil.GROUP_ID, artifact.getGroupId(), spaces);
        write(MetadataUtil.ARTIFACT_ID, artifact.getArtifactId(), spaces);
        write(MetadataUtil.VERSION, artifact.getVersion(), spaces);
    }


    private void startElement(String element, int spaces) throws XMLStreamException {
        writeSpaces(spaces);
        xmlWriter.add(ef.createStartElement("", null, element));
        writeLineSeparator(1);
    }

    private void endElement(String element, int spaces) throws XMLStreamException {
        writeSpaces(spaces);
        xmlWriter.add(ef.createEndElement("", null, element));
        writeLineSeparator(1);
    }


    public void close() throws XMLStreamException, IOException {
        if (xmlReader != null) xmlReader.close();
        if (xmlWriter != null) xmlWriter.close();

        if (pathToTempMetadata != null) {
            Files.deleteIfExists(pathToMetadata);
            Files.move(pathToTempMetadata, pathToMetadata);
        }
    }


    private void write(String element, String data, int spaces)
            throws XMLStreamException {
        writeSpaces(spaces);
        xmlWriter.add(ef.createStartElement("", null, element));
        xmlWriter.add(ef.createCharacters(data));
        xmlWriter.add(ef.createEndElement("", null, element));
        writeLineSeparator(1);
    }

    private void writeSpaces(final int spaces) throws XMLStreamException {
        for (int i = 0; i < spaces; i++) {
            xmlWriter.add(ef.createSpace(" "));
        }
    }

    private void writeLineSeparator(final int lineSeparators) throws XMLStreamException {
        for (int i = 0; i < lineSeparators; i++) {
            xmlWriter.add(ef.createCharacters(System.getProperty("line.separator")));
        }
    }

    private boolean isStartTag(XMLEvent event, String tag) {
        return event.isStartElement() && event.asStartElement().getName().toString().equals(tag);
    }

    private boolean isEndTag(XMLEvent event, String tag) {
        return event.isEndElement() && event.asEndElement().getName().toString().equals(tag);
    }

    private OModuleMetadata getModuleById(int id) {
        Iterator<OModuleMetadata> iterator = modulesToUpdate.iterator();
        while (iterator.hasNext()) {
            OModuleMetadata module = iterator.next();
            if (module.getId() == id) {
                iterator.remove();
                return module;
            }
        }
        return null;
    }
}
