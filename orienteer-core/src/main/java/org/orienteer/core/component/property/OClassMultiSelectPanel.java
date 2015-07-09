package org.orienteer.core.component.property;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;
import org.apache.wicket.model.IModel;
import org.orienteer.core.model.ListOClassesModel;

import java.util.*;

/**
 * {@link org.orienteer.core.component.property.MultiSelectPanel} to select multiple OClasses
 */
public class OClassMultiSelectPanel extends MultiSelectPanel<OClass> {

    public OClassMultiSelectPanel(String id, IModel<Collection<OClass>> model) {
        super(id, model);
    }

    @Override
    protected ChoiceProvider<OClass> getProvider() {
        final ListOClassesModel listOClassesModel = new ListOClassesModel();

        return new TextChoiceProvider<OClass>() {

            @Override
            public void query(String s, int i, Response<OClass> response) {
                response.addAll(listOClassesModel.getObject());
            }

            @Override
            public Collection<OClass> toChoices(Collection<String> collection) {
                final Collection<OClass> classes = listOClassesModel.getObject();
                final Map<String, OClass> classesByNames = new HashMap<String, OClass>();
                for (OClass clazz : classes) {
                    classesByNames.put(clazz.getName(), clazz);
                }

                return Collections2.transform(collection, new Function<String, OClass>() {
                    @Override
                    public OClass apply(String s) {
                        return classesByNames.get(s);
                    }
                });
            }

            @Override
            protected String getDisplayText(OClass choice) {
                return choice.getName();
            }

            @Override
            protected Object getId(OClass choice) {
                return choice.getName();
            }
        };
    }
}
