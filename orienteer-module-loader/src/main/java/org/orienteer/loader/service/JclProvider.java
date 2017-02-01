package org.orienteer.loader.service;

import org.xeustechnologies.jcl.JarClassLoader;

/**
 * @author Vitaliy Gonchar
 */
public abstract class JclProvider {

    private static JarClassLoader jcl;

    public synchronized static JarClassLoader getJcl() {
        jcl = jcl == null ? new JarClassLoader() : jcl;
        return jcl;
    }


    public synchronized static JarClassLoader createNewJcl() {
        jcl = jcl != null ? new JarClassLoader(jcl) : new JarClassLoader();
        return jcl;
    }
}
