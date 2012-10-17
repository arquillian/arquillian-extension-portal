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

import java.util.List;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.portal.PortalTest;
import org.jboss.arquillian.portal.impl.PortletArchiveMetadata;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletType;

/**
 * Reads the portlet.xml contents to provide a list of portlet names in a file that is accessible
 * to the Extension when running in container.
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
        if (testClass.isAnnotationPresent(PortalTest.class)) {
            PortletDescriptor portletXml = Descriptors.importAs(PortletDescriptor.class).fromStream(
                    applicationArchive.get("WEB-INF/portlet.xml").getAsset().openStream());

            if (null != portletXml) {
                PortletArchiveMetadata metadata = new PortletArchiveMetadata();
                List<PortletType<PortletDescriptor>> portlets = portletXml.getAllPortlet();
                for (PortletType<PortletDescriptor> portlet : portlets) {
                    metadata.addPortletName(portlet.getPortletName());
                }
                portletMetadata.set(metadata);
            }
        }
    }

}
