package org.orienteer.core.boot.loader.util;

import org.apache.http.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Abstract utility class for work with xml
 */
class AbstractXmlUtil implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractXmlUtil.class);

    /**
     * Create {@link Document} from {@link Path}
     * @param xml - xml file
     * @return - created {@link Document} or null if can't create {@link Document}
     * @throws IllegalArgumentException if xml is null
     */
    protected final Document readDocumentFromFile(Path xml) {
        Args.notNull(xml, "xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            return factory.newDocumentBuilder().parse(xml.toFile());
        } catch (SAXException | ParserConfigurationException | IOException e) {
            LOG.warn("Can't create document xml! Path: {}", xml.toAbsolutePath(), e);
        }
        return null;
    }

    /**
     * Create new {@link Document}
     * @return new {@link Document} or null if can't create {@link Document}
     */
    protected final Document createNewDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.newDocument();
        } catch (ParserConfigurationException e) {
            LOG.warn("Cannot create new document!", e);
        }
        return null;
    }

    /**
     * Save {@link Document} document in file system with {@link Path} xml
     * @param document - {@link Document} for save
     * @param xml - {@link Path} for saving document
     * @throws IllegalArgumentException if document or xml is null
     */
    protected final void saveDocument(Document document, Path xml) {
        Args.notNull(document, "document");
        Args.notNull(xml, "xml");
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xml.toFile());
            transformer.transform(source, result);
        } catch (TransformerException e) {
            LOG.error("Cannot save document!", e);
        }
    }

    /**
     * Execute XPath expression
     * @param expression - {@link String} expression for execute
     * @param document {@link Document} in which expression will execute
     * @return {@link NodeList} with result or null if can't execute expression.
     * @throws IllegalArgumentException if expression or document is null
     */
    protected final NodeList executeExpression(String expression, Document document) {
        Args.notNull(expression, "expression");
        Args.notNull(document, "document");
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOG.warn("Can't execute search query: {},", expression , e);
        }
        return null;
    }

    /**
     * @throws IllegalStateException
     * @param path - {@link Path} for information
     */
    protected final void documentCannotCreateException(Path path) {
        throw new IllegalStateException("Can't create document from file: " + path.toAbsolutePath());
    }

    /**
     * @throws IllegalStateException
     * @param path - {@link Path} for information
     */
    protected final void documentCannotReadException(Path path) {
        throw new IllegalStateException("Can't read document from file: " + path.toAbsolutePath());
    }
}
