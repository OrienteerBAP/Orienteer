package org.orienteer.core.loader.util;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.orienteer.core.loader.Dependency;
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
 */
public abstract class PomXmlParser {

    private static final Logger LOG = LoggerFactory.getLogger(PomXmlParser.class);


    public static Set<Dependency> readDependencies(Path pomXml, Map<String, String>...baseVersions){
        Set<Dependency> dependencies = Sets.newConcurrentHashSet();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            Map<String, String> versions = getArtifactVersions(Files.newInputStream(pomXml));
            for (Map<String, String> map : baseVersions) {
                versions.putAll(map);
            }
            XMLStreamReader streamReader = factory.createXMLStreamReader(new InputStreamReader(Files.newInputStream(pomXml)));

            while(streamReader.hasNext()){
                streamReader.next();
                if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                    String elementName = streamReader.getLocalName();
                    if (elementName.equals("dependency")) {
                        Optional<Dependency> depOptional = parseDependency(streamReader, versions);
                        if (depOptional.isPresent()) dependencies.add(depOptional.get());
                    }
                }
            }
        } catch (XMLStreamException e) {
            LOG.error("Cannot read pom.xml");
//            if (LOG.isDebugEnabled())
                e.printStackTrace();
        } catch (IOException e) {
            LOG.error("Cannot open pom.xml");
//            if (LOG.isDebugEnabled())
                e.printStackTrace();
        }
        return dependencies;
    }


    public static Map<String, String> getArtifactVersions(InputStream in) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        Map<String, String> versions = Maps.newHashMap();
        boolean isReadProjectVersion = false;
        try {
            XMLStreamReader streamReader = factory.createXMLStreamReader(new InputStreamReader(in));
            while(streamReader.hasNext()) {
                streamReader.next();
                if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {

                    String elementName = streamReader.getLocalName();
                    if (elementName.equals("properties")) {
                        versions.putAll(parseVersionProperty(streamReader));
                        break;
                    } else if (elementName.equals("parent")) {
                        versions.put("${project.version}", getParrentVersion(streamReader));
                        isReadProjectVersion = true;
                    }  else if (!isReadProjectVersion && elementName.equals("version")) {
                        versions.put("${project.version}", streamReader.getElementText());
                        isReadProjectVersion = true;
                    }
                    while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                        streamReader.next();
                    }
                    streamReader.next();
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return versions;
    }

    private static String getParrentVersion(XMLStreamReader streamReader) throws XMLStreamException {
        String version = null;
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                String elementName = streamReader.getLocalName();
                if (elementName.equals("version")) {
                    version = streamReader.getElementText();
                    break;
                }
                streamReader.next();
            }
        }
        return version != null ? version : "no-version";
    }

    private static Map<String, String> parseVersionProperty(XMLStreamReader streamReader)
            throws XMLStreamException {
        Map<String, String> versions = Maps.newHashMap();
        while (!(streamReader.getEventType() == XMLStreamReader.END_ELEMENT)) {
            streamReader.next();
            if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                String elementName = streamReader.getLocalName();
                if (elementName.endsWith(".version")) {
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

    public static Optional<Dependency> parseDependency(XMLStreamReader streamReader,
                                                       Map<String, String> versions)
            throws XMLStreamException {
        String groupId = null;
        String artifactId = null;
        String artifactVersion = null;
//        String scope = null;
        Optional<Dependency> dependencyOptional = Optional.absent();
        while(!(streamReader.getEventType() == XMLStreamReader.END_ELEMENT)) {
            streamReader.next();

            if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                String elementName = streamReader.getLocalName();

                if("groupId".equals(elementName)){
                    groupId = streamReader.getElementText();
                } else if("artifactId".equals(elementName)){
                    artifactId = streamReader.getElementText();
                } else if("version".equals(elementName)){
                    String version = streamReader.getElementText();
                    if (isLinkToVersion(version)) {
                        String ver = versions.get(version);
                        version = ver != null ? ver : version;
                    }
                    artifactVersion = version;
                }
//                else if("scope".equals(elementName)){
//                    scope = streamReader.getElementText();
//                }
                while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                    streamReader.next();
                }
                streamReader.next();
            }
        }
        if (artifactVersion != null) {
            dependencyOptional = Optional.of(new Dependency(groupId, artifactId, artifactVersion));
        }

        return dependencyOptional;
    }

    public static boolean isLinkToVersion(String artifactVersion) {
        return artifactVersion != null &&
                (artifactVersion.contains("$") || artifactVersion.contains("{") || artifactVersion.contains("}"));
    }

    private static String getLinkToVersion(String artifactVersion) {
        return !artifactVersion.startsWith("$") ? "${" + artifactVersion + "}" : artifactVersion;
    }
//
//    public static List<Map<String, String>> getBlocks(InputStream in, String blockName) {
//        XMLInputFactory factory = XMLInputFactory.newInstance();
//        XMLStreamReader reader = null;
//        Map<String, String> block = null;
//        List<Map<String, String>> blocks = new ArrayList<>();
//        try {
//            reader = factory.createXMLStreamReader(new InputStreamReader(in));
//            while (reader.hasNext()) {
//                reader.next();
//                if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
//                    String info = reader.getLocalName();
//                    if (info.equals(blockName)) block = getBlock(reader, blockName);
//                    if (block != null) {
//                        blocks.add(block);
//                        block = null;
//                    }
//                }
//            }
//        } catch (XMLStreamException e) {
//            if (LOG.isDebugEnabled()) e.printStackTrace();
//        } finally {
//            if (reader != null) try {
//                reader.close();
//            } catch (XMLStreamException e) {
//                if (LOG.isDebugEnabled()) e.printStackTrace();
//            }
//        }
//        return blocks;
//    }
//
//    private static Map<String, String> getBlock(final XMLStreamReader reader, final String blockName) throws XMLStreamException{
//        Map<String, String> block = new HashMap<>();
//        String fieldName;
//        while (reader.hasNext()) {
//            reader.next();
//            if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
//                fieldName = reader.getLocalName();
//                try {
//                    block.put(fieldName, reader.getElementText());
//                } catch (XMLStreamException ex) {
//                    if (LOG.isDebugEnabled()) ex.printStackTrace();
//                }
//            } else if (reader.getEventType() == XMLStreamReader.END_ELEMENT && reader.getLocalName().equals(blockName)) {
//                break;
//            }
//        }
//        return block;
//    }


}
