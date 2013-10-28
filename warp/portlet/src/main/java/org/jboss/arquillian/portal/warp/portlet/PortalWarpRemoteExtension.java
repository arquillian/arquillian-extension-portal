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
package org.jboss.arquillian.portal.warp.portlet;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.portal.warp.portlet.provider.ActionRequestProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.ActionResponseProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.EventRequestProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.EventResponseProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.PortletRequestProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.PortletResponseProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.RenderRequestProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.RenderResponseProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.ResourceRequestProvider;
import org.jboss.arquillian.portal.warp.portlet.provider.ResourceResponseProvider;
import org.jboss.arquillian.portal.warp.portlet.server.execution.PortalWarpLifecycle;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortalWarpRemoteExtension implements RemoteLoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        // Providers for Request/Response objects
        builder.service(ResourceProvider.class, PortletRequestProvider.class);
        builder.service(ResourceProvider.class, PortletResponseProvider.class);

        builder.service(ResourceProvider.class, ActionRequestProvider.class);
        builder.service(ResourceProvider.class, ActionResponseProvider.class);

        builder.service(ResourceProvider.class, EventRequestProvider.class);
        builder.service(ResourceProvider.class, EventResponseProvider.class);

        builder.service(ResourceProvider.class, RenderRequestProvider.class);
        builder.service(ResourceProvider.class, RenderResponseProvider.class);

        builder.service(ResourceProvider.class, ResourceRequestProvider.class);
        builder.service(ResourceProvider.class, ResourceResponseProvider.class);

        // Observers
        builder.observer(PortalWarpLifecycle.class);
    }
}
