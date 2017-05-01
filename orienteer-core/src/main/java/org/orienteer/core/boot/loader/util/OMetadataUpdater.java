package org.orienteer.core.boot.loader.util;

import com.google.common.collect.Lists;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
class OMetadataUpdater extends AbstractXmlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OMetadataUpdater.class);

    private final Path pathToMetadata;

    private static final String ALL_MODULES_EXP = String.format("/%s/*", MetadataTag.METADATA.get());

    OMetadataUpdater(Path pathToMetadata) {
        this.pathToMetadata = pathToMetadata;
    }

    void create(List<OArtifact> oArtifacts) {
        Document document = createNewDocument();
        if (document == null) documentCannotCreateException(pathToMetadata);

        Element root = document.createElement(MetadataTag.METADATA.get());
        document.appendChild(root);
        addModules(oArtifacts, document);
        saveDocument(document, pathToMetadata);
    }

    void update(OArtifact oArtifact) {
        update(oArtifact, false);
    }

    void update(List<OArtifact> oArtifacts) {
        update(oArtifacts, false);
    }

    void update(OArtifact oArtifact, boolean updateJar) {
        update(Lists.newArrayList(oArtifact), updateJar);
    }

    @SuppressWarnings("unchecked")
    void update(List<OArtifact> oArtifacts, boolean updateJar) {
        Document document = readDocumentFromFile(pathToMetadata);
        if (document == null) documentCannotReadException(pathToMetadata);

        NodeList nodeList = executeExpression(ALL_MODULES_EXP, document);
        List<OArtifact> updatedModules = Lists.newArrayList();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = (Element) nodeList.item(i);
                if (node.getNodeType() == Element.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Element dependencyElement = (Element) element.getElementsByTagName(MetadataTag.DEPENDENCY.get()).item(0);
                    OArtifact oArtifact = containsInModulesConfigsList(dependencyElement, oArtifacts);
                    if (oArtifact != null) {
                        if (updateJar) {
                            changeoArtifactsLoadAndJar(element, oArtifact);
                        } else changeModule(element, oArtifact);
                        updatedModules.add(oArtifact);
                    }
                }
            }
        }
        if (updatedModules.size() != oArtifacts.size()) {
            addModules(difference(updatedModules, oArtifacts), document);
        }
        saveDocument(document, pathToMetadata);
    }

    @SuppressWarnings("unchecked")
    void update(OArtifact oArtifact, OArtifact newModuleConfig) {
        Document document = readDocumentFromFile(pathToMetadata);
        if (document == null) documentCannotReadException(pathToMetadata);
        NodeList nodeList = executeExpression(ALL_MODULES_EXP, document);
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Element dependencyElement = (Element) element.getElementsByTagName(MetadataTag.DEPENDENCY.get()).item(0);
                    OArtifact module = containsInModulesConfigsList(dependencyElement, Lists.newArrayList(oArtifact));
                    if (module != null) {
                        changeModule(element, newModuleConfig);
                        break;
                    }
                }
            }
            saveDocument(document, pathToMetadata);
        }
    }

    @SuppressWarnings("unchecked")
    void delete(OArtifact oArtifact) {
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

    @SuppressWarnings("unchecked")
    void delete(List<OArtifact> oArtifacts) {
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

    private void addModules(List<OArtifact> oArtifacts, Document document) {
        for (OArtifact module : oArtifacts) {
            addModule(module, document);
        }
    }

    private void addModule(OArtifact oArtifact, Document document) {
        Element root = document.getDocumentElement();
        Element module = document.createElement(MetadataTag.MODULE.get());
        root.appendChild(module);
        Element load = document.createElement(MetadataTag.LOAD.get());
        load.appendChild(document.createTextNode(Boolean.toString(oArtifact.isLoad())));
        module.appendChild(load);
        Element trusted = document.createElement(MetadataTag.TRUSTED.get());
        trusted.appendChild(document.createTextNode(Boolean.toString(oArtifact.isTrusted())));
        module.appendChild(trusted);

        addMavenDependency(oArtifact.getArtifactReference(), document, module);
    }

    @SuppressWarnings("unchecked")
    private void changeModule(Element moduleElement, OArtifact oArtifact) {
        NodeList nodeList = moduleElement.getElementsByTagName("*");
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

    @SuppressWarnings("unchecked")
    private void changeoArtifactsLoadAndJar(Element moduleElement, OArtifact oArtifact) {
        Element jar = (Element) moduleElement.getElementsByTagName(MetadataTag.JAR.get()).item(0);
        Element load = (Element) moduleElement.getElementsByTagName(MetadataTag.LOAD.get()).item(0);
        Document document = moduleElement.getOwnerDocument();
        load.setTextContent(Boolean.toString(oArtifact.isLoad()));
        if (jar == null) {
            Element jarElement = document.createElement(MetadataTag.JAR.get());
            jarElement.appendChild(document.createTextNode(oArtifact.getArtifactReference().getFile().getAbsolutePath()));
        } else jar.setTextContent(oArtifact.getArtifactReference().getFile().getAbsolutePath());
    }

    private void changeMavenDependency(Element dependency, OArtifactReference artifact) {
        NodeList nodeList = dependency.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                MetadataTag tag = MetadataTag.getByName(element.getTagName());
                switch (tag) {
                    case GROUP_ID:
                        element.setTextContent(artifact.getGroupId());
                        break;
                    case ARTIFACT_ID:
                        element.setTextContent(artifact.getArtifactId());
                        break;
                    case VERSION:
                        element.setTextContent(artifact.getVersion());
                        break;
                    case REPOSITORY:
                        element.setTextContent(artifact.getRepository());
                        break;
                    case DESCRIPTION:
                        element.setTextContent(artifact.getDescription());
                        break;
                    case JAR:
                        element.setTextContent(artifact.getFile().getAbsolutePath());
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

//    private Artifact containsInDependencies(Element element, List<Artifact> dependencies) {
//        String groupId = element.element(MetadataTag.GROUP_ID.get()).getText();
//        String artifactId = element.element(MetadataTag.ARTIFACT_ID.get()).getText();
//        for (Artifact dependency : dependencies) {
//            if (dependency.getGroupId().equals(groupId) && dependency.getArtifactId().equals(artifactId)) {
//                return dependency;
//            }
//        }
//        return null;
//    }

    private Element addMavenDependency(OArtifactReference artifact, Document document, Element element) {
        Element mavenElement = document.createElement(MetadataTag.DEPENDENCY.get());
        element.appendChild(mavenElement);

        Element groupId = document.createElement(MetadataTag.GROUP_ID.get());
        groupId.appendChild(document.createTextNode(artifact.getGroupId()));
        mavenElement.appendChild(groupId);

        Element artifactId = document.createElement(MetadataTag.ARTIFACT_ID.get());
        artifactId.appendChild(document.createTextNode(artifact.getArtifactId()));
        mavenElement.appendChild(artifactId);

        Element version = document.createElement(MetadataTag.VERSION.get());
        version.appendChild(document.createTextNode(artifact.getVersion()));
        mavenElement.appendChild(version);

        Element description = document.createElement(MetadataTag.DESCRIPTION.get());
        description.appendChild(document.createTextNode(artifact.getDescription()));
        mavenElement.appendChild(description);

        Element jar = document.createElement(MetadataTag.JAR.get());
        jar.appendChild(document.createTextNode(artifact.getFile().getAbsolutePath()));
        mavenElement.appendChild(jar);

        return mavenElement;
    }
}
