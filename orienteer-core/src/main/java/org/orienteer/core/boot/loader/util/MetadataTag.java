package org.orienteer.core.boot.loader.util;

/**
 * @author Vitaliy Gonchar
 * Contains xml tags for work with metadata.xml
 */
enum MetadataTag {
    METADATA("metadata"),
    MODULE("module"),
    LOAD("load"),
    TRUSTED("trusted"),
    INITIALIZER("initializer"),
    GROUP_ID("groupId"),
    ARTIFACT_ID("artifactId"),
    VERSION("version"),
    DEPENDENCY("dependency"),
    JAR("jar"),
    EMPTY_TAG(" ");

    private final String tag;

    MetadataTag(String tag) {
        this.tag = tag;
    }

    String get() {
        return tag;
    }

    static MetadataTag getByName(String name) {
        for (MetadataTag tag : MetadataTag.values()) {
            if (tag.get().equals(name)) {
                return tag;
            }
        }
        return EMPTY_TAG;
    }

    @Override
    public String toString() {
        return "MetadataTag{" +
                "tag='" + tag + '\'' +
                '}';
    }
}
