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
package org.jboss.arquillian.portal.impl.enricher.resource;

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
            try {
                return provider.customizeURL(original);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create portal specific url based on " + original
                    + " from provider: " + provider, e);
            }
        }
        return original;
    }
}
