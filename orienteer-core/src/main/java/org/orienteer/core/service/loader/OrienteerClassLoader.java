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

    public static OrienteerClassLoader get(ClassLoader parent) {
        return new OrienteerClassLoader(parent);
    }

    public static OrienteerClassLoader get() {
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

    public List<Class<? extends AbstractWidget<Object>>> getWidgetsInPackage(String packageName) {
        List<Class<? extends AbstractWidget<Object>>> widgets = Lists.newArrayList();
        try {
            for (URL url : getAllResources()) {
                String file = url.getFile().replace('/', '.');
                if (isTopClass(file) && isInPackage(file, packageName)) {
                    Class<?> clazz = this.loadClass(getClassName(url.getFile()));
                    Widget widgetDescription = clazz.getAnnotation(Widget.class);
                    if (widgetDescription != null) {
                        if(!AbstractWidget.class.isAssignableFrom(clazz))
                            throw new WicketRuntimeException("@"+Widget.class.getSimpleName()+" should be only on widgets");
                        Class<? extends AbstractWidget<Object>> widgetClass = (Class<? extends AbstractWidget<Object>>) clazz;
                        widgets.add(widgetClass);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            LOG.warn("Cannot load class in package: " + packageName);
            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
        return widgets;
    }

    private boolean isTopClass(String url) {
        return url.endsWith(".class") && !url.contains("$");
    }

    private boolean isInPackage(String url, String packageName) {
        return url.contains(packageName);
    }

    private String getClassName(String url) {
        url = url.replace('/', '.');
        if (url.contains("!")) {
            url = url.substring(url.indexOf("!") + 2);
        }
        String res = url.substring(0, url.indexOf(".class"));
        return res;
    }


    @Override
    public String toString() {
        return "OrienteerClassLoader{" +
                "parent=" + parent +
                '}';
    }
}
