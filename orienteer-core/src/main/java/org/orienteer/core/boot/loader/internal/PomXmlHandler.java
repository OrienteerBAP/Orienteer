package org.orienteer.core.boot.loader.internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.http.util.Args;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Class for work with pom.xml which is located in artifact jar file.
 */
class PomXmlHandler extends AbstractXmlHandler {

    private static final String PROJECT                     = "project";
    private static final String PARENT                      = "parent";
    private static final String GROUP                       = "groupId";
    private static final String ARTIFACT                    = "artifactId";
    private static final String VERSION                     = "version";
    private static final String DEPENDENCIES                = "dependencies";
    private static final String PROPERTIES                  = "properties";
    private static final String DEPENDENCIES_MANAGMENT      = "dependencyManagement";
    private static final String WITHOUT_VERSION             = "without-version";


    /**
     * Read maven group, artifact, version in xml node <parent></parent>
     * @param pomXml pom.xml for read
     * @return {@link Artifact} with parent's group, artifact, version
     */
    Artifact readParentGAVInPomXml(Path pomXml) {
        Args.notNull(pomXml, "pomXml");
        Document doc = readDocumentFromFile(pomXml);
        String expression = String.format("/%s/%s", PROJECT, PARENT);
        NodeList nodeList = executeExpression(expression, doc);
        if (nodeList != null && nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            String groupId = element.getElementsByTagName(GROUP).item(0).getTextContent();
            String artifactId = element.getElementsByTagName(ARTIFACT).item(0).getTextContent();
            String version = element.getElementsByTagName(VERSION).item(0).getTextContent();
            if (groupId != null && artifactId != null && version != null)
                return (Artifact) new DefaultArtifact(getGAV(groupId, artifactId, version));
        }

        return null;
    }

    /**
     * Read group, artifact, version from nodes:
     * <groupId></groupId>, <artifactId></artifactId>, <version></version>
     * If presents parent node group and artifact takes from parent node
     * @param pomXml pom.xml for read
     * @return {@link Artifact} with group, artifact, version
     */
    Artifact readGroupArtifactVersionInPomXml(Path pomXml) {
        String groupExpression    = String.format("/%s/%s", PROJECT, GROUP);
        String artifactExpression = String.format("/%s/%s", PROJECT, ARTIFACT);
        String versionExpression  = String.format("/%s/%s", PROJECT, VERSION);
        Document doc = readDocumentFromFile(pomXml);
        NodeList group = executeExpression(groupExpression, doc);
        NodeList artifact = executeExpression(artifactExpression, doc);
        NodeList version = executeExpression(versionExpression, doc);

        Artifact parent = readParentGAVInPomXml(pomXml);
        String groupId = (group != null && group.getLength() > 0) ? group.item(0).getTextContent() : (parent != null ? parent.getGroupId() : null);
        String artifactId = (artifact != null && artifact.getLength() > 0) ? artifact.item(0).getTextContent() : null;
        String versionId = (version != null && version.getLength() > 0) ? version.item(0).getTextContent() : (parent != null ? parent.getVersion() : null);

        
        return (groupId != null && artifactId != null && versionId != null) 
            ? (Artifact) new DefaultArtifact(getGAV(groupId, artifactId, versionId))
            : null;
    }

    /**
     * Read dependencies from xml node <dependencies></dependencies>
     * @param pomXml pom.xml for read
     * @return collection with dependencies in pom.xml
     */
    @SuppressWarnings("unchecked")
    Set<Artifact> readDependencies(Path pomXml) {
        String dependenciesExp          = String.format("/%s/%s/*", PROJECT, DEPENDENCIES);
        String dependenciesManagmentExp = String.format("/%s/%s/%s/*", PROJECT, DEPENDENCIES_MANAGMENT, DEPENDENCIES);
        Document doc = readDocumentFromFile(pomXml);
        NodeList dependenciesNode = executeExpression(dependenciesExp, doc);
        if (dependenciesNode == null || dependenciesNode.getLength() == 0) {
            dependenciesNode = executeExpression(dependenciesManagmentExp, doc);
        }
        Set<Artifact> dependencies =  Sets.newHashSet();
        if (dependenciesNode != null && dependenciesNode.getLength() != 0) {
            Map<String, String> versions = getPropertiesVersionsFromPomXml(pomXml);
            for (int i = 0; i < dependenciesNode.getLength(); i++) {
                Node node = dependenciesNode.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Artifact artifact = parseDependency(element, versions);
                    if (artifact!=null) dependencies.add(artifact);
                }
            }
        }

