/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.portal.warp.portlet.server.execution;

import org.jboss.arquillian.core.spi.Manager;
import org.jboss.arquillian.portal.warp.portlet.server.event.ExecutePortalWarpActionRequest;
import org.jboss.arquillian.portal.warp.portlet.server.event.ExecutePortalWarpEventRequest;
import org.jboss.arquillian.portal.warp.portlet.server.event.ExecutePortalWarpRenderRequest;
import org.jboss.arquillian.portal.warp.portlet.server.event.ExecutePortalWarpResourceRequest;
import org.jboss.arquillian.warp.impl.server.execution.WarpFilter;
import org.jboss.arquillian.warp.spi.context.RequestScoped;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;
import java.io.IOException;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortalWarpFilter implements ActionFilter, EventFilter, RenderFilter, ResourceFilter {

    private Manager manager;

    @Override
    public void init(FilterConfig filterConfig) throws PortletException {
    }

    @Override
    public void destroy() {
        manager = null;
    }

    @Override
    public void doFilter(ActionRequest request, ActionResponse response, FilterChain chain) throws IOException, PortletException {
        getManager(request);

        if (manager != null) {
            manager.bind(RequestScoped.class, ActionRequest.class, request);
            manager.bind(RequestScoped.class, ActionResponse.class, response);

            doCommonBinding(request, response, chain);

            manager.fire(new ExecutePortalWarpActionRequest());
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void doFilter(EventRequest request, EventResponse response, FilterChain chain) throws IOException, PortletException {
        getManager(request);

        if (manager != null) {
            manager.bind(RequestScoped.class, EventRequest.class, request);
            manager.bind(RequestScoped.class, EventResponse.class, response);

            doCommonBinding(request, response, chain);

            manager.fire(new ExecutePortalWarpEventRequest());
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException, PortletException {
        getManager(request);

        if (manager != null) {
            manager.bind(RequestScoped.class, RenderRequest.class, request);
            manager.bind(RequestScoped.class, RenderResponse.class, response);

            doCommonBinding(request, response, chain);

            manager.fire(new ExecutePortalWarpRenderRequest());
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void doFilter(ResourceRequest request, ResourceResponse response, FilterChain chain) throws IOException, PortletException {
        getManager(request);

        if (manager != null) {
            manager.bind(RequestScoped.class, ResourceRequest.class, request);
            manager.bind(RequestScoped.class, ResourceResponse.class, response);

            doCommonBinding(request, response, chain);

            manager.fire(new ExecutePortalWarpResourceRequest());
        } else {
            chain.doFilter(request, response);
        }
    }

    private void doCommonBinding(PortletRequest request, PortletResponse response, FilterChain chain) throws IOException, PortletModeException {
        manager.bind(RequestScoped.class, PortletRequest.class, request);
        manager.bind(RequestScoped.class, PortletResponse.class, response);
        manager.bind(RequestScoped.class, FilterChain.class, chain);
    }

    private void getManager(PortletRequest request) {
        if (manager == null) {
            manager = (Manager) request.getAttribute(WarpFilter.ARQUILLIAN_MANAGER_ATTRIBUTE);
        }
    }
}
