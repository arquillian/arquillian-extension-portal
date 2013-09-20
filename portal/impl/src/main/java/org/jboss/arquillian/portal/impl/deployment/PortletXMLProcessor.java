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
package org.jboss.arquillian.portal.impl.deployment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.portal.impl.PortletArchiveMetadata;
import org.jboss.arquillian.portal.impl.enricher.resource.PortalURLResourceProvider;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletType;

/**
 * Reads the portlet.xml contents to provide a list of portlet names that is passed to the container implementation by
 * {@link PortalURLResourceProvider} during {@link @PortalURL} enrichment.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletXMLProcessor implements ApplicationArchiveProcessor {

    @Inject
    @ClassScoped
    InstanceProducer<PortletArchiveMetadata> portletMetadata;

    /**
     * @see org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor#process(org.jboss.shrinkwrap.api.Archive, org.jboss.arquillian.test.spi.TestClass)
     */
    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        boolean processed = false;

        for (Field field : testClass.getJavaClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PortalURL.class)) {
                processed = processPortletXml(applicationArchive);
                if (processed) {
                    break;
                }
            }
        }

        if (!processed) {
            for (Method method : testClass.getJavaClass().getDeclaredMethods()) {
                Annotation[][] methodParameterAnnotations = method.getParameterAnnotations();
                for (Annotation[] parameterAnnotations : methodParameterAnnotations) {
                    for (Annotation annotation : parameterAnnotations) {
                        if (annotation instanceof PortalURL) {
                            processed = processPortletXml(applicationArchive);
                            if (processed) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean processPortletXml(Archive<?> applicationArchive) {
        PortletDescriptor portletXml;
        try {
            portletXml = Descriptors.importAs(PortletDescriptor.class).fromStream(
                    applicationArchive.get("WEB-INF/portlet.xml").getAsset().openStream());

            if (null != portletXml) {
                PortletArchiveMetadata metadata = new PortletArchiveMetadata();
                List<PortletType<PortletDescriptor>> portlets = portletXml.getAllPortlet();
                for (PortletType<PortletDescriptor> portlet : portlets) {
                    metadata.addPortletName(portlet.getPortletName());
                }
                portletMetadata.set(metadata);
            }
            return true;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to retrieve portlet.xml from Deployment", e);
        }
    }
}
