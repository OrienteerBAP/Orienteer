package org.orienteer.core.service.impl;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializablePredicate;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.service.IFilterPredicateFactory;

/**
 * Default implementation for {@link IFilterPredicateFactory}
 */
public class DefaultFilterPredicateFactory implements IFilterPredicateFactory {

    @Override
    public SerializablePredicate<OClass> getPredicateByTarget(IModel<Boolean> showAllModel) {
        return (input) -> {
            Boolean showAll = showAllModel.getObject();
            return showAll == null || showAll || OClassDomain.BUSINESS.equals(CustomAttribute.DOMAIN.getValue(input));
        };
    }

    @Override
    public SerializablePredicate<OClass> getPredicateByOperation(int operation) {
        OSecurityUser user = OrienteerWebSession.get().getUser();
        return (input) -> user.checkIfAllowed(ORule.ResourceGeneric.CLASS, input.getName(), operation) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OClass> getPredicateForClassesView(IModel<Boolean> showAll) {
        return compose(getPredicateByOperation(2), getPredicateByTarget(showAll));
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OClass> getPredicateForClassesSearch() {
        return compose(getPredicateByOperation(2), getPredicateByTarget(Model.of(false)));
    }
}
