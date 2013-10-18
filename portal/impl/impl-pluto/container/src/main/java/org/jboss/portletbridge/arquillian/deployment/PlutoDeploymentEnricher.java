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
package org.jboss.portletbridge.arquillian.deployment;

import java.util.List;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletType;
import org.jboss.shrinkwrap.descriptor.api.webfragment30.WebFragmentDescriptor;

/**
 * Augment the portlet web archive to include the necessary pieces for Pluto Container.
 * 
 * @author kenfinnigan
 */
public class PlutoDeploymentEnricher implements ApplicationArchiveProcessor {

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        if (testClass.isAnnotationPresent(PortalTest.class)) {
            if (applicationArchive instanceof WebArchive) {
                WebArchive webArchive = (WebArchive) applicationArchive;

                // Add PortalServlet for each portlet to web-fragment.xml
                PortletDescriptor portletXml;
                WebFragmentDescriptor webFragmentXml;
                try {
                    portletXml = Descriptors.importAs(PortletDescriptor.class).fromStream(
                            applicationArchive.get("WEB-INF/portlet.xml").getAsset().openStream());

                    webFragmentXml = Descriptors.importAs(WebFragmentDescriptor.class).fromStream(
                            getClass().getClassLoader().getResourceAsStream("META-INF/web-fragment.xml"));

                    if (null != portletXml && null != webFragmentXml) {
                        List<PortletType<PortletDescriptor>> portlets = portletXml.getAllPortlet();
                        for (PortletType<PortletDescriptor> portlet : portlets) {
                            addPortletFragment(webFragmentXml, portlet.getPortletName());
                        }
                    }

                    // Add Pluto required config
                    JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class)
                            .addAsManifestResource(new StringAsset(webFragmentXml.exportAsString()), "web-fragment.xml");

                    webArchive.addAsLibrary(javaArchive);

                    webArchive.addAsWebInfResource("themes/pluto.jsp", "themes/pluto.jsp")
                            .addAsWebInfResource("themes/portlet-skin.jsp", "themes/portlet-skin.jsp")
                            .addAsWebInfResource("META-INF/pluto.tld", "tld/pluto.tld")
                            .addAsWebInfResource("pluto-portal-driver-config.xml");
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to add Pluto configuration to Deployment", e);
                }
            }
        }
    }

    private void addPortletFragment(WebFragmentDescriptor webFragmentXml, String portlet) {
        webFragmentXml.createServlet()
                          .servletName(portlet)
                          .servletClass("org.apache.pluto.container.driver.PortletServlet")
                          .createInitParam()
                              .paramName("portlet-name")
                              .paramValue(portlet)
                              .up()
                          .loadOnStartup(1)
                          .up()
                      .createServletMapping()
                          .servletName(portlet)
                          .urlPattern("/PlutoInvoker/" + portlet)
                          .up();
    }

}
