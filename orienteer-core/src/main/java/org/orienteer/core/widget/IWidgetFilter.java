package org.orienteer.core.widget;

import java.io.Serializable;

import org.apache.wicket.util.io.IClusterable;

import com.google.common.base.Predicate;


/**
 * Dummy interface over {@link Predicate} just to make it {@link Serializable}
 *
 * @param <T> the type of widget's main object
 */
public interface IWidgetFilter<T> extends Predicate<IWidgetType<T>>, IClusterable{
}
