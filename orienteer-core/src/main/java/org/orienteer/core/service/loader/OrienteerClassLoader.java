package org.orienteer.core.service.loader;

import com.google.common.collect.Lists;
import org.apache.wicket.WicketRuntimeException;
import org.kevoree.kcl.impl.FlexyClassLoaderImpl;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Vitaliy Gonchar
 */
public class OrienteerClassLoader extends FlexyClassLoaderImpl {

    private static final Logger LOG = LoggerFactory.getLogger(OrienteerClassLoader.class);

    private final ClassLoader parent;

    private OrienteerClassLoader(ClassLoader parent) {
        super();
        this.parent = parent;
    }

    private OrienteerClassLoader() {
        super();
        this.parent = OrienteerWebApplication.class.getClassLoader();
    }

    public static OrienteerClassLoader create(ClassLoader parent) {
        return new OrienteerClassLoader(parent);
    }

    public static OrienteerClassLoader create() {
        return new OrienteerClassLoader();
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> resources = getListFromEnumeration(super.getResources(name));
        resources.addAll(getListFromEnumeration(parent.getResources(name)));

        return Collections.enumeration(resources);
    }

    private List<URL> getListFromEnumeration(Enumeration<URL> resources) {
        List<URL> list = Lists.newArrayList();
        while (resources.hasMoreElements()) {
            list.add(resources.nextElement());
        }
        return list;
    }

}
