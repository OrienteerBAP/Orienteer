package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 * Class for read {@link OArtifact} from metadata.xml
 */
class OMetadataReader extends AbstractXmlUtil {
    private static final Logger LOG = LoggerFactory.getLogger(OMetadataReader.class);

    private final Path pathToMetadata;

    OMetadataReader(Path pathToMetadata) {
        this.pathToMetadata = pathToMetadata;
    }

    List<OArtifact> readModulesForLoad() {
        List<OArtifact> modules = read();
        List<OArtifact> modulesForLoad = Lists.newArrayList();
        for (OArtifact module : modules) {
            if (module.isLoad()) modulesForLoad.add(module);
        }
        return modulesForLoad;
    }

    List<OArtifact> readAllOoArtifacts() {
        return read();
    }

    @SuppressWarnings("unchecked")
    private List<OArtifact> read() {
        Document document = readDocumentFromFile(pathToMetadata);
        String expression = String.format("/%s/*", MetadataTag.METADATA.get());
        return getOoArtifactsInMetadataXml(executeExpression(expression, document));
    }

    private List<OArtifact> getOoArtifactsInMetadataXml(NodeList nodeList) {
        List<OArtifact> modules = Lists.newArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                OArtifact module = getOoArtifact(element);
                modules.add(module);
            }
        }
        return modules;
    }

    @SuppressWarnings("unchecked")
    private OArtifact getOoArtifact(Element mainElement) {
        OArtifact module = new OArtifact();
        NodeList nodeList = mainElement.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                MetadataTag tag = MetadataTag.getByName(element.getTagName());
                switch (tag) {
                    case LOAD:
                        module.setLoad(Boolean.valueOf(element.getTextContent()));
                        break;
                    case TRUSTED:
                        module.setTrusted(Boolean.valueOf(element.getTextContent()));
                        break;
                    case DEPENDENCY:
                        module.setArtifact(getMavenDependency(element));
                        break;
                }
            }
        }
        return module;
    }

    @SuppressWarnings("unchecked")
    private OArtifactReference getMavenDependency(Element mainElement) {
        OArtifactReference artifact;
        String groupId    = null;
        String artifactId = null;
        String version    = null;
        String jar        = null;
        String repository = "";
        String description = "";
        NodeList nodeList = mainElement.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                MetadataTag tag = MetadataTag.getByName(element.getTagName());
                switch (tag) {
                    case GROUP_ID:
                        groupId = element.getTextContent();
                        break;
                    case ARTIFACT_ID:
                        artifactId = element.getTextContent();
                        break;
                    case VERSION:
                        version = element.getTextContent();
                        break;
                    case REPOSITORY:
                        repository = element.getTextContent();
                        break;
                    case DESCRIPTION:
                        description = element.getTextContent();
                        break;
                    case JAR:
                        jar = element.getTextContent();
                        break;
                }
            }
        }
        artifact = new OArtifactReference(groupId, artifactId, version, repository, description);
        return jar != null ? artifact.setFile(new File(jar)) : artifact;
    }

}
