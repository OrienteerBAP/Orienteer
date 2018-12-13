package org.orienteer.core.boot.loader.internal;

/**
 * Interface which indicates that component can reindex
 */
interface IReindexSupport {
    void reindex(OModulesMicroFrameworkConfig config);
}