        return dependencies;
    }

    /**
     * Search versions which are in xml node <properties></properties>
     * @param pomXml pom.xml for read
     * @return map with versions
     * example: ${wicket.version} : 7.6.0
     */
    private Map<String, String> getPropertiesVersionsFromPomXml(Path pomXml) {
        return getPropertiesVersionsFromPomXml(readDocumentFromFile(pomXml));
    }


    /**
     * Search versions which are in xml node <properties></properties>
     * @param doc {@link Document} for read
     * @return map with versions
     * example: ${wicket.version} : 7.6.0
     */
    private Map<String, String> getPropertiesVersionsFromPomXml(Document doc) {
        String expression = String.format("/%s/%s/*", PROJECT, PROPERTIES);
        Map<String, String> versions = Maps.newHashMap();
        NodeList properties = executeExpression(expression, doc);
        for (int i = 0; i < properties.getLength(); i++) {
            Node node = properties.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                versions.put(getLinkToVersion(element.getTagName()), element.getTextContent());
            }
        }
        return versions;
    }

    /**
     * Search parent version in pom.xml
     * @param pomXml - pom.xml for read
     * @return parent version
     */
    private String getParentVersion(Path pomXml) {
        return getParentVersion(readDocumentFromFile(pomXml));
    }


    /**
     * Search parent version in pom.xml
     * @param doc {@link Document} for read
     * @return parent version
     */
    private String getParentVersion(Document doc)  {
        String expression = String.format("/%s/%s/%s", PROJECT, PARENT, VERSION);
        NodeList nodeList = executeExpression(expression, doc);
        if (nodeList != null && nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return element.getTextContent();
        }
        return WITHOUT_VERSION;
    }

    /**
     * Parse element for getting {@link Artifact} from it
     * @param element - {@link Element} for parse
     * @param versions - versions which are in <properties></properties> of pom.xml
     * @return {@link Artifact} - if parse is success
     *         Optional.absent() - if parse is failed
     */
    private Artifact parseDependency(Element element, Map<String, String> versions) {
        Element groupElement = (Element) element.getElementsByTagName(GROUP).item(0);
        Element artifactElement = (Element) element.getElementsByTagName(ARTIFACT).item(0);
        Element versionElement = (Element) element.getElementsByTagName(VERSION).item(0);
        String groupId = groupElement != null ? groupElement.getTextContent() : null;
        String artifactId = artifactElement != null ? artifactElement.getTextContent() : null;
        String version = versionElement != null ? versionElement.getTextContent() : null;
        if (isLinkToVersion(version) && versions != null) {
            version = versions.get(version);
        }

        return (groupId != null && artifactId != null && version != null)
            ? (Artifact) new DefaultArtifact(getGAV(groupId, artifactId, version))
            : null;
    }

    /**
     * Create group, artifact, version string for {@link Artifact}
     * syntax - groupId:artifactId:version
     * example - org.orienteer:devutils:1.2
     * @param group    - artifact groupId
     * @param artifact - artifact artifactId
     * @param version  - artifact version
     * @return GAV string for creating {@link Artifact}
     */
    private String getGAV(String group, String artifact, String version) {
        return String.format("%s:%s:%s", group, artifact, version);
    }

    /**
     * Checks if artifact version is link to version in xml node <properties></properties>
     * @param version - version for check
     * @return true - if version is link
     *         false - if version is not link
     */
    private boolean isLinkToVersion(String version) {
        return version != null &&
                (version.contains("$") || version.contains("{") || version.contains("}"));
    }

    /**
     * Create link for version.
     * example: wicket.version -> ${wicket.version}
     * @param version - version for creating link
     * @return link for version
     */
    private String getLinkToVersion(String version) {
        return !version.startsWith("$") ? "${" + version + "}" : version;
    }
}
