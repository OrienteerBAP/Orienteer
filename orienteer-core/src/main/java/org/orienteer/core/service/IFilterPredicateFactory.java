package org.orienteer.core.service;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import org.danekja.java.util.function.serializable.SerializablePredicate;
import org.orienteer.core.service.impl.DefaultFilterPredicateFactory;

/**
 * Service for dynamic generate filter predicates
 */
@ImplementedBy(DefaultFilterPredicateFactory.class)
public interface IFilterPredicateFactory extends IClusterable {

    /**
     * Create predicate which filter classes by target. Targets: BUSINESS, ALL.
     * @param showAll {@link IModel} if true - target = ALL
     * @return predicate
     */
    public SerializablePredicate<OClass> getPredicateByTarget(IModel<Boolean> showAll);

    /**
     * Create predicate which filter classes by user permissions for this class.
     * @param operation {@link int} user operation. See OrientDB security operations.
     * @return predicate
     */
    public SerializablePredicate<OClass> getPredicateByOperation(int operation);

    /**
     * Create predicate for classes view
     * @param showAll {@link IModel} if true - target = ALL
     * @return predicate
     */
    public SerializablePredicate<OClass> getPredicateForClassesView(IModel<Boolean> showAll);

    /**
     * Create predicate for classes search
     * @return predicate
     */
    public SerializablePredicate<OClass> getPredicateForClassesSearch();

    public SerializablePredicate<OProperty> getPredicateForListProperties();

    public SerializablePredicate<OProperty> getPredicateForTableProperties();

    /**
     * Compose predicates
     * @param predicates {@link SerializablePredicate} predicates for compose
     * @param <V> type of predicate
     * @return predicate which contains composed predicates
     * If predicates are empty or null then returns (v) then false
     */
    public default <V> SerializablePredicate<V> compose(SerializablePredicate<V>...predicates) {
        SerializablePredicate<V> result;
        if (predicates != null && predicates.length > 0) {
            result = predicates[0];
            for (int i = 1; i < predicates.length; ++i) {
                result = result.and(predicates[i]);
            }
        } else result = (v) -> false;

        return result;
    }

    /**
     * See {@link IFilterPredicateFactory#getPredicateForClassesView(IModel)}
     * @param showAll {@link IModel} if true - target = ALL
     * @return predicate
     */
    @Deprecated
    public default IGuicePredicate<OClass> getGuicePredicateForClassesView(IModel<Boolean> showAll) {
        SerializablePredicate<OClass> p = getPredicateForClassesView(showAll);
        return (IGuicePredicate<OClass>) p::test;
    }

    /**
     * See {@link IFilterPredicateFactory#getPredicateForClassesSearch()}
     * @return predicate
     */
    @Deprecated
    public default IGuicePredicate<OClass> getGuicePredicateForClassesSearch() {
        SerializablePredicate<OClass> p = getPredicateForClassesSearch();
        return (IGuicePredicate<OClass>) p::test;
    }

    @Deprecated
    public default IGuicePredicate<OProperty> getGuicePredicateForListProperties() {
        SerializablePredicate<OProperty> p = getPredicateForListProperties();
        return (IGuicePredicate<OProperty>) p::test;
    }

    @Deprecated
    public default IGuicePredicate<OProperty> getGuicePredicateForTableProperties() {
        SerializablePredicate<OProperty> p = getPredicateForTableProperties();
        return (IGuicePredicate<OProperty>) p::test;
    }

    /**
     * Guice Predicate interface which extends {@link IClusterable}
     * @param <V> predicate type
     */
    @Deprecated
    public interface IGuicePredicate<V> extends Predicate<V>, IClusterable {

    }
}
