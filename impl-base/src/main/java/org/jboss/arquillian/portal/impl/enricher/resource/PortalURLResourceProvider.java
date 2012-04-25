package org.jboss.arquillian.portal.impl.enricher.resource;

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

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;

import org.jboss.arquillian.container.test.impl.enricher.resource.URLResourceProvider;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.portal.spi.enricher.resource.PortalURLProvider;
import org.jboss.arquillian.test.api.ArquillianResource;

/**
 * @author <a href="mailto:ken@kenfinnigan.me">Ken Finnigan</a>
 */
public class PortalURLResourceProvider extends URLResourceProvider {

    @Inject
    Instance<ServiceLoader> loader;

    @Override
    public Object doLookup(ArquillianResource resource, Annotation... qualifiers) {
        boolean found = false;
        for (Annotation annotation : qualifiers) {
            if (annotation.annotationType() == PortalURL.class) {
                found = true;
                break;
            }
        }

        if (!found) {
            return super.doLookup(resource, qualifiers);
        }

        return locateURL(resource, qualifiers);
    }

    private Object locateURL(ArquillianResource resource, Annotation[] qualifiers) {
        return toURL((URL) super.doLookup(resource, qualifiers));
    }

    private URL toURL(URL original) {
        Collection<PortalURLProvider> providers = loader.get().all(PortalURLProvider.class);
        for (PortalURLProvider provider : providers) {
            if (provider.customizes()) {
                try {
                    return provider.customizeURL(original);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create portal specific url based on " + original
                        + " from provider: " + provider, e);
                }
            }
        }
        return original;
    }
}
