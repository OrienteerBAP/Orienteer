package org.orienteer.core.component.meta;

import com.orientechnologies.orient.core.compression.OCompressionFactory;
import com.orientechnologies.orient.core.conflict.ORecordConflictStrategy;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.storage.OCluster;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.property.BooleanViewPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.widget.schema.OClustersWidget;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.io.IOException;
import java.util.*;

/**
 * Meta panel for {@link OCluster}
 *
 * @param <V> type of a value
 */
public class OClusterMetaPanel<V> extends AbstractComplexModeMetaPanel<OCluster, DisplayMode, String, V> implements IDisplayModeAware
{
    public static final List<String> OCLUSTER_ATTRS = Arrays.asList(OClustersWidget.NAME,
            OClustersWidget.CONFLICT_STRATEGY, OClustersWidget.COUNT, OClustersWidget.COMPRESSION,
            OClustersWidget.RECORD_GROW_FACTOR, OClustersWidget.RECORD_OVERFLOW_GROW_FACTOR, OClustersWidget.USE_WAL);

    public static final List<String> COMPRESSIONS = new ArrayList<String>();

    public OClusterMetaPanel(String id, IModel<DisplayMode> modeModel, IModel<OCluster> entityModel, IModel<String> criteryModel) {
        super(id, modeModel, entityModel, criteryModel);

        if(COMPRESSIONS.isEmpty()) {
            COMPRESSIONS.addAll(OCompressionFactory.INSTANCE.getCompressions());
        }
        Collections.sort(COMPRESSIONS);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected V getValue(OCluster entity, String critery) {
        if(OClustersWidget.CONFLICT_STRATEGY.equals(critery))
        {
            ORecordConflictStrategy strategy = entity.getRecordConflictStrategy();
            return (V)(strategy!=null?strategy.getName():null);
        }
        else if(OClustersWidget.COMPRESSION.equals(critery))
        {
            return (V)entity.compression();
        }
        else {
            return (V) PropertyResolver.getValue(critery, entity);
        }
    }

    @Override
    protected void setValue(OCluster entity, String critery, V value) {
        ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
        db.commit();
        try
        {
            if(OClustersWidget.COMPRESSION.equals(critery)) {
                entity.set(OCluster.ATTRIBUTES.COMPRESSION, value);
            }
            else if(OClustersWidget.NAME.equals(critery)) {
                entity.set(OCluster.ATTRIBUTES.NAME, value);
            }
            else if(OClustersWidget.RECORD_GROW_FACTOR.equals(critery)) {
                entity.set(OCluster.ATTRIBUTES.RECORD_GROW_FACTOR, value);
            }
            else if(OClustersWidget.RECORD_OVERFLOW_GROW_FACTOR.equals(critery)) {
                entity.set(OCluster.ATTRIBUTES.RECORD_OVERFLOW_GROW_FACTOR, value);
            }
            else if(OClustersWidget.USE_WAL.equals(critery)) {
                entity.set(OCluster.ATTRIBUTES.USE_WAL, value);
            }
            else if(OClustersWidget.CONFLICT_STRATEGY.equals(critery)) {
                entity.set(OCluster.ATTRIBUTES.CONFLICTSTRATEGY, value);
            }
            else {
                entity.set(OCluster.ATTRIBUTES.valueOf(critery), value);
            }
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
        finally
        {
            db.begin();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Component resolveComponent(String id, DisplayMode mode, String critery) {

        if(DisplayMode.EDIT.equals(mode) && !OSecurityHelper.isAllowed(ORule.ResourceGeneric.SCHEMA, null, OrientPermission.UPDATE))
        {
            mode = DisplayMode.VIEW;
        }
        if(DisplayMode.VIEW.equals(mode))
        {
            if(OClustersWidget.USE_WAL.equals(critery)) {
                return new BooleanViewPanel(id, (IModel<Boolean>)getModel()).setDefaultValue(false);
            }
            return new Label(id, getModel());
        }
        else if(DisplayMode.EDIT.equals(mode)) {
            if (OClustersWidget.COMPRESSION.equals(critery)) {
                return new DropDownChoice<String>(id, (IModel<String>)getModel(), COMPRESSIONS);
            }
            else if (OClustersWidget.USE_WAL.equals(critery)) {
                return new BooleanEditPanel(id, (IModel<Boolean>)getModel());
            }
            else if(OClustersWidget.COUNT.equals(critery)){
                return resolveComponent(id, DisplayMode.VIEW, critery);
            } else {
                return new TextField<V>(id, getModel()).setType(String.class);
            }
        }
        return null;
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new SimpleNamingModel<String>("cluster."+ getPropertyObject().toLowerCase());
    }
}
