package org.orienteer.core.component.meta;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OBalancedClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ODefaultClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ORoundRobinClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.security.ORule;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.property.BooleanViewPanel;
import org.orienteer.core.component.property.DisplayMode;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.orientechnologies.orient.core.db.ODatabase.ATTRIBUTES;

/**
 * Meta panel for {@link ODatabase}
 *
 * @param <V> type of a value
 */
public class ODatabaseMetaPanel<V> extends AbstractComplexModeMetaPanel<ODatabase, DisplayMode, String, V> implements IDisplayModeAware{
    public static final List<String> ODATABASE_ATTRS = new ArrayList<String>();

    static
    {
        for(ATTRIBUTES attr: ATTRIBUTES.values()) {
            if(ATTRIBUTES.CUSTOM != attr) {
                ODATABASE_ATTRS.add(attr.name());
            }
        }
    }

    private static final long serialVersionUID = 1L;
    private static final List<String> CLUSTER_SELECTIONS =
            Arrays.asList(new String[]{ODefaultClusterSelectionStrategy.NAME, ORoundRobinClusterSelectionStrategy.NAME, OBalancedClusterSelectionStrategy.NAME});

    public ODatabaseMetaPanel(String id, IModel<DisplayMode> modeModel, IModel<ODatabase> entityModel,
                              IModel<String> criteryModel) {
        super(id, modeModel, entityModel, criteryModel);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected V getValue(ODatabase entity, String critery) {
        return (V) entity.get(ATTRIBUTES.valueOf(critery));
    }

    @Override
    protected void setValue(ODatabase entity, String critery, V value) {
        ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
        db.commit();
        try
        {
//            if(ATTRIBUTES.CLUSTERSELECTION.name().equals(critery))
//            {
//                if(value!=null) entity.set(ATTRIBUTES.valueOf(critery), value.toString());
//            } else {
                entity.set(ATTRIBUTES.valueOf(critery), value);
//            }
        } finally
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
//            if(ATTRIBUTES.VALIDATION.name().equals(critery))
//            {
//                return new BooleanViewPanel(id, (IModel<Boolean>)getModel()).setHideIfFalse(true);
//            }
            return new Label(id, getModel());
        }
        else if(DisplayMode.EDIT.equals(mode)) {
//            if(ATTRIBUTES.CLUSTERSELECTION.name().equals(critery))
//            {
//                return new DropDownChoice<String>(id, (IModel<String>)getModel(), CLUSTER_SELECTIONS);
//            } else if(ATTRIBUTES.VALIDATION.name().equals(critery))
//            {
//                return new BooleanEditPanel(id, (IModel<Boolean>)getModel());
//            } else {
                return new TextField<V>(id, getModel()).setType(String.class);
//            }
        }
            return null;
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new SimpleNamingModel<String>("database", getPropertyModel());
    }
}
