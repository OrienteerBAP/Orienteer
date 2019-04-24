package org.orienteer.pages.wicket.mapper;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.web.ODocumentsPage;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ODocumentsAliasMapper extends AbstractODocumentAliasMapper<List<ORID>> {

    public static final String PARAM_DOCS = "docs";

    public ODocumentsAliasMapper(String url, OQueryModel<ODocument> model) {
        super(url, ODocumentsPage.class, model, PARAM_DOCS, new TransparentParameterPageEncoder(PARAM_DOCS));

    }

    @Override
    protected List<ORID> convertDocumentsToValue(List<ODocument> documents) {
        if (documents == null || documents.size() == 1) {
            return null;
        }
        return documents.stream()
                .map(ODocument::getIdentity)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    protected String convertValueToString(List<ORID> value) {
        if (value == null) {
            return null;
        }
        return value.stream()
                .map(ORID::toString)
                .collect(Collectors.joining(","));
    }
}
