package org.orienteer.core.service;

import org.apache.wicket.core.util.resource.locator.IResourceNameIterator;
import org.apache.wicket.core.util.resource.locator.IResourceStreamLocator;
import org.apache.wicket.util.resource.IResourceStream;

import java.util.Locale;

/**
 * @author Vitaliy Gonchar
 */
public class OrienteerResourceStreamLocator implements IResourceStreamLocator {

    private final IResourceStreamLocator delegate;

    private static final String WEBJARS_ABSOLUTE_PATH = "de/agilecoders/wicket/webjars/request/resource/";

    public OrienteerResourceStreamLocator(IResourceStreamLocator delegate) {
        this.delegate = delegate;
    }

    @Override
    public IResourceStream locate(Class<?> clazz, String path) {
        String newPath = getNonAbsolutePathForWebjars(path);
        return delegate.locate(clazz, newPath);
    }


    @Override
    public IResourceStream locate(Class<?> clazz, String path, String style, String variation,
                                  Locale locale, String extension, boolean strict) {
        String newPath = getNonAbsolutePathForWebjars(path);
        return delegate.locate(clazz, newPath, style, variation, locale, extension, strict);
    }

    @Override
    public IResourceNameIterator newResourceNameIterator(String path, Locale locale, String style,
                                                         String variation, String extension, boolean strict) {
        String newPath = getNonAbsolutePathForWebjars(path);
        return delegate.newResourceNameIterator(newPath, locale, style, variation, extension, strict);
    }

    private String getNonAbsolutePathForWebjars(String path) {
        return path.contains(WEBJARS_ABSOLUTE_PATH) ? path.substring(WEBJARS_ABSOLUTE_PATH.length()) : path;
    }
}
