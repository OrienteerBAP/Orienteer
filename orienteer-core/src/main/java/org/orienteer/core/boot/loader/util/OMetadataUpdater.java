package org.orienteer.core.boot.loader.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.aether.artifact.Artifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
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
    void create(List<OArtifact> oArtifacts) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(MetadataTag.METADATA.get());
        addModules(oArtifacts, root);
        writeToFile(document);
    }

    @VisibleForTesting
    void update(OArtifact oArtifact) {
        update(oArtifact, false);
    }

    void update(List<OArtifact> oArtifacts) {
        update(oArtifacts, false);
    }

    void update(OArtifact oArtifact, boolean updateJar) {
        update(Lists.newArrayList(oArtifact), updateJar);
    }

    @SuppressWarnings("unchecked")
    void update(List<OArtifact> oArtifacts, boolean updateJar) {
        Document document = readFromFile();
        if (document == null) throw new UnsupportedOperationException("Cannot open metadata.xml for update it.");
        Element rootElement = document.getRootElement();
        List<Node> modules = rootElement.elements(MetadataTag.MODULE.get());
        List<OArtifact> updatedModules = Lists.newArrayList();
        for (Node node : modules) {
            Element element = (Element) node;
            Element dependencyElement = element.element(MetadataTag.DEPENDENCY.get());
            OArtifact oArtifact = containsInModulesConfigsList(dependencyElement, oArtifacts);
            if (oArtifact != null) {
                if (updateJar) {
                    changeoArtifactsLoadAndJar(element, oArtifact);
                } else changeModule(element, oArtifact);
                updatedModules.add(oArtifact);
            }
        }
        if (updatedModules.size() != oArtifacts.size()) {
            addModules(difference(updatedModules, oArtifacts), rootElement);
        }
        writeToFile(document);
    }

    @SuppressWarnings("unchecked")
    void update(OArtifact moduleConfigForUpdate, OArtifact newModuleConfig) {
        Document document = readFromFile();
        if (document == null) throw new UnsupportedOperationException("Cannot open metadata.xml for update it.");
        Element rootElement = document.getRootElement();
        List<Node> modules = rootElement.elements(MetadataTag.MODULE.get());
        for (Node node : modules) {
            Element element = (Element) node;
            Element dependencyElement = element.element(MetadataTag.DEPENDENCY.get());
            OArtifact module = containsInModulesConfigsList(dependencyElement, Lists.newArrayList(moduleConfigForUpdate));
            if (module != null) {
                changeModule(element, newModuleConfig);
                break;
            }
        }

        writeToFile(document);
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    void delete(OArtifact oArtifact) {
        Document document = readFromFile();
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator(MetadataTag.MODULE.get());
        while (iterator.hasNext()) {
            Element element = iterator.next();
            Element dependencyElement = element.element(MetadataTag.DEPENDENCY.get());
            if (isNecessaryElement(dependencyElement, oArtifact)) {
                iterator.remove();
                break;
            }
        }
        writeToFile(document);
    }

    @SuppressWarnings("unchecked")
    void delete(List<OArtifact> oArtifacts) {
        Document document = readFromFile();
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator(MetadataTag.MODULE.get());
        while (iterator.hasNext()) {
            Element element = iterator.next();
            Element dependencyElement = element.element(MetadataTag.DEPENDENCY.get());
            OArtifact metadata = containsInModulesConfigsList(dependencyElement, oArtifacts);
            if (metadata != null) {
                iterator.remove();
            }
        }
        writeToFile(document);
    }

    private void addModules(List<OArtifact> oArtifacts, Element root) {
        for (OArtifact module : oArtifacts) {
            addModule(module, root);
        }
    }

    private void addModule(OArtifact oArtifact, Element root) {
        Element moduleTag = root.addElement(MetadataTag.MODULE.get());
        moduleTag.addElement(MetadataTag.LOAD.get()).addText(Boolean.toString(oArtifact.isLoad()));
        moduleTag.addElement(MetadataTag.TRUSTED.get()).addText(Boolean.toString(oArtifact.isTrusted()));
        addMavenDependency(oArtifact.getArtifactReference(), moduleTag, MetadataTag.DEPENDENCY.get());
    }

    @SuppressWarnings("unchecked")
    private void changeModule(Element moduleElement, OArtifact oArtifact) {
        Iterator<Element> iterator = moduleElement.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            MetadataTag tag = MetadataTag.getByName(element.getName());
            switch (tag) {
                case LOAD:
                    element.setText(Boolean.toString(oArtifact.isLoad()));
                    break;
                case TRUSTED:
                    element.setText(Boolean.toString(oArtifact.isTrusted()));
                    break;
                case DEPENDENCY:
                    changeMavenDependency(element, oArtifact.getArtifactReference());
                    break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void changeoArtifactsLoadAndJar(Element moduleElement,
                                                       OArtifact oArtifact) {
        Iterator<Element> iterator = moduleElement.elementIterator();
        boolean isUpdate = false;
        while (iterator.hasNext() && !isUpdate) {
            Element element = iterator.next();
            MetadataTag tag = MetadataTag.getByName(element.getName());
            switch (tag) {
                case LOAD:
                    element.setText(Boolean.toString(oArtifact.isLoad()));
                    break;
                case DEPENDENCY:
                    Element dependency = moduleElement.element(MetadataTag.DEPENDENCY.get());
                    Iterator<Element> depIterator = dependency.elementIterator();
                    while (depIterator.hasNext()) {
                        Element jarElement = depIterator.next();
                        MetadataTag jarTag = MetadataTag.getByName(jarElement.getName());
                        if (jarTag == MetadataTag.JAR) {
                            jarElement.setText(oArtifact.getArtifactReference().getFile().getAbsolutePath());
                            isUpdate = true;
                        }
                    }
                    break;
            }
        }
        if (!isUpdate) {
            Element jarElement = moduleElement.addElement(MetadataTag.JAR.get());
            jarElement.setText(oArtifact.getArtifactReference().getFile().getAbsolutePath());
        }
    }

    private void changeMavenDependency(Element dependency, OArtifactReference artifact) {
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
                case REPOSITORY:
                    element.setText(artifact.getRepository());
                    break;
                case DESCRIPTION:
                    element.setText(artifact.getDescription());
                    break;
                case JAR:
                    element.setText(artifact.getFile().getAbsolutePath());
                    break;
            }
        }
    }

    private boolean isNecessaryElement(Element dependencyElement, OArtifact module) {
        Element groupElement = (Element) dependencyElement.elements(MetadataTag.GROUP_ID.get()).get(0);
        Element artifactElement = (Element) dependencyElement.elements(MetadataTag.ARTIFACT_ID.get()).get(0);
        OArtifactReference artifact = module.getArtifactReference();
        return groupElement.getText().equals(artifact.getGroupId())
                && artifactElement.getText().equals(artifact.getArtifactId());
    }

    private OArtifact containsInModulesConfigsList(Element dependencyElement, List<OArtifact> modulesConfigs) {
        for (OArtifact moduleConfig : modulesConfigs) {
            if (isNecessaryElement(dependencyElement, moduleConfig)) return moduleConfig;
        }
        return null;
    }

    private List<OArtifact> difference(List<OArtifact> list1, List<OArtifact> list2) {
        List<OArtifact> result = Lists.newArrayList();
        for (OArtifact module : list2) {
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

    private Element addMavenDependency(OArtifactReference artifact, Element element, String tag) {
        Element mavenElement = element.addElement(tag);
        mavenElement.addElement(MetadataTag.GROUP_ID.get()).addText(artifact.getGroupId());
        mavenElement.addElement(MetadataTag.ARTIFACT_ID.get()).addText(artifact.getArtifactId());
        mavenElement.addElement(MetadataTag.VERSION.get()).addText(artifact.getVersion());
        mavenElement.addElement(MetadataTag.DESCRIPTION.get()).addText(artifact.getDescription());
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
