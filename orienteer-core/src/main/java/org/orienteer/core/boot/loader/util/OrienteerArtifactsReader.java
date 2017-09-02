package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import org.apache.http.util.Args;
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
 */
class OrienteerArtifactsReader extends AbstractXmlUtil {

    private final Path pathToFile;

    /**
     * Constructor
     * @param pathToFile {@link Path} of modules.xml
     * @throws IllegalArgumentException if pathToFile is null
     */
    OrienteerArtifactsReader(Path pathToFile) {
        Args.notNull(pathToFile, "pathToFile");
        this.pathToFile = pathToFile;
    }

    /**
     * Read artifacts from modules.xml
     * @return list of {@link OArtifact} with artifacts in modules.xml or empty list if modules.xml is empty
     * @throws IllegalStateException if document can't be created
     */
    @SuppressWarnings("unchecked")
    List<OArtifact> readArtifacts() {
        List<OArtifact> artifacts = Lists.newArrayList();
        Document document = readDocumentFromFile(pathToFile);
        if (document == null) documentCannotReadException(pathToFile);
        String expression = String.format("/%s/*", MetadataTag.METADATA.get());

        NodeList nodeList = executeExpression(expression, document);
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    artifacts.add(getArtifact((Element) node));
                }
            }
        }
        return artifacts;
    }

    /**
     * Parse {@link Element} element and get {@link OArtifact} from it.
     * @param element {@link Element} for parse
     * @return {@link OArtifact} from element
     */
    private OArtifact getArtifact(Element element) {
        Element groupElement = (Element) element.getElementsByTagName(MetadataTag.GROUP_ID.get()).item(0);
        Element artifactElement = (Element) element.getElementsByTagName(MetadataTag.ARTIFACT_ID.get()).item(0);
        Element descriptionElement = (Element) element.getElementsByTagName(MetadataTag.DESCRIPTION.get()).item(0);
        String groupId = groupElement != null ? groupElement.getTextContent() : null;
        String artifactId = artifactElement != null ? artifactElement.getTextContent() : null;
        String description = descriptionElement != null ? descriptionElement.getTextContent() : null;
        OArtifact module = new OArtifact();
        return module.setArtifactReference(new OArtifactReference(groupId, artifactId, "").setDescription(description));
    }

}
