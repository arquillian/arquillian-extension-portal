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
package org.jboss.arquillian.portal.warp.portlet.client.deployment;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.portal.warp.AfterPortletPhase;
import org.jboss.arquillian.portal.warp.BeforePortletPhase;
import org.jboss.arquillian.portal.warp.Phase;
import org.jboss.arquillian.portal.warp.portlet.PortalWarpRemoteExtension;
import org.jboss.arquillian.portal.warp.portlet.PortalWarpCommons;
import org.jboss.arquillian.portal.warp.portlet.server.execution.PortalWarpFilter;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletDeploymentEnricher implements ProtocolArchiveProcessor {

    static String[] REQUIRED_PORTAL_WARP_PACKAGES = new String[] {
            // Implementation
            "org.jboss.arquillian.portal.warp.portlet.server.event",
            "org.jboss.arquillian.portal.warp.portlet.server.execution",
            "org.jboss.arquillian.portal.warp.portlet.provider"
    };

    @Inject
    private Instance<TestClass> testClass;

    @Override
    public void process(TestDeployment testDeployment, Archive<?> protocolArchive) {
        final TestClass testClass = this.testClass.get();
        final Archive<?> applicationArchive = testDeployment.getApplicationArchive();

        if (PortalWarpCommons.isPortalTest(testClass.getJavaClass())) {
            addPortalWarpPackageToProtocol(protocolArchive.as(WebArchive.class));

            addPortalWarpFilterToDeployment(applicationArchive);
        }
    }

    private void addPortalWarpFilterToDeployment(Archive<?> applicationArchive) {
        if (applicationArchive instanceof WebArchive) {
            WebArchive webArchive = (WebArchive) applicationArchive;

            PortletDescriptor portletXml;
            try {
                portletXml = Descriptors.importAs(PortletDescriptor.class).fromStream(
                        applicationArchive.get("WEB-INF/portlet.xml").getAsset().openStream());

                if (null != portletXml) {
                    webArchive.addClass(PortalWarpFilter.class);

                    portletXml.createFilter()
                                .filterName("PortalWarpFilter")
                                .filterClass(PortalWarpFilter.class.getName())
                                .lifecycle("ACTION_PHASE", "EVENT_PHASE", "RENDER_PHASE", "RESOURCE_PHASE")
                                .up()
                            .createFilterMapping()
                                .filterName("PortalWarpFilter")
                                .portletName("*")
                                .up();

                    webArchive.addAsWebInfResource(new StringAsset(portletXml.exportAsString()), "portlet.xml");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to retrieve portlet.xml from Deployment", e);
            }
        }
    }

    private void addPortalWarpPackageToProtocol(WebArchive archive) {
        archive.addAsLibrary(createPortalWarpArchive());
    }

    private JavaArchive createPortalWarpArchive() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "arquillian-portal-warp.jar");

        // API
        archive.addClass(Phase.class);
        archive.addClasses(BeforePortletPhase.class, AfterPortletPhase.class);

        for (String packageName : REQUIRED_PORTAL_WARP_PACKAGES) {
            archive.addPackage(packageName);
        }

        // register remote extension
        archive.addClass(PortalWarpRemoteExtension.class);
        archive.addAsServiceProvider(RemoteLoadableExtension.class.getName(), PortalWarpRemoteExtension.class.getName(),"!org.jboss.arquillian.protocol.servlet.runner.ServletRemoteExtension");

        return archive;
    }
}
