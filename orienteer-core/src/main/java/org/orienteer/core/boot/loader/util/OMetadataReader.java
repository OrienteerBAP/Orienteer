package org.orienteer.core.boot.loader.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
class OMetadataReader {
    private static final Logger LOG = LoggerFactory.getLogger(OMetadataReader.class);


    private final Path pathToMetadata;

    @VisibleForTesting OMetadataReader(Path pathToMetadata) {
        this.pathToMetadata = pathToMetadata;
    }

    @VisibleForTesting List<OModuleMetadata> readModulesForLoad() {
        List<OModuleMetadata> modules = read();
        List<OModuleMetadata> modulesForLoad = Lists.newArrayList();
        for (OModuleMetadata module : modules) {
            if (module.isLoad()) modulesForLoad.add(module);
        }
        return modulesForLoad;
    }

    @VisibleForTesting List<OModuleMetadata> readAllModules() {
        List<OModuleMetadata> modules = read();
        return modules;
    }

    private List<OModuleMetadata> read() {

        Document document = readFromFile();
        Element rootElement = document.getRootElement();
        List<OModuleMetadata> modules = getModulesInMetadataXml(rootElement.elements(MetadataUtil.MODULE));
        return modules;
    }


    private List<OModuleMetadata> getModulesInMetadataXml(List<Element> elements) {
        List<OModuleMetadata> modules = Lists.newArrayList();
        for (Element element : elements) {
            OModuleMetadata module = getModule(element);
            modules.add(module);
        }
        return modules;
    }

    private OModuleMetadata getModule(Element mainElement) {
        OModuleMetadata module = new OModuleMetadata();
        List<Element> elements = mainElement.elements();
        for (Element element : elements) {
            switch (element.getName()) {
                case MetadataUtil.ID:
                    module.setId(Integer.valueOf(element.getText()));
                    break;
                case MetadataUtil.INITIALIZER:
                    module.setInitializerName(element.getText());
                    break;
                case MetadataUtil.LOAD:
                    module.setLoad(Boolean.valueOf(element.getText()));
                    break;
                case MetadataUtil.MAVEN:
                    Artifact mainDependency = getMavenDependency(element.element(MetadataUtil.MAIN_DEPENDENCY));
                    List<Artifact> dependencies = getDependencies(element.element(MetadataUtil.DEPENDENCIES));
                    module.setMainArtifact(mainDependency)
                            .setDependencies(dependencies);
                    break;
            }
        }
        return module;
    }

    private List<Artifact> getDependencies(Element dependenciesElement) {
        List<Artifact> dependencies = Lists.newArrayList();
        List<Element> elements = dependenciesElement.elements(MetadataUtil.DEPENDENCY);
        for (Element element : elements) {
            if (element.getName().equals(MetadataUtil.DEPENDENCY)) {
                dependencies.add(getMavenDependency(element));
            }
        }
        return dependencies;
    }

    private Artifact getMavenDependency(Element mainElement) {
        Artifact artifact;
        String groupId    = null;
        String artifactId = null;
        String version    = null;
        String jar        = null;
        List<Element> elements = mainElement.elements();
        for (Element element : elements) {
            switch (element.getName()) {
                case MetadataUtil.GROUP_ID:
                    groupId = element.getText();
                    break;
                case MetadataUtil.ARTIFACT_ID:
                    artifactId = element.getText();
                    break;
                case MetadataUtil.VERSION:
                    version = element.getText();
                    break;
                case MetadataUtil.JAR:
                    jar = element.getText();
                    break;
            }
        }
        artifact = new DefaultArtifact(String.format("%s:%s:%s", groupId, artifactId, version));
        return artifact.setFile(new File(jar));
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
}
