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

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.portal.warp.Phase;
import org.jboss.arquillian.portal.warp.portlet.server.event.AfterPortletPhase;
import org.jboss.arquillian.portal.warp.portlet.server.event.BeforePortletPhase;
import org.jboss.arquillian.portal.warp.portlet.server.event.ExecutePortalWarpActionRequest;
import org.jboss.arquillian.portal.warp.portlet.server.event.ExecutePortalWarpEventRequest;
import org.jboss.arquillian.portal.warp.portlet.server.event.ExecutePortalWarpRenderRequest;
import org.jboss.arquillian.portal.warp.portlet.server.event.ExecutePortalWarpResourceRequest;
import org.jboss.arquillian.portal.warp.portlet.server.event.PortalWarpLifecycleFinished;
import org.jboss.arquillian.portal.warp.portlet.server.event.PortalWarpLifecycleStarted;
import org.jboss.arquillian.warp.spi.LifecycleManager;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.FilterChain;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortalWarpLifecycle {

    @Inject
    private Instance<LifecycleManager> lifecycleManager;

    @Inject
    private Event<PortalWarpLifecycleStarted> portalWarpLifecycleStarted;

    @Inject
    private Event<PortalWarpLifecycleFinished> portalWarpLifecycleFinished;

    public void processPortletAction(@Observes ExecutePortalWarpActionRequest event, ActionRequest request, ActionResponse response, FilterChain filterChain)
            throws Throwable {

        try {
            lifecycleManager.get().bindTo(PortletRequest.class, request);

            portalWarpLifecycleStarted.fire(new PortalWarpLifecycleStarted());
            lifecycleManager.get().fireEvent(new BeforePortletPhase(Phase.ACTION));

            filterChain.doFilter(request, response);

            lifecycleManager.get().fireEvent(new AfterPortletPhase(Phase.ACTION));
        } finally {
            portalWarpLifecycleFinished.fire(new PortalWarpLifecycleFinished());

            lifecycleManager.get().unbindFrom(PortletRequest.class, request);
        }
    }

    public void processPortletEvent(@Observes ExecutePortalWarpEventRequest event, EventRequest request, EventResponse response, FilterChain filterChain)
            throws Throwable {

        try {
            lifecycleManager.get().bindTo(PortletRequest.class, request);

            portalWarpLifecycleStarted.fire(new PortalWarpLifecycleStarted());
            lifecycleManager.get().fireEvent(new BeforePortletPhase(Phase.EVENT));

            filterChain.doFilter(request, response);

            lifecycleManager.get().fireEvent(new AfterPortletPhase(Phase.EVENT));
        } finally {
            portalWarpLifecycleFinished.fire(new PortalWarpLifecycleFinished());

            lifecycleManager.get().unbindFrom(PortletRequest.class, request);
        }
    }

    public void processPortletRender(@Observes ExecutePortalWarpRenderRequest event, RenderRequest request, RenderResponse response, FilterChain filterChain)
            throws Throwable {

        try {
            lifecycleManager.get().bindTo(PortletRequest.class, request);

            portalWarpLifecycleStarted.fire(new PortalWarpLifecycleStarted());
            lifecycleManager.get().fireEvent(new BeforePortletPhase(Phase.RENDER));

            filterChain.doFilter(request, response);

            lifecycleManager.get().fireEvent(new AfterPortletPhase(Phase.RENDER));
        } finally {
            portalWarpLifecycleFinished.fire(new PortalWarpLifecycleFinished());

            lifecycleManager.get().unbindFrom(PortletRequest.class, request);
        }
    }

    public void processPortletResource(@Observes ExecutePortalWarpResourceRequest event, ResourceRequest request, ResourceResponse response, FilterChain filterChain)
            throws Throwable {

        try {
            lifecycleManager.get().bindTo(PortletRequest.class, request);

            portalWarpLifecycleStarted.fire(new PortalWarpLifecycleStarted());
            lifecycleManager.get().fireEvent(new BeforePortletPhase(Phase.RESOURCE));

            filterChain.doFilter(request, response);

            lifecycleManager.get().fireEvent(new AfterPortletPhase(Phase.RESOURCE));
        } finally {
            portalWarpLifecycleFinished.fire(new PortalWarpLifecycleFinished());

            lifecycleManager.get().unbindFrom(PortletRequest.class, request);
        }
    }
}
