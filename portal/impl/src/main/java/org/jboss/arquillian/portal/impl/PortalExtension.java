/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.arquillian.portal.impl;

import org.jboss.arquillian.container.test.impl.enricher.resource.URLResourceProvider;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.portal.impl.container.PortalAdditionalDeployments;
import org.jboss.arquillian.portal.impl.deployment.PortletXMLProcessor;
import org.jboss.arquillian.portal.impl.enricher.resource.PortalURLResourceProvider;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * @author <a href="mailto:ken@kenfinnigan.me">Ken Finnigan</a>
 */
public class PortalExtension implements LoadableExtension {

    /**
     * @see org.jboss.arquillian.core.spi.LoadableExtension#register(org.jboss.arquillian.core.spi.LoadableExtension.ExtensionBuilder)
     */
    @Override
    public void register(ExtensionBuilder builder) {
        builder.override(ResourceProvider.class, URLResourceProvider.class, PortalURLResourceProvider.class);

        builder.observer(PortletXMLProcessor.class);
        builder.observer(PortalAdditionalDeployments.class);
    }

}
