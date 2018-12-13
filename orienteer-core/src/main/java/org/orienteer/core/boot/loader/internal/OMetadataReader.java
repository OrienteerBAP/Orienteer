package org.orienteer.core.boot.loader.internal;

import com.google.common.collect.Lists;
import org.apache.http.util.Args;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Class for read {@link OArtifact} from metadata.xml
 */
class OMetadataReader extends AbstractXmlHandler {

    private final Path pathToMetadata;

    /**
     * Constructor
     * @param pathToMetadata {@link Path} of metadata.xml
     * @throws IllegalArgumentException if pathToMetadata is null
     */
    OMetadataReader(Path pathToMetadata) {
        Args.notNull(pathToMetadata, "pathToMetadata");
        this.pathToMetadata = pathToMetadata;
    }

    /**
     * Read artifacts for load from metadata.xml
     * @return list {@link OArtifact} of artifacts for load in metadata.xml or empty list if metadata.xml is empty
     */
    List<OArtifact> readArtifactsForLoad() {
        List<OArtifact> modules = read();
        List<OArtifact> modulesForLoad = Lists.newArrayList();
        for (OArtifact module : modules) {
            if (module.isLoad()) modulesForLoad.add(module);
        }
        return modulesForLoad;
    }

    /**
     * Read all artifacts from metadata.xml
     * @return list {@link OArtifact} of artifacts in metadata.xml or empty list if metadata.xml is empty
     */
    List<OArtifact> readAllOoArtifacts() {
        return read();
    }

    /**
     * Read all artifacts from metadata.xml
     * @return list {@link OArtifact} of artifacts in metadata.xml or empty list if metadata.xml is empty
     */
    @SuppressWarnings("unchecked")
    private List<OArtifact> read() {
        Document document = readDocumentFromFile(pathToMetadata);
        String expression = String.format("/%s/*", MetadataTag.METADATA.get());
        return getOoArtifactsInMetadataXml(executeExpression(expression, document));
    }

    /**
     * Search artifacts in nodeList
     * @param nodeList list with nodes in metadata.xml
     * @return list {@link OArtifact} with artifacts or empty list if nodeList don't contains any artifact
     */
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

    /**
     * Parse mainElement and get {@link OArtifact} from it.
     * @param mainElement - {@link Element}t for parse
     * @return {@link OArtifact} from mainElement
     */
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
                    case DOWNLOADED:
                        module.setDownloaded(Boolean.valueOf(element.getTextContent()));
                        break;
                    case DEPENDENCY:
                        module.setArtifactReference(getMavenDependency(element));
                        break;
                }
            }
        }
        return module;
    }

    /**
     * Parse mainElement and get {@link OArtifactReference} from it.
     * @param mainElement - {@link Element} for parse.
     * @return {@link OArtifactReference} from mainElement.
     */
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
