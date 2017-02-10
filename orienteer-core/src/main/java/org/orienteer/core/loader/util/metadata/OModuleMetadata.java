package org.orienteer.core.loader.util.metadata;

import org.orienteer.core.loader.ODependency;

/**
 * @author Vitaliy Gonchar
 */
public class OModuleMetadata {
    private String initializerName;
    private ODependency dependency;
    private boolean trusted;
    private boolean load;

    private int id;

    public OModuleMetadata(String initializerName, ODependency dependency, boolean trusted, boolean load, int id) {
        this.initializerName = initializerName;
        this.dependency = dependency;
        this.trusted = trusted;
        this.load = load;
        this.id = id;
    }

    public OModuleMetadata() {}


    public void setInitializerName(String initializerName) {
        this.initializerName = initializerName;
    }

    public void setDependency(ODependency dependency) {
        this.dependency = dependency;
    }

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public String getInitializerName() {
        return initializerName;
    }

    public ODependency getDependency() {
        return dependency;
    }

    public boolean isTrusted() {
        return trusted;
    }

    public boolean isLoad() {
        return load;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OModuleMetadata metadata = (OModuleMetadata) o;

        if (trusted != metadata.trusted) return false;
        if (load != metadata.load) return false;
        if (id != metadata.id) return false;
        if (initializerName != null ? !initializerName.equals(metadata.initializerName) : metadata.initializerName != null)
            return false;
        return dependency != null ? dependency.equals(metadata.dependency) : metadata.dependency == null;
    }

    @Override
    public int hashCode() {
        int result = initializerName != null ? initializerName.hashCode() : 0;
        result = 31 * result + (dependency != null ? dependency.hashCode() : 0);
        result = 31 * result + (trusted ? 1 : 0);
        result = 31 * result + (load ? 1 : 0);
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "OModuleMetadata{" +
                "initializerName='" + initializerName + '\'' +
                ", dependency=" + dependency +
                ", trusted=" + trusted +
                ", load=" + load +
                ", id=" + id +
                '}';
    }
}
