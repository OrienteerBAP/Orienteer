package org.orienteer.pages.wicket.mapper;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.web.ODocumentPage;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import java.util.List;

public class ODocumentAliasMapper extends AbstractODocumentAliasMapper<ORID> {

    public static final String PARAM_RID = "rid";

    public ODocumentAliasMapper(String url, OQueryModel<ODocument> model) {
        super(
                url,
                ODocumentPage.class,
                model,
                PARAM_RID,
                new TransparentParameterPageEncoder(PARAM_RID)
        );
    }

    @Override
    protected ORID convertDocumentsToValue(List<ODocument> documents) {
        ORID result = null;
        if (documents != null && documents.size() == 1) {
            result = documents.get(0).getIdentity();
        }
        return result;
    }

    @Override
    protected String convertValueToString(ORID value) {
        return value != null ? value.toString() : null;
    }

}
