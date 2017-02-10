package org.orienteer.core.loader.util.metadata;

import com.google.common.base.Optional;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import static org.orienteer.core.loader.util.metadata.MetadataUtil.ID;
import static org.orienteer.core.loader.util.metadata.MetadataUtil.MODULE;

/**
 * @author Vitaliy Gonchar
 */
abstract class DeleteMetadata {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteMetadata.class);

    static Optional<Path> deleteMetadata(OModuleMetadata moduleMetadata, Path metadataPath) {
        List<OModuleMetadata> allMetadata = MetadataUtil.readMetadata();
        if (!allMetadata.contains(moduleMetadata)) {
            LOG.error("Cannot delete module metadata. It doe not exists in metadata.xml");
            return Optional.absent();
        }
        try {
            Document document = getDocument(metadataPath);
            Element rootElement = document.getRootElement();
            List<Element> children = rootElement.getChildren(MODULE);
            Iterator<Element> iterator = children.iterator();
            while (iterator.hasNext()) {
                Element child = iterator.next();
                String id = child.getChild(ID).getValue();
                if (id.equals(Integer.toString(moduleMetadata.getId()))) {
                    iterator.remove();
                    break;
                }
            }
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, Files.newOutputStream(metadataPath));
        } catch (IOException | JDOMException e) {
            LOG.error("Cannot open file: " + metadataPath);
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }

        return Optional.of(metadataPath);
    }

    private static Document getDocument(Path metadataXml) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(Files.newInputStream(metadataXml));
    }
}
