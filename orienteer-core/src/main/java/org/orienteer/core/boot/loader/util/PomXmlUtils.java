package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.http.util.Args;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 * Class for work with pom.xml which is located in module jar file.
 */
class PomXmlUtils extends AbstractXmlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PomXmlUtils.class);

    private static final String PROJECT                     = "project";
    private static final String PARENT                      = "parent";
    private static final String GROUP                       = "groupId";
    private static final String ARTIFACT                    = "artifactId";
    private static final String VERSION                     = "version";
    private static final String DEPENDENCIES                = "dependencies";
    private static final String PROPERTIES                  = "properties";
    private static final String DEPENDENCY                  = "dependency";
    private static final String DEPENDENCIES_MANAGMENT      = "dependencyManagement";
    private static final String PROPERTIES_VERSION_END      = ".version";
    private static final String PROPERTIES_PROJECT_VERSION  = "${project.version}";
    private static final String WITHOUT_VERSION             = "without-version";


    private final Map<String, String> orienteerVersions     = Maps.newHashMap();

    public PomXmlUtils addOrienteerVersions(Path pomXml) {
        orienteerVersions.putAll(getPropertiesVersionsFromPomXml(pomXml));
        String parentVersion = getParentVersion(pomXml);
        if (parentVersion != null) {
            orienteerVersions.put(PROPERTIES_PROJECT_VERSION, parentVersion);
        }
        return this;
    }

    public Map<String, String> getOrienteerVersions() {
        return Collections.unmodifiableMap(orienteerVersions);
    }


    public Optional<Artifact> readParentGAVInPomXml(Path pomXml) {
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
                return Optional.of((Artifact) new DefaultArtifact(getGAV(groupId, artifactId, version)));
        }

        return Optional.absent();
    }

    public Optional<Artifact> readGroupArtifactVersionInPomXml(Path pomXml) {
        String groupExpression    = String.format("/%s/%s", PROJECT, GROUP);
        String artifactExpression = String.format("/%s/%s", PROJECT, ARTIFACT);
        String versionExpression  = String.format("/%s/%s", PROJECT, VERSION);
        Document doc = readDocumentFromFile(pomXml);
        NodeList group = executeExpression(groupExpression, doc);
        NodeList artifact = executeExpression(artifactExpression, doc);
        NodeList version = executeExpression(versionExpression, doc);

        Artifact parent = readParentGAVInPomXml(pomXml).orNull();
        String groupId = (group != null && group.getLength() > 0) ? group.item(0).getTextContent() : (parent != null ? parent.getGroupId() : null);
        String artifactId = (artifact != null && artifact.getLength() > 0) ? artifact.item(0).getTextContent() : null;
        String versionId = (version != null && version.getLength() > 0) ? version.item(0).getTextContent() : (parent != null ? parent.getVersion() : null);

        if (groupId != null && artifactId != null && versionId != null)
            return Optional.of((Artifact) new DefaultArtifact(getGAV(groupId, artifactId, versionId)));

        return Optional.absent();
    }

    @SuppressWarnings("unchecked")
    public Set<Artifact> readDependencies(Path pomXml) {
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
            versions.putAll(orienteerVersions);
            for (int i = 0; i < dependenciesNode.getLength(); i++) {
                Node node = dependenciesNode.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Optional<Artifact> artifactOptional = parseDependency(element, versions);
                    if (artifactOptional.isPresent()) dependencies.add(artifactOptional.get());
                }
            }
        }

        return dependencies;
    }

    private Map<String, String> getPropertiesVersionsFromPomXml(Path pomXml) {
        return getPropertiesVersionsFromPomXml(readDocumentFromFile(pomXml));
    }

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

    private String getParentVersion(Path pomXml) {
        return getParentVersion(readDocumentFromFile(pomXml));
    }

    private String getParentVersion(Document doc)  {
        String expression = String.format("/%s/%s/%s", PROJECT, PARENT, VERSION);
        NodeList nodeList = executeExpression(expression, doc);
        if (nodeList != null && nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return element.getTextContent();
        }
        return WITHOUT_VERSION;
    }

    private Optional<Artifact> parseDependency(Element element, Map<String, String> versions) {
        Element groupElement = (Element) element.getElementsByTagName(GROUP).item(0);
        Element artifactElement = (Element) element.getElementsByTagName(ARTIFACT).item(0);
        Element versionElement = (Element) element.getElementsByTagName(VERSION).item(0);
        String groupId = groupElement != null ? groupElement.getTextContent() : null;
        String artifactId = artifactElement != null ? artifactElement.getTextContent() : null;
        String version = versionElement != null ? versionElement.getTextContent() : null;
        if (isLinkToVersion(version) && versions != null) {
            String ver = versions.get(version);
            if (ver != null) version = ver;
        }

        if (groupId != null && artifactId != null && version != null)
            return Optional.of((Artifact) new DefaultArtifact(getGAV(groupId, artifactId, version)));

        return Optional.absent();
    }

    private String getGAV(String group, String artifact, String version) {
        return String.format("%s:%s:%s", group, artifact, version);
    }

    private boolean isLinkToVersion(String artifactVersion) {
        return artifactVersion != null &&
                (artifactVersion.contains("$") || artifactVersion.contains("{") || artifactVersion.contains("}"));
    }

    private String getLinkToVersion(String artifactVersion) {
        return !artifactVersion.startsWith("$") ? "${" + artifactVersion + "}" : artifactVersion;
    }
}
