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
package org.jboss.arquillian.portal.impl.container;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.event.container.AfterStart;
import org.jboss.arquillian.container.spi.event.container.BeforeStop;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.portal.spi.container.deployment.PortalContainerDeploymentProvider;
import org.jboss.shrinkwrap.api.Archive;

/**
 * @author <a href="mailto:ken@kenfinnigan.me">Ken Finnigan</a>
 */
public class PortalAdditionalDeployments {

    @Inject
    Instance<ServiceLoader> loader;

    Collection<Archive<?>> portalContainerDeployments = new ArrayList<Archive<?>>();

    public void deployPortal(@Observes AfterStart afterStartEvent) throws DeploymentException {
        Collection<PortalContainerDeploymentProvider> providers = loader.get().all(
            PortalContainerDeploymentProvider.class);

        for (PortalContainerDeploymentProvider provider : providers) {
            Archive<?> tmp = provider.build();
            portalContainerDeployments.add(tmp);
            afterStartEvent.getDeployableContainer().deploy(tmp);
            tmp = null;
        }
    }

    public void undeployPortal(@Observes BeforeStop beforeStopEvent) throws DeploymentException {
        for (Archive<?> archive : portalContainerDeployments) {
            beforeStopEvent.getDeployableContainer().undeploy(archive);
        }
    }
}
