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

import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.test.impl.client.deployment.AnnotationDeploymentScenarioGenerator;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Augment the portlet web archive to include the necessary pieces for Pluto Container.
 * 
 * @author kenfinnigan
 */
public class PlutoDeploymentGenerator extends AnnotationDeploymentScenarioGenerator {

    @Override
    public List<DeploymentDescription> generate(TestClass testClass) {
        List<DeploymentDescription> deployments = super.generate(testClass);

        for (DeploymentDescription deployment : deployments) {
            Archive<?> archive = deployment.getArchive();
            if (WebArchive.class.isInstance(archive)) {
                WebArchive webArchive = (WebArchive) archive;

                JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class)
                        .addAsManifestResource("META-INF/pluto.tld", "pluto.tld")
                        .addAsManifestResource("META-INF/web-fragment.xml", "web-fragment.xml");

                webArchive.addAsLibrary(javaArchive);

                webArchive.addAsWebInfResource("themes/pluto.jsp", "themes/pluto.jsp")
                        .addAsWebInfResource("themes/portlet-skin.jsp", "themes/portlet-skin.jsp")
                        .addAsWebInfResource("META-INF/pluto.tld", "tld/pluto.tld")
                        .addAsWebInfResource("pluto-portal-driver-config.xml");
            }
        }
        return deployments;
    }

}
