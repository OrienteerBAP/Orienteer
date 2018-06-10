package org.orienteer.core.service.impl;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializablePredicate;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.service.IFilterPredicateFactory;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

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
        return user != null ? (input) -> user.checkIfAllowed(ORule.ResourceGeneric.CLASS, input.getName(), operation) != null 
        		: (i) -> false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OClass> getPredicateForClassesView(IModel<Boolean> showAll) {
        return compose(getPredicateByOperation(2), getPredicateByTarget(showAll));
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OClass> getPredicateForClassesSearch() {
        return compose(getPredicateByOperation(2), getPredicateByTarget(Model.of(false)), getPredicateByFeature(OrientPermission.READ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OProperty> getPredicateForListProperties() {
        return compose(
                (prop) -> !(Boolean)CustomAttribute.HIDDEN.getValue(prop),
                getPredicateByFeatureProp(OrientPermission.READ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OProperty> getPredicateForTableProperties() {
        return compose(
                CustomAttribute.DISPLAYABLE::getValue,
                getPredicateByFeatureProp(OrientPermission.READ)
        );
    }

    private SerializablePredicate<OClass> getPredicateByFeature(OrientPermission permission) {
        return (input) -> {
            String feature = CustomAttribute.FEATURE.getValue(input);
            return Strings.isNullOrEmpty(feature) || OSecurityHelper.isAllowed(OSecurityHelper.FEATURE_RESOURCE, feature, permission);
        };
    }

    private SerializablePredicate<OProperty> getPredicateByFeatureProp(OrientPermission permission) {
        return (input) -> {
            String feature = CustomAttribute.FEATURE.getValue(input);
            return Strings.isNullOrEmpty(feature) || OSecurityHelper.isAllowed(OSecurityHelper.FEATURE_RESOURCE, feature, permission);
        };
    }
}
