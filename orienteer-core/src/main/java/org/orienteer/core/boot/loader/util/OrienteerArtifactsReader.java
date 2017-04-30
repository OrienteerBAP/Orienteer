package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.file.Path;
import java.util.List;

/**
 * Read Orienteer artifacts from modules.xml
 * @author Vitaliy Gonchar
 */
class OrienteerArtifactsReader extends AbstractXmlUtil {

    private final Path pathToFile;

    OrienteerArtifactsReader(Path pathToFile) {
        this.pathToFile = pathToFile;
    }

    @SuppressWarnings("unchecked")
    List<OArtifact> readModules() {
        List<OArtifact> artifacts = Lists.newArrayList();
        Document document = readDocumentFromFile(pathToFile);
        if (document == null) documentCannotReadException(pathToFile);
        String expression = String.format("/%s/*", MetadataTag.METADATA.get());

        NodeList nodeList = executeExpression(expression, document);
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    artifacts.add(getModule((Element) node));
                }
            }
        }
        return artifacts;
    }

    private OArtifact getModule(Element dependencyElement) {
        Element groupElement = (Element) dependencyElement.getElementsByTagName(MetadataTag.GROUP_ID.get()).item(0);
        Element artifactElement = (Element) dependencyElement.getElementsByTagName(MetadataTag.ARTIFACT_ID.get()).item(0);
        Element versionElement = (Element) dependencyElement.getElementsByTagName(MetadataTag.VERSION.get()).item(0);
        Element descriptionElement = (Element) dependencyElement.getElementsByTagName(MetadataTag.DESCRIPTION.get()).item(0);
        String groupId = groupElement != null ? groupElement.getTextContent() : null;
        String artifactId = artifactElement != null ? artifactElement.getTextContent() : null;
        String version = versionElement != null ? versionElement.getTextContent() : null;
        String description = descriptionElement != null ? descriptionElement.getTextContent() : null;
        OArtifact module = new OArtifact();
        return module.setArtifact(new OArtifactReference(groupId, artifactId, version, description));
    }

}
