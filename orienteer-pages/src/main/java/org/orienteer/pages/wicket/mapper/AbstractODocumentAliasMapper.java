package org.orienteer.pages.wicket.mapper;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.INamedParameters;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaManager;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Abstract document alias mapper for map documents
 * @param <V> type fo map
 */
public abstract class AbstractODocumentAliasMapper<V> extends AbstractMountedMapper {

    private final OQueryModel<ODocument> queryModel;
    private final String parameter;
    private final String mountPath;

    public AbstractODocumentAliasMapper(String mountPath,
                                        Class<? extends IRequestablePage> pageClass,
                                        OQueryModel<ODocument> queryModel,
                                        String parameter,
                                        IPageParametersEncoder pageParametersEncoder) {
        super(mountPath, pageClass, pageParametersEncoder);
        this.queryModel = queryModel;
        this.parameter = parameter;
        this.mountPath = mountPath;
    }

    public AbstractODocumentAliasMapper(String mountPath, Supplier<Class<? extends IRequestablePage>> pageProvider,
                                        OQueryModel<ODocument> queryModel, String parameter, IPageParametersEncoder encoder) {
        super(mountPath, pageProvider, encoder);
        this.queryModel = queryModel;
        this.parameter = parameter;
        this.mountPath = mountPath;
    }

    protected abstract V convertDocumentsToValue(List<ODocument> documents);
    protected abstract String convertValueToString(V value);

    @Override
    protected boolean isMatch(PageParameters parameters) {
        String sv = parameters != null ? parameters.get(parameter).toOptionalString() : null;
        return Objects.equals(sv, getValueAsString(parameters));
    }

    @Override
    protected UrlInfo parseRequest(Request request) {
        UrlInfo urlInfo = super.parseRequest(request);
        String param = urlInfo != null ? urlInfo.getPageParameters().get(parameter).toOptionalString() : null;
        if (!Strings.isNullOrEmpty(param)) {
            return urlInfo;
        }
        return null;
    }

    @Override
    protected PageParameters extractPageParameters(Request request, Url url) {
        PageParameters parameters = super.extractPageParameters(request, url);
        String value = getValueAsString(parameters);

        if (value != null) {
            parameters.set(parameter, value);
        }

        return parameters;
    }

    public String getMountPath() {
        return mountPath;
    }

    private V getValue(PageParameters parameters) {
        applyFilters(parameters, queryModel);
        List<ODocument> docs = queryModel.getObject();
        V value = convertDocumentsToValue(docs);
        queryModel.clearFilterCriteriaManagers();
        queryModel.detach();
        return value;
    }

    private String getValueAsString(PageParameters parameters) {
        V value = getValue(parameters);
        return convertValueToString(value);
    }

    protected void applyFilters(PageParameters parameters, OQueryModel<ODocument> queryModel) {
        IModel<Boolean> joining = Model.of(true);
        for (INamedParameters.NamedPair pair : parameters.getAllNamed()) {
            String param = pair.getValue();
            if (!Strings.isNullOrEmpty(param) && !Objects.equals(pair.getKey(), parameter)) {
                IFilterCriteriaManager manager = new FilterCriteriaManager(pair.getKey());
                manager.addFilterCriteria(manager.createEqualsFilterCriteria(Model.of(param), joining));
                queryModel.addFilterCriteriaManager(pair.getKey(), manager);
            }
        }
    }
}
