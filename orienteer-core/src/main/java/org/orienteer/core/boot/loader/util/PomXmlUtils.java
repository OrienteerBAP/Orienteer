package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy Gonchar
 * Class for work with pom.xml which is located in module jar file.
 */
class PomXmlUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PomXmlUtils.class);

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

    private Map<String, String> orienteerVersions;

    PomXmlUtils addOrienteerVersions(Path pomXml) {
        if (orienteerVersions == null){
            orienteerVersions = Maps.newHashMap();
        }

        orienteerVersions.putAll(getVersionsFromPomXml(getRootElement(pomXml)));
        return this;
    }

    public Map<String, String> getOrienteerVersions() {
        return orienteerVersions;
    }

    Optional<Artifact> readParentGAVInPomXml(Path pomXml) {
        Element rootElement = getRootElement(pomXml);
        Element parentElement = rootElement.element(PARENT);
        String groupId = parentElement.element(GROUP).getText();
        String artifactId = parentElement.element(ARTIFACT).getText();
        String version = parentElement.element(VERSION).getText();
        if (groupId != null && artifactId != null && version != null)
            return Optional.of((Artifact) new DefaultArtifact(getGAV(groupId, artifactId, version)));
        return Optional.absent();
    }

    Optional<Artifact> readGroupArtifactVersionInPomXml(Path pomXml) {
        Element rootElement = getRootElement(pomXml);
        Element group = rootElement.element(GROUP);
        Element artifact = rootElement.element(ARTIFACT);
        Element version = rootElement.element(VERSION);
        Artifact parent = readParentGAVInPomXml(pomXml).orNull();
        String groupId = group != null ? group.getText() : (parent != null ? parent.getGroupId() : null);
        String artifactId = artifact != null ? artifact.getText() : null;
        String versionId = version != null ? version.getText() : (parent != null ? parent.getVersion() : null);
        if (groupId != null && artifactId != null && versionId != null)
            return  Optional.of((Artifact) new DefaultArtifact(getGAV(groupId, artifactId, versionId)));
        return Optional.absent();
    }

    @SuppressWarnings("unchecked")
    Set<Artifact> readDependencies(Path pathToPomXml) {
        Element rootElement = getRootElement(pathToPomXml);
        Element dependenciesElement = rootElement.element(DEPENDENCIES);
        if (dependenciesElement == null) {
            dependenciesElement = rootElement.element(DEPENDENCIES_MANAGMENT).element(DEPENDENCIES);
        }
        List<Element> dependencyElements = dependenciesElement.elements();

        Set<Artifact> dependencies =  Sets.newHashSet();
        Map<String, String> versions = getVersionsFromPomXml(rootElement);
        if (orienteerVersions != null) {
            versions.putAll(orienteerVersions);
        }
        for (Element element : dependencyElements) {
            Optional<Artifact> depOptional = parseDependency(element, versions);
            if (depOptional.isPresent()) dependencies.add(depOptional.get());
        }

        return dependencies;
    }

    private Map<String, String> getVersionsFromPomXml(Element rootElement) {
        Map<String, String> versions = Maps.newHashMap();
        Element properties = rootElement.element(PROPERTIES);
        if (properties != null) {
            versions.putAll(parseVersionProperty(properties));
        }
        Element parent = rootElement.element(PARENT);
        if (parent != null) {
            versions.put(PROPERTIES_PROJECT_VERSION, getParentVersion(parent));
        }
        Element version = rootElement.element(VERSION);
        if (version != null) {
            versions.put(PROPERTIES_PROJECT_VERSION, version.getText());
        }
        return versions;
    }

    private String getParentVersion(Element parentElement)  {
        String version = parentElement.element(VERSION) != null ? parentElement.element(VERSION).getText() : null;
        return version != null ? version : WITHOUT_VERSION;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseVersionProperty(Element propertyElement) {
        Map<String, String> versions = Maps.newHashMap();
        List<Element> elements = propertyElement.elements();
        for (Element element : elements) {
            String elementName = getLinkToVersion(element.getName());
            versions.put(elementName, element.getText());
        }
        return versions;
    }

    private Optional<Artifact> parseDependency(Element dependencyElement, Map<String, String> versions) {
        Element groupElement = dependencyElement.element(GROUP);
        Element artifactElement = dependencyElement.element(ARTIFACT);
        Element versionElement = dependencyElement.element(VERSION);
        String groupId = groupElement != null ? groupElement.getText() : null;
        String artifactId = artifactElement != null ? artifactElement.getText() : null;
        String version = versionElement != null ? versionElement.getText() : null;
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

    private Element getRootElement(Path path) {
        Document document = readFromFile(path);
        return document.getRootElement();
    }

    private Document readFromFile(Path path) {
        SAXReader reader = new SAXReader();
        try {
            return reader.read(path.toFile());
        } catch (DocumentException ex) {
            LOG.error("Cannot read: " + path.toAbsolutePath(), ex);
        }
        return null;
    }
}
