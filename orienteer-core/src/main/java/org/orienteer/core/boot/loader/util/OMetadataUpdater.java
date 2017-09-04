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
 * Utility class for update metadata.xml
 */
class OMetadataUpdater extends AbstractXmlUtil {

    private final Path pathToMetadata;

    private static final String ALL_MODULES_EXP = String.format("/%s/*", MetadataTag.METADATA.get());

    /**
     * Constructor
     * @param pathToMetadata {@link Path} of metadata.xml
     * @throws IllegalArgumentException if pathToMetadata is null
     */
    OMetadataUpdater(Path pathToMetadata) {
        Args.notNull(pathToMetadata, "pathToMetadata");
        this.pathToMetadata = pathToMetadata;
    }

    /**
     * Create new metadata.xml with oArtifacts
     * @param oArtifacts list of {@link OArtifact} for write in metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    void create(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        Document document = createNewDocument();
        if (document == null) documentCannotCreateException(pathToMetadata);

        Element root = document.createElement(MetadataTag.METADATA.get());
        document.appendChild(root);
        addArtifacts(oArtifacts, document);
        saveDocument(document, pathToMetadata);
    }

    /**
     * Update metadata.xml
     * @param oArtifact - {@link OArtifact} for update in metadata.xml
     * @throws IllegalArgumentException if oArtifact is null
     */
    void update(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        update(oArtifact, false);
    }

    /**
     * Update metadata.xml
     * @param oArtifacts - list of {@link OArtifact} for update in metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    void update(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        update(oArtifacts, false);
    }

    /**
     * Update jar for oArtifact in metadata.xml
     * @param oArtifact {@link OArtifact} for update
     * @param updateJar true - jar will be update
     *                  false - jar will not be update
     * @throws IllegalArgumentException if oArtifact is null
     */
    void update(OArtifact oArtifact, boolean updateJar) {
        Args.notNull(oArtifact, "oArtifact");
        update(Lists.newArrayList(oArtifact), updateJar);
    }

