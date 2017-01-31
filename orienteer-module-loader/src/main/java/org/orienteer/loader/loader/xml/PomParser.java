package org.orienteer.loader.loader.xml;

import org.orienteer.loader.loader.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Vitaliy Gonchar
 */
public abstract class PomParser {

    private static final Logger LOG = LoggerFactory.getLogger(PomParser.class);

    private static Map<String, String> versions;

    public static final String WITHOUT_VERSION = "WITHOUT_VERSION";

    public static Set<Dependency> readDependencies(InputStream in){
        Set<Dependency> dependencies = new HashSet<>();
        versions = new HashMap<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader streamReader = factory.createXMLStreamReader(new InputStreamReader(in));

            while(streamReader.hasNext()){
                streamReader.next();
                if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT){
                    String elementName = streamReader.getLocalName();

                    if (elementName.equals("properties")) {
                        parseVersionProperty(streamReader);
                    } else if (elementName.equals("dependency")) {
                        Dependency dependency = parseDependency(streamReader);
                        if (dependency != null) dependencies.add(dependency);
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return dependencies;
    }

    public static Dependency getParentDependency(InputStream in) {
        Dependency dep = null;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader streamReader = factory.createXMLStreamReader(new InputStreamReader(in));

            while(streamReader.hasNext()){
                streamReader.next();
                if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT){
                    String elementName = streamReader.getLocalName();
                    if (elementName.equals("parent")) {
                        dep = parseDependency(streamReader);
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return dep;
    }

    public static Map<String, String> getArtifactVersions(InputStream in) {
        versions = new HashMap<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader streamReader = factory.createXMLStreamReader(new InputStreamReader(in));
            while(streamReader.hasNext()) {
                streamReader.next();
                if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                    String elementName = streamReader.getLocalName();

                    if (elementName.equals("properties")) {
                        parseVersionProperty(streamReader);
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return versions;
    }

    private static void parseVersionProperty(XMLStreamReader streamReader) throws XMLStreamException {
        while (!(streamReader.getEventType() == XMLStreamReader.END_ELEMENT)) {
            streamReader.next();
            if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                String elementName = streamReader.getLocalName();
                if (elementName.endsWith(".version")) {
                    elementName = getLinkToVersion(elementName);
                    versions.put(elementName, streamReader.getElementText());
                }
                while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                    streamReader.next();
                }
                streamReader.next();
            }
        }
    }

    public static Dependency parseDependency(XMLStreamReader streamReader) throws XMLStreamException {
        String groupId = null;
        String artifactId = null;
        String artifactVersion = null;
        String scope = null;

        while(!(streamReader.getEventType() == XMLStreamReader.END_ELEMENT)){
            streamReader.next();

            if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT){
                String elementName = streamReader.getLocalName();

                if("groupId".equals(elementName)){
                    groupId = streamReader.getElementText();
                    while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                        streamReader.next();
                    }
                    streamReader.next();
                } else if("artifactId".equals(elementName)){
                    artifactId = streamReader.getElementText();
                    while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                        streamReader.next();
                    }
                    streamReader.next();
                } else if("version".equals(elementName)){
                    String version = streamReader.getElementText();
                    if (isLinkToVersion(version)) {
                        String ver = versions.get(version.substring("${".length(), version.indexOf("}")));
                        if (ver != null) version = ver;
                    }
                    artifactVersion = version;
                    while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                        streamReader.next();
                    }
                    streamReader.next();
                } else if("scope".equals(elementName)){
                    scope = streamReader.getElementText();
                    while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                        streamReader.next();
                    }
                    streamReader.next();
                } else {
                    while(streamReader.getEventType() != XMLStreamReader.END_ELEMENT){
                        streamReader.next();
                    }
                    streamReader.next();
                }
            }
        }
        if (artifactVersion == null) artifactVersion = WITHOUT_VERSION;
        return new Dependency(groupId, artifactId, artifactVersion, scope);
    }

    public static boolean isLinkToVersion(String artifactVersion) {
        return artifactVersion != null &&
                (artifactVersion.contains("$") || artifactVersion.contains("{") || artifactVersion.contains("}"));
    }

    private static String getLinkToVersion(String artifactVersion) {
        return !artifactVersion.startsWith("$") ? "${" + artifactVersion + "}" : artifactVersion;
    }

    public static List<Map<String, String>> getBlocks(InputStream in, String blockName) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        Map<String, String> block = null;
        List<Map<String, String>> blocks = new ArrayList<>();
        try {
            reader = factory.createXMLStreamReader(new InputStreamReader(in));
            while (reader.hasNext()) {
                reader.next();
                if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
                    String info = reader.getLocalName();
                    if (info.equals(blockName)) block = getBlock(reader, blockName);
                    if (block != null) {
                        blocks.add(block);
                        block = null;
                    }
                }
            }
        } catch (XMLStreamException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        } finally {
            if (reader != null) try {
                reader.close();
            } catch (XMLStreamException e) {
                if (LOG.isDebugEnabled()) e.printStackTrace();
            }
        }
        return blocks;
    }

    private static Map<String, String> getBlock(final XMLStreamReader reader, final String blockName) throws XMLStreamException{
        Map<String, String> block = new HashMap<>();
        String fieldName;
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
                fieldName = reader.getLocalName();
                try {
                    block.put(fieldName, reader.getElementText());
                } catch (XMLStreamException ex) {
                    if (LOG.isDebugEnabled()) ex.printStackTrace();
                }
            } else if (reader.getEventType() == XMLStreamReader.END_ELEMENT && reader.getLocalName().equals(blockName)) {
                break;
            }
        }
        return block;
    }


    public static Map<String, String> getVersions() {
        return versions;
    }

}
