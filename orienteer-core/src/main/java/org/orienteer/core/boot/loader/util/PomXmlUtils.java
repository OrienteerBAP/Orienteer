package org.orienteer.core.boot.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static final String PROPERTIES_VERSION_END      = ".version";
    private static final String PROPERTIES_PROJECT_VERSION  = "${project.version}";
    private static final String WITHOUT_VERSION             = "without-version";

    private Map<String, String> orienteerVersions;

    public PomXmlUtils setOrienteerVersions(Map<String, String> orienteerVersions) {
        this.orienteerVersions = orienteerVersions;
        return this;
    }

    public Map<String, String> getOrienteerVersions() {
        return orienteerVersions;
    }

    public Optional<Artifact> readGroupArtifactVersionInPomXml(Path pomXml) {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        Optional<Artifact> dependency = Optional.absent();
        try {
            XMLStreamReader streamReader = null;
            String group = null;
            String artifact = null;
            String version = null;
            String parentVersion = null;
            try {
                streamReader = factory.createXMLStreamReader(
                        new InputStreamReader(Files.newInputStream(pomXml)));
                boolean isRun = true;
                while (streamReader.hasNext() && isRun) {
                    streamReader.next();
                    if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                        String elementName = streamReader.getLocalName();
                        switch (elementName) {
                            case GROUP:
                                if (group == null) group = streamReader.getElementText();
                                break;
                            case ARTIFACT:
                                artifact = streamReader.getElementText();
                                break;
                            case VERSION:
                                version = streamReader.getElementText();
                                break;
                            case PARENT:
                                Optional<Artifact> parentDependency =
                                        parseDependency(streamReader, null);
                                if (parentDependency.isPresent()) {
                                    group = parentDependency.get().getGroupId();
                                    parentVersion = parentDependency.get().getVersion();
                                }
                                break;
                            default:
                                if (elementName.equals(DEPENDENCIES) || elementName.equals(PROPERTIES)) {
                                    isRun = false;
                                }
                        }
                        while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                            streamReader.next();
                        }
                        streamReader.next();
                    }
                }
                if (version == null || isLinkToVersion(version)) version = parentVersion;
                if (version != null && group != null && artifact != null)
                    dependency = Optional.of((Artifact) new DefaultArtifact(getGAV(group, artifact, version)));
            } finally {
                if (streamReader != null) streamReader.close();
            }
        } catch (XMLStreamException e) {
            LOG.error("Cannot read pom.xml");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        } catch (IOException e) {
            LOG.error("Cannot open pom.xml");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return dependency;
    }

    public Set<Artifact> readDependencies(Path pomXml) {
        Set<Artifact> dependencies =  Sets.newHashSet();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            Map<String, String> versions = getVersionsInProperties(Files.newInputStream(pomXml));
            if (orienteerVersions != null) versions.putAll(orienteerVersions);
            XMLStreamReader streamReader = null;
            try {
                streamReader = factory.createXMLStreamReader(
                        new InputStreamReader(Files.newInputStream(pomXml)));

                while (streamReader.hasNext()) {
                    streamReader.next();
                    if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                        String elementName = streamReader.getLocalName();
                        if (elementName.equals(DEPENDENCY)) {
                            Optional<Artifact> depOptional = parseDependency(streamReader, versions);
                            if (depOptional.isPresent()) dependencies.add(depOptional.get());
                        }
                    }
                }
            } finally {
                if (streamReader != null) streamReader.close();
            }
        } catch (XMLStreamException e) {
            LOG.error("Cannot read pom.xml");
            if (LOG.isDebugEnabled())
                e.printStackTrace();
        } catch (IOException e) {
            LOG.error("Cannot open pom.xml");
            if (LOG.isDebugEnabled())
                e.printStackTrace();
        }
        return dependencies;
    }

    public Map<String, String> getVersionsInProperties(Path pathToPomXml) {
        InputStream in = null;
        try {
            in = Files.newInputStream(pathToPomXml);
        } catch (IOException e) {
            LOG.error("Cannot open pom.xml for read: " + pathToPomXml);
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return in != null ? getVersionsInProperties(in) : Maps.<String, String>newHashMap();
    }

    public Map<String, String> getVersionsInProperties(InputStream in) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        Map<String, String> versions = Maps.newHashMap();
        boolean isReadProjectVersion = false;
        try {
            XMLStreamReader streamReader = null;
            try {
                streamReader = factory.createXMLStreamReader(new InputStreamReader(in));
                boolean isRun = true;
                while (streamReader.hasNext() && isRun) {
                    streamReader.next();
                    if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                        String elementName = streamReader.getLocalName();
                        switch (elementName) {
                            case PROPERTIES:
                                versions.putAll(parseVersionProperty(streamReader));
                                isRun = false;
                                break;
                            case PARENT:
                                versions.put(PROPERTIES_PROJECT_VERSION, getParrentVersion(streamReader));
                                isReadProjectVersion = true;
                                break;
                            case VERSION:
                                if (!isReadProjectVersion) {
                                    versions.put(PROPERTIES_PROJECT_VERSION, streamReader.getElementText());
                                    isReadProjectVersion = true;
                                }
                                break;
                        }
                        while (streamReader.getEventType() != XMLStreamReader.END_ELEMENT) {
                            streamReader.next();
                        }
                        streamReader.next();
                    }
                }
            } finally {
                if (streamReader != null) streamReader.close();
            }
        } catch (XMLStreamException e) {
            LOG.error("Cannot read pom.xml");
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return versions;
    }

    private String getParrentVersion(XMLStreamReader streamReader) throws XMLStreamException {
        String version = null;
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals(VERSION)) {
                    version = streamReader.getElementText();
                    break;
                }
                streamReader.next();
            }
        }
        return version != null ? version : WITHOUT_VERSION;
    }

    private Map<String, String> parseVersionProperty(XMLStreamReader streamReader)
            throws XMLStreamException {
        Map<String, String> versions = Maps.newHashMap();
        while (!(streamReader.getEventType() == XMLStreamReader.END_ELEMENT)) {
            streamReader.next();
            if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                String elementName = streamReader.getLocalName();
                if (elementName.endsWith(PROPERTIES_VERSION_END)) {
                    elementName = getLinkToVersion(elementName);
                    String version = streamReader.getElementText();
                    if (version != null) versions.put(elementName, version);
                }
                while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                    streamReader.next();
                }
                streamReader.next();
            }
        }
        return versions;
    }

    private Optional<Artifact> parseDependency(XMLStreamReader streamReader,
                                                       Map<String, String> versions)
            throws XMLStreamException {
        String groupId = null;
        String artifactId = null;
        String artifactVersion = null;
        Optional<Artifact> dependencyOptional = Optional.absent();
        boolean run = true;
        while(!(streamReader.getEventType() == XMLStreamReader.END_ELEMENT) && run) {
            streamReader.next();

            if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                String elementName = streamReader.getLocalName();
                switch (elementName) {
                    case GROUP:
                        groupId = streamReader.getElementText();
                        break;
                    case ARTIFACT:
                        artifactId = streamReader.getElementText();
                        break;
                    case VERSION:
                        String version = streamReader.getElementText();
                        if (isLinkToVersion(version) && versions != null) {
                            String ver = versions.get(version);
                            version = ver != null ? ver : version;
                        }
                        artifactVersion = version;
                        run = false;
                        break;
                }

                while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                    streamReader.next();
                }
                streamReader.next();
            }
        }
        if (artifactVersion != null) {
            dependencyOptional = Optional.of((Artifact) new DefaultArtifact(getGAV(groupId, artifactId, artifactVersion)));
        }

        return dependencyOptional;
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