    /**
     * Update metadata.xml.
     * oArtifacts will be write to metadata.xml or will be update its flag load or trusted.
     * @param oArtifacts list of {@link OArtifact} for update
     * @param updateJar true - jar will be update
     *                  false - jar will not be update
     * @throws IllegalArgumentException if oArtifacts is null
     */
    @SuppressWarnings("unchecked")
    void update(List<OArtifact> oArtifacts, boolean updateJar) {
        Args.notNull(oArtifacts, "oArtifacts");
        Document document = readDocumentFromFile(pathToMetadata);
        if (document == null) documentCannotReadException(pathToMetadata);

        NodeList nodeList = executeExpression(ALL_MODULES_EXP, document);
        List<OArtifact> updatedModules = Lists.newArrayList();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Element.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Element dependencyElement = (Element) element.getElementsByTagName(MetadataTag.DEPENDENCY.get()).item(0);
                    OArtifact oArtifact = containsInModulesConfigsList(dependencyElement, oArtifacts);
                    if (oArtifact != null) {
                        if (updateJar) {
                            changeoArtifactsLoadAndJar(element, oArtifact);
                        } else changeArtifactElement(element, oArtifact);
                        updatedModules.add(oArtifact);
                    }
                }
            }
        }
        if (updatedModules.size() != oArtifacts.size()) {
            addArtifacts(difference(updatedModules, oArtifacts), document);
        }
        saveDocument(document, pathToMetadata);
    }

    /**
     * Replace artifactForReplace in metadata.xml by newArtifact
     * @param artifactForReplace - artifact for replace
     * @param newArtifact - new artifact
     * @throws IllegalArgumentException if artifactForReplace or newArtifact is null
     */
    @SuppressWarnings("unchecked")
    void update(OArtifact artifactForReplace, OArtifact newArtifact) {
        Args.notNull(artifactForReplace, "artifactForUpdate");
        Args.notNull(newArtifact, "newArtifact");
        Document document = readDocumentFromFile(pathToMetadata);
        if (document == null) documentCannotReadException(pathToMetadata);
        NodeList nodeList = executeExpression(ALL_MODULES_EXP, document);
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Element dependencyElement = (Element) element.getElementsByTagName(MetadataTag.DEPENDENCY.get()).item(0);
                    OArtifact module = containsInModulesConfigsList(dependencyElement, Lists.newArrayList(artifactForReplace));
                    if (module != null) {
                        changeArtifactElement(element, newArtifact);
                        break;
                    }
                }
            }
            saveDocument(document, pathToMetadata);
        }
    }

    /**
     * Delete oArtifact from metadata.xml
     * @param oArtifact {@link OArtifact} for delete from metadata.xml
     * @throws IllegalArgumentException if oArtifact is null
     */
    @SuppressWarnings("unchecked")
    void delete(OArtifact oArtifact) {
        Args.notNull(oArtifact, "oArtifact");
        Document document = readDocumentFromFile(pathToMetadata);
        if (document == null) documentCannotReadException(pathToMetadata);
        NodeList nodeList = executeExpression(ALL_MODULES_EXP, document);

        if (nodeList != null) {
            Element root = document.getDocumentElement();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Element dependencyElement = (Element) element.getElementsByTagName(MetadataTag.DEPENDENCY.get()).item(0);
                    if (isNecessaryElement(dependencyElement, oArtifact)) {
                        root.removeChild(element);
                        break;
                    }
                }
            }
            saveDocument(document, pathToMetadata);
        }
    }

    /**
     * Delete list of {@link OArtifact} from metadata.xml
     * @param oArtifacts list of {@link OArtifact} for delete from metadata.xml
     * @throws IllegalArgumentException if oArtifacts is null
     */
    @SuppressWarnings("unchecked")
    void delete(List<OArtifact> oArtifacts) {
        Args.notNull(oArtifacts, "oArtifacts");
        Document document = readDocumentFromFile(pathToMetadata);
        if (document == null) documentCannotReadException(pathToMetadata);
        NodeList nodeList = executeExpression(ALL_MODULES_EXP, document);
        if (nodeList != null) {
            Element root = document.getDocumentElement();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Element dependencyElement = (Element) element.getElementsByTagName(MetadataTag.DEPENDENCY.get()).item(0);
                    OArtifact metadata = containsInModulesConfigsList(dependencyElement, oArtifacts);
                    if (metadata != null) {
                        root.removeChild(element);
                    }
                }
            }
            saveDocument(document, pathToMetadata);
        }
    }

    /**
     * Add artifacts to {@link Document} document
     * @param oArtifacts list of {@link OArtifact} for add to document
     * @param document {@link Document} of metadata.xml
     */
    private void addArtifacts(List<OArtifact> oArtifacts, Document document) {
        for (OArtifact artifact : oArtifacts) {
            addArtifact(artifact, document);
        }
    }

    /**
     * Add artifact to {@link Document} document
     * @param oArtifact {@link OArtifact} for add to document
     * @param document {@link Document} of metadata.xml
     */
    private void addArtifact(OArtifact oArtifact, Document document) {
        Element root = document.getDocumentElement();
        Element module = document.createElement(MetadataTag.MODULE.get());
        root.appendChild(module);
        Element load = document.createElement(MetadataTag.LOAD.get());
        load.appendChild(document.createTextNode(Boolean.toString(oArtifact.isLoad())));
        module.appendChild(load);
        Element trusted = document.createElement(MetadataTag.TRUSTED.get());
        trusted.appendChild(document.createTextNode(Boolean.toString(oArtifact.isTrusted())));
        module.appendChild(trusted);

        module.appendChild(createMavenDependency(oArtifact.getArtifactReference(), document));
    }

    /**
     * Change oArtifact in metadata.xml
     * @param artifactElement {@link Element} of oArtifact in metadata.xml
     * @param oArtifact {@link OArtifact} for change
     */
    @SuppressWarnings("unchecked")
    private void changeArtifactElement(Element artifactElement, OArtifact oArtifact) {
        NodeList nodeList = artifactElement.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                MetadataTag tag = MetadataTag.getByName(element.getTagName());
                switch (tag) {
                    case LOAD:
                        element.setTextContent(Boolean.toString(oArtifact.isLoad()));
                        break;
                    case TRUSTED:
                        element.setTextContent(Boolean.toString(oArtifact.isTrusted()));
                        break;
                    case DEPENDENCY:
                        changeMavenDependency(element, oArtifact.getArtifactReference());
                        break;
                }
            }
        }
    }

    /**
     * Change artifacts load and jar
     * @param artifactElement {@link Element} of oArtifact
     * @param oArtifact {@link OArtifact} artifact for change
     */
    @SuppressWarnings("unchecked")
    private void changeoArtifactsLoadAndJar(Element artifactElement, OArtifact oArtifact) {
        Element jar = (Element) artifactElement.getElementsByTagName(MetadataTag.JAR.get()).item(0);
        Element load = (Element) artifactElement.getElementsByTagName(MetadataTag.LOAD.get()).item(0);
        Document document = artifactElement.getOwnerDocument();
        load.setTextContent(Boolean.toString(oArtifact.isLoad()));
        if (jar == null) {
            Element jarElement = document.createElement(MetadataTag.JAR.get());
            jarElement.appendChild(document.createTextNode(oArtifact.getArtifactReference().getFile().getAbsolutePath()));
        } else jar.setTextContent(oArtifact.getArtifactReference().getFile().getAbsolutePath());
    }

    /**
     * Change maven dependency
     * @param dependency {@link Element} of dependency
     * @param artifactReference {@link OArtifactReference} for change
     */
    private void changeMavenDependency(Element dependency, OArtifactReference artifactReference) {
        NodeList nodeList = dependency.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                MetadataTag tag = MetadataTag.getByName(element.getTagName());
                switch (tag) {
                    case GROUP_ID:
                        element.setTextContent(artifactReference.getGroupId());
                        break;
                    case ARTIFACT_ID:
                        element.setTextContent(artifactReference.getArtifactId());
                        break;
                    case VERSION:
                        element.setTextContent(artifactReference.getVersion());
                        break;
                    case REPOSITORY:
                        element.setTextContent(artifactReference.getRepository());
                        break;
                    case DESCRIPTION:
                        element.setTextContent(artifactReference.getDescription());
                        break;
                    case JAR:
                        element.setTextContent(artifactReference.getFile().getAbsolutePath());
                        break;
                }
            }
        }
    }

    private boolean isNecessaryElement(Element dependencyElement, OArtifact module) {
        Element groupElement = (Element) dependencyElement.getElementsByTagName(MetadataTag.GROUP_ID.get()).item(0);
        Element artifactElement = (Element) dependencyElement.getElementsByTagName(MetadataTag.ARTIFACT_ID.get()).item(0);
        OArtifactReference artifact = module.getArtifactReference();
        return groupElement.getTextContent().equals(artifact.getGroupId())
                && artifactElement.getTextContent().equals(artifact.getArtifactId());
    }

    private OArtifact containsInModulesConfigsList(Element dependencyElement, List<OArtifact> modulesConfigs) {
        for (OArtifact moduleConfig : modulesConfigs) {
            if (isNecessaryElement(dependencyElement, moduleConfig)) return moduleConfig;
        }
        return null;
    }

    private List<OArtifact> difference(List<OArtifact> list1, List<OArtifact> list2) {
        List<OArtifact> result = Lists.newArrayList();
        for (OArtifact module : list2) {
            if (!list1.contains(module)) result.add(module);
        }
        return result;
    }


    /**
     * Add maven dependency to {@link Document} document.
     * @param artifactReference {@link OArtifactReference} which is maven dependency
     * @param document {@link Document} of metadata.xml
     * @return {@link Element} with maven dependency
     */
    private Element createMavenDependency(OArtifactReference artifactReference, Document document) {
        Element mavenElement = document.createElement(MetadataTag.DEPENDENCY.get());

        Element groupId = document.createElement(MetadataTag.GROUP_ID.get());
        groupId.appendChild(document.createTextNode(artifactReference.getGroupId()));
        mavenElement.appendChild(groupId);

        Element artifactId = document.createElement(MetadataTag.ARTIFACT_ID.get());
        artifactId.appendChild(document.createTextNode(artifactReference.getArtifactId()));
        mavenElement.appendChild(artifactId);

        Element version = document.createElement(MetadataTag.VERSION.get());
        version.appendChild(document.createTextNode(artifactReference.getVersion()));
        mavenElement.appendChild(version);

        Element description = document.createElement(MetadataTag.DESCRIPTION.get());
        description.appendChild(document.createTextNode(artifactReference.getDescription()));
        mavenElement.appendChild(description);

        Element jar = document.createElement(MetadataTag.JAR.get());
        jar.appendChild(document.createTextNode(artifactReference.getFile().getAbsolutePath()));
        mavenElement.appendChild(jar);

        return mavenElement;
    }
}
