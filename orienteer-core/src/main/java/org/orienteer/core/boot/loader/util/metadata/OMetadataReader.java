package org.orienteer.core.boot.loader.util.metadata;

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

import static org.orienteer.core.boot.loader.util.metadata.MetadataUtil.*;

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
        List<OModuleMetadata> modules = getModulesInMetadataXml(rootElement.elements(MODULE));
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
                case ID:
                    module.setId(Integer.valueOf(element.getText()));
                    break;
                case INITIALIZER:
                    module.setInitializerName(element.getText());
                    break;
                case LOAD:
                    module.setLoad(Boolean.valueOf(element.getText()));
                    break;
                case MAVEN:
                    Artifact mainDependency = getMavenDependency(element.element(MAIN_DEPENDENCY));
                    List<Artifact> dependencies = getDependencies(element.elements(DEPENDENCIES));
                    module.setMainArtifact(mainDependency)
                            .setDependencies(dependencies);
                    break;
            }
        }
        return module;
    }

    private List<Artifact> getDependencies(List<Element> elements) {
        List<Artifact> dependencies = Lists.newArrayList();
        for (Element element : elements) {
            dependencies.add(getMavenDependency(element.element(DEPENDENCY)));
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
                case GROUP_ID:
                    groupId = element.getText();
                    break;
                case ARTIFACT_ID:
                    artifactId = element.getText();
                    break;
                case VERSION:
                    version = element.getText();
                    break;
                case JAR:
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
