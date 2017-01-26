package org.orienteer.loader.service;

import org.apache.wicket.protocol.http.WicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Vitaliy Gonchar
 */
public class ReloadInfoFilter extends WicketFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ReloadInfoFilter.class);

    private boolean isOn;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        if (isOn) {
            res.setStatus(503);
            LOG.debug("Reload application. Send 503 code");
        } else super.doFilter(request, response, chain);
    }


    public void on() {
        isOn = true;
    }

    public void off() {
        isOn = false;
    }
}
