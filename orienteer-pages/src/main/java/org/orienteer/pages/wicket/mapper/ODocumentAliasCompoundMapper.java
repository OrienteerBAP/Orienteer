package org.orienteer.pages.wicket.mapper;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.CompoundRequestMapper;
import org.orienteer.pages.repository.ODocumentAliasRepository;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * {@link IRequestMapper} for map documents
 */
public class ODocumentAliasCompoundMapper extends CompoundRequestMapper {

    private final BiFunction<String, OQueryModel<ODocument>, IRequestMapper> mapperCreator;

    public ODocumentAliasCompoundMapper(BiFunction<String, OQueryModel<ODocument>, IRequestMapper> mapperCreator) {
        super();
        this.mapperCreator = mapperCreator;

        getAliasClasses().forEach(map -> {
            OQueryModel<ODocument> model = new OQueryModel<>("select from " + map.getValue());
            String key = map.getKey();

            if (!key.startsWith("/")) {
                key = "/" + key;
            }

            if (key.endsWith("/")) {
                key = key.substring(0, key.length() - 1);
            }

            if (key.split("/").length == 2) { // check if key contains only one segment
                key = "/" + map.getValue().getName().toLowerCase() + key;
            }

            add(key, model);
        });
    }

    protected List<Pair<String, OClass>> getAliasClasses() {
        return ODocumentAliasRepository.getAliasClasses();
    }

    public ODocumentAliasCompoundMapper add(String url, OQueryModel<ODocument> model) {
        add(mapperCreator.apply(url, model));
        return this;
    }

    public ODocumentAliasCompoundMapper remove(String url) {
        for (IRequestMapper mapper : this) {
            if (mapper instanceof AbstractODocumentAliasMapper) {
                AbstractODocumentAliasMapper<?> docMapper = (AbstractODocumentAliasMapper<?>) mapper;
                if (Objects.equals(docMapper.getMountPath(), url)) {
                    remove(docMapper);
                    break;
                }
            }
        }

        return this;
    }
}
