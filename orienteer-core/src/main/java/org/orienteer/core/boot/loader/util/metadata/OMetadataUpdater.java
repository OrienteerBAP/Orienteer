package org.orienteer.core.boot.loader.util.metadata;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import static org.orienteer.core.boot.loader.util.metadata.MetadataUtil.*;

/**
 * @author Vitaliy Gonchar
 */
class OMetadataUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(OMetadataUpdater.class);

    private final Path pathToMetadata;

    @VisibleForTesting OMetadataUpdater(Path pathToMetadata) {
        this.pathToMetadata = pathToMetadata;
    }

    @VisibleForTesting void create(List<OModuleMetadata> modules) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(METADATA);
        addModules(modules, root);
        writeToFile(document);
    }

    @VisibleForTesting void update(OModuleMetadata module) {
        Document document = readFromFile();
        if (document == null) throw new UnsupportedOperationException("Cannot open metadata.xml for update it.");
        Element rootElement = document.getRootElement();
        List<Node> modules = rootElement.elements(MODULE);
        boolean isUpdate = false;
        for (Node node : modules) {
            Element element = (Element) node;
            Element idElement = (Element) element.elements(ID).get(0);
            if (Integer.valueOf(idElement.getText()) == module.getId()) {
                Iterator iterator = element.elementIterator();
                changeModule(iterator, module);
                isUpdate = true;
                break;
            }
        }
        if (!isUpdate) addModule(module, rootElement);

        writeToFile(document);
    }

    void update(List<OModuleMetadata> modulesForWrite) {
        Document document = readFromFile();
        if (document == null) throw new UnsupportedOperationException("Cannot open metadata.xml for update it.");
        Element rootElement = document.getRootElement();
        List<Node> modules = rootElement.elements(MODULE);
        List<OModuleMetadata> updatedModules = Lists.newArrayList();
        int id = 0;
        for (Node node : modules) {
            Element element = (Element) node;
            Element idElement = (Element) element.elements(ID).get(0);
            int currentId = Integer.valueOf(idElement.getText());
            if (id < currentId) id = currentId;
            OModuleMetadata module = containsInModulesList(currentId, modulesForWrite);
            if (module != null) {
                Iterator iterator = element.elementIterator();
                changeModule(iterator, module);
                updatedModules.add(module);
            }
        }
        if (updatedModules.size() != modulesForWrite.size()) {
            setIdForModules(modulesForWrite, ++id);
            addModules(difference(updatedModules, modulesForWrite), rootElement);
        }
        writeToFile(document);
    }

    @VisibleForTesting void delete(OModuleMetadata module) {
        Document document = readFromFile();
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator(MODULE);
        while (iterator.hasNext()) {
            Element element = iterator.next();
            String id = element.element(ID).getText();
            if (id.equals(Integer.toString(module.getId()))) {
                iterator.remove();
                break;
            }
        }
        writeToFile(document);
    }

    void delete(List<OModuleMetadata> modules) {
        Document document = readFromFile();
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator(MODULE);
        while (iterator.hasNext()) {
            Element element = iterator.next();
            String id = element.element(ID).getText();
            OModuleMetadata metadata = containsInModulesList(Integer.valueOf(id), modules);
            if (metadata != null) {
                iterator.remove();
            }
        }
        writeToFile(document);
    }

    private void addModules(List<OModuleMetadata> modules, Element root) {
        for (OModuleMetadata module : modules) {
            addModule(module, root);
        }
    }

    private void addModule(OModuleMetadata module, Element root) {
        Element moduleTag = root.addElement(MODULE);
        moduleTag.addElement(ID).addText("" + module.getId());
        moduleTag.addElement(INITIALIZER).addText(module.getInitializerName());
        moduleTag.addElement(LOAD).addText(Boolean.toString(module.isLoad()));
        Element maven = moduleTag.addElement(MAVEN);
        addMavenDependency(module.getMainArtifact(), maven, MAIN_DEPENDENCY);
        Element dependencies = maven.addElement(DEPENDENCIES);
        for (Artifact artifact : module.getDependencies()) {
            addMavenDependency(artifact, dependencies, DEPENDENCY);
        }
    }

    private void changeModule(Iterator iterator, OModuleMetadata module) {
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            switch (element.getName()) {
                case INITIALIZER:
                    element.setText(module.getInitializerName());
                    break;
                case LOAD:
                    element.setText(Boolean.toString(module.isLoad()));
                    break;
                case MAVEN:
                    Element mainDependency = element.element(MAIN_DEPENDENCY);
                    Element dependencies = element.element(DEPENDENCIES);
                    changeMavenDependency(mainDependency, module.getMainArtifact());
                    changeMavenDependencies(dependencies.elements(DEPENDENCY), module.getDependencies());
                    break;
            }
        }
    }

    private void changeMavenDependencies(List<Node> dependencies, List<Artifact> dependenciesForUpdate) {
        for (Node node : dependencies) {
            Element element = (Element) node;
            Artifact dependencyForChange = containsInDependencies(element, dependenciesForUpdate);
            if (dependencyForChange != null) {
                changeMavenDependency(element, dependencyForChange);
            }
        }
    }

    private void changeMavenDependency(Element dependency, Artifact artifact) {
        Iterator iterator = dependency.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            switch (element.getName()) {
                case GROUP_ID:
                    element.setText(artifact.getGroupId());
                    break;
                case ARTIFACT_ID:
                    element.setText(artifact.getArtifactId());
                    break;
                case VERSION:
                    element.setText(artifact.getVersion());
                    break;
                case JAR:
                    element.setText(artifact.getFile().getAbsolutePath());
                    break;
            }
        }
    }

    private OModuleMetadata containsInModulesList(int id, List<OModuleMetadata> modules) {
        for (OModuleMetadata module : modules) {
            if (id == module.getId()) return module;
        }
        return null;
    }

    private List<OModuleMetadata> difference(List<OModuleMetadata> list1, List<OModuleMetadata> list2) {
        List<OModuleMetadata> result = Lists.newArrayList();
        for (OModuleMetadata module : list2) {
            if (!list1.contains(module)) result.add(module);
        }
        return result;
    }

    private Artifact containsInDependencies(Element element, List<Artifact> dependencies) {
        String groupId = element.element(GROUP_ID).getText();
        String artifactId = element.element(ARTIFACT_ID).getText();
        for (Artifact dependency : dependencies) {
            if (dependency.getGroupId().equals(groupId) && dependency.getArtifactId().equals(artifactId)) {
                return dependency;
            }
        }
        return null;
    }

    private void setIdForModules(List<OModuleMetadata> modules, int id) {
        for (OModuleMetadata module : modules) {
            module.setId(id);
            id++;
        }
    }

    private Element addMavenDependency(Artifact artifact, Element element, String tag) {
        Element mavenElement = element.addElement(tag);
        mavenElement.addElement(GROUP_ID).addText(artifact.getGroupId());
        mavenElement.addElement(ARTIFACT_ID).addText(artifact.getArtifactId());
        mavenElement.addElement(VERSION).addText(artifact.getVersion());
        mavenElement.addElement(JAR).addText(artifact.getFile().getAbsolutePath());
        return mavenElement;
    }

    private Document readFromFile() {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(pathToMetadata.toFile());
            return document;
        } catch (DocumentException ex) {
            LOG.error("Cannot read metadata.xml", ex);
        }
        return null;
    }

    private void writeToFile(Document document) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer;
        try {
            writer = new XMLWriter(Files.newOutputStream(pathToMetadata), format);
            writer.write(document);
        } catch (IOException e) {
            LOG.error("Cannot writeToFile to metadata.xml", e);
        }
    }
}
