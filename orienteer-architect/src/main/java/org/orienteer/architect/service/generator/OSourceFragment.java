package org.orienteer.architect.service.generator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents Code fragment in Java Sources
 */
public class OSourceFragment implements ISource {

    private final List<ISource> sources;

    public OSourceFragment() {
        this(new LinkedList<>());
    }

    public OSourceFragment(List<ISource> sources) {
        this.sources = new LinkedList<>();
        this.sources.addAll(sources);
    }

    public OSourceFragment addSource(ISource source) {
        sources.add(source);
        return this;
    }

    public OSourceFragment addSources(List<ISource> sources) {
        this.sources.addAll(sources);
        return this;
    }

    public boolean removeSource(ISource source) {
        return sources.remove(source);
    }

    public boolean removeSources(List<ISource> sources) {
        return this.sources.removeAll(sources);
    }

    public void clearSources() {
        sources.clear();
    }

    public List<ISource> getSources() {
        return Collections.unmodifiableList(sources);
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        sources.forEach(source -> source.appendJavaSrc(sb));
    }
}
