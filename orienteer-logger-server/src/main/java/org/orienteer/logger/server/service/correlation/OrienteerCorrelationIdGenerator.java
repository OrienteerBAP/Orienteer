package org.orienteer.logger.server.service.correlation;

import org.orienteer.logger.impl.DefaultCorrelationIdGenerator;

import java.security.MessageDigest;

/**
 * Orienteer implementation of {@link org.orienteer.logger.IOCorrelationIdGenerator}
 */
public class OrienteerCorrelationIdGenerator extends DefaultCorrelationIdGenerator {

    @Override
    protected void generateDigestFromObject(MessageDigest md, Object object) {
        if (object instanceof String) {
            md.update(object.getClass().getName().getBytes());
        } else if (object instanceof OLogObj) {
            OLogObj obj = (OLogObj) object;
            md.update(obj.getClass().getName().getBytes());
            md.update(obj.getKey().getBytes());
        } else {
            super.generateDigestFromObject(md, object);
        }
    }
}
