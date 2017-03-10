package org.orienteer.core.boot.loader.util;

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

/**
 * @author Vitaliy Gonchar
 */
class OMetadataUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(OMetadataUpdater.class);

    private final Path pathToMetadata;

    @VisibleForTesting
    OMetadataUpdater(Path pathToMetadata) {
        this.pathToMetadata = pathToMetadata;
    }

    @VisibleForTesting
    void create(List<OModuleMetadata> modules) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(MetadataTag.METADATA.get());
        addModules(modules, root);
        writeToFile(document);
    }

    @VisibleForTesting
    void update(OModuleMetadata module) {
        update(module, false);
    }

    void update(List<OModuleMetadata> modules) {
        update(modules, false);
    }

    void update(OModuleMetadata module, boolean updateJar) {
        update(Lists.newArrayList(module), updateJar);
    }

    @SuppressWarnings("unchecked")
    void update(List<OModuleMetadata> modulesForWrite, boolean updateJar) {
        Document document = readFromFile();
        if (document == null) throw new UnsupportedOperationException("Cannot open metadata.xml for update it.");
        Element rootElement = document.getRootElement();
        List<Node> modules = rootElement.elements(MetadataTag.MODULE.get());
        List<OModuleMetadata> updatedModules = Lists.newArrayList();
        for (Node node : modules) {
            Element element = (Element) node;
            Element dependencyElement = element.element(MetadataTag.DEPENDENCY.get());
            OModuleMetadata module = containsInModulesList(dependencyElement, modulesForWrite);
            if (module != null) {
                if (updateJar) {
                    changeModulesJar(element, module);
                } else changeModule(element, module);
                updatedModules.add(module);
            }
        }
        if (updatedModules.size() != modulesForWrite.size()) {
            addModules(difference(updatedModules, modulesForWrite), rootElement);
        }
        writeToFile(document);
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    void delete(OModuleMetadata module) {
        Document document = readFromFile();
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator(MetadataTag.MODULE.get());
        while (iterator.hasNext()) {
            Element element = iterator.next();
            Element dependencyElement = element.element(MetadataTag.DEPENDENCY.get());
            if (isNecessaryElement(dependencyElement, module)) {
                iterator.remove();
                break;
            }
        }
        writeToFile(document);
    }

    @SuppressWarnings("unchecked")
    void delete(List<OModuleMetadata> modules) {
        Document document = readFromFile();
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator(MetadataTag.MODULE.get());
        while (iterator.hasNext()) {
            Element element = iterator.next();
            Element dependencyElement = element.element(MetadataTag.DEPENDENCY.get());
            OModuleMetadata metadata = containsInModulesList(dependencyElement, modules);
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
        Element moduleTag = root.addElement(MetadataTag.MODULE.get());
        moduleTag.addElement(MetadataTag.LOAD.get()).addText(Boolean.toString(module.isLoad()));
        moduleTag.addElement(MetadataTag.TRUSTED.get()).addText(Boolean.toString(module.isTrusted()));
        addMavenDependency(module.getMainArtifact(), moduleTag, MetadataTag.DEPENDENCY.get());
    }

    @SuppressWarnings("unchecked")
    private void changeModule(Element moduleElement, OModuleMetadata module) {
        Iterator<Element> iterator = moduleElement.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            MetadataTag tag = MetadataTag.getByName(element.getName());
            switch (tag) {
                case LOAD:
                    element.setText(Boolean.toString(module.isLoad()));
                    break;
                case TRUSTED:
                    element.setText(Boolean.toString(module.isTrusted()));
                    break;
                case DEPENDENCY:
                    changeMavenDependency(element, module.getMainArtifact());
                    break;
            }
        }
    }

    private void changeModulesJar(Element moduleElement, OModuleMetadata module) {
        Element dependency = moduleElement.element(MetadataTag.DEPENDENCY.get());
        Iterator<Element> iterator = dependency.elementIterator();
        boolean isUpdate = false;
        while (iterator.hasNext()) {
            Element element = iterator.next();
            MetadataTag tag = MetadataTag.getByName(element.getName());
            if (tag == MetadataTag.JAR) {
                element.setText(module.getMainArtifact().getFile().getAbsolutePath());
                isUpdate = true;
                break;
            }
        }
        if (!isUpdate) {
            Element jarElement = dependency.addElement(MetadataTag.JAR.get());
            jarElement.setText(module.getMainArtifact().getFile().getAbsolutePath());
        }
    }

    private void changeMavenDependency(Element dependency, Artifact artifact) {
        Iterator iterator = dependency.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            MetadataTag tag = MetadataTag.getByName(element.getName());
            switch (tag) {
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

    private boolean isNecessaryElement(Element dependencyElement, OModuleMetadata module) {
        Element groupElement = (Element) dependencyElement.elements(MetadataTag.GROUP_ID.get()).get(0);
        Element artifactElement = (Element) dependencyElement.elements(MetadataTag.ARTIFACT_ID.get()).get(0);
        Artifact artifact = module.getMainArtifact();
        return groupElement.getText().equals(artifact.getGroupId())
                && artifactElement.getText().equals(artifact.getArtifactId());
    }

    private OModuleMetadata containsInModulesList(Element dependencyElement, List<OModuleMetadata> modules) {
        for (OModuleMetadata module : modules) {
            if (isNecessaryElement(dependencyElement, module)) return module;
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
        String groupId = element.element(MetadataTag.GROUP_ID.get()).getText();
        String artifactId = element.element(MetadataTag.ARTIFACT_ID.get()).getText();
        for (Artifact dependency : dependencies) {
            if (dependency.getGroupId().equals(groupId) && dependency.getArtifactId().equals(artifactId)) {
                return dependency;
            }
        }
        return null;
    }

    private Element addMavenDependency(Artifact artifact, Element element, String tag) {
        Element mavenElement = element.addElement(tag);
        mavenElement.addElement(MetadataTag.GROUP_ID.get()).addText(artifact.getGroupId());
        mavenElement.addElement(MetadataTag.ARTIFACT_ID.get()).addText(artifact.getArtifactId());
        mavenElement.addElement(MetadataTag.VERSION.get()).addText(artifact.getVersion());
        mavenElement.addElement(MetadataTag.JAR.get()).addText(artifact.getFile().getAbsolutePath());
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
