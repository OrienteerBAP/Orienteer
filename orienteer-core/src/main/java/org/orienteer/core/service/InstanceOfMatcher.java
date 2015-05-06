package org.orienteer.core.service;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

/**
 * {@link Matcher} to check that object to be created are of specified class
 *
 * @param <T> class to check for
 */
public class InstanceOfMatcher<T> extends AbstractMatcher<TypeLiteral<?>>
{
	private final Class<T> clazz;
	protected InstanceOfMatcher(Class<T> clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public boolean matches(TypeLiteral<?> t) {
		return clazz.isAssignableFrom(t.getRawType());
	}
	
	public static <T> InstanceOfMatcher<T> createFor(Class<T> clazz)
	{
		return new InstanceOfMatcher<T>(clazz);
	}

}
