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

import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.portal.impl.enricher.resource.PortalURLResourceProvider;
import org.jboss.arquillian.portal.warp.portlet.client.deployment.PortletDeploymentEnricher;
import org.jboss.arquillian.portal.warp.portlet.client.resource.PortalWarpURLProvider;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.arquillian.warp.impl.client.proxy.ProxyURLProvider;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortalWarpExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        // url enhancement
        builder.override(ResourceProvider.class, PortalURLResourceProvider.class, PortalWarpURLProvider.class);
        builder.override(ResourceProvider.class, ProxyURLProvider.class, PortalWarpURLProvider.class);

        // deployment enrichment
        builder.service(ProtocolArchiveProcessor.class, PortletDeploymentEnricher.class);
    }
}
