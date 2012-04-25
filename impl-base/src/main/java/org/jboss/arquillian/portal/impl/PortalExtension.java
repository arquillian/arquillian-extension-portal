/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.arquillian.portal.impl;

import org.jboss.arquillian.container.test.impl.enricher.resource.URLResourceProvider;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.portal.impl.container.PortalAdditionalDeployments;
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

        builder.observer(PortalAdditionalDeployments.class);
    }

}
