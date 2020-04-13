package org.orienteer.logger.server.service.correlation;

import org.apache.http.util.Args;

import java.io.Serializable;
import java.util.Objects;

/**
 * Wrapper which represents log object
 */
public final class OLogObj implements Serializable {

    private final String key;
    private final Object data;

    public static OLogObj of(String key, Object data) {
        return new OLogObj(key, data);
    }

    private OLogObj(String key, Object data) {
        this.key = Args.notEmpty(key, "key");
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return Objects.toString(data);
    }
}
