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
import org.jboss.arquillian.portal.spi.container.deployment.PortletContainerDeploymentProvider;
import org.jboss.shrinkwrap.api.Archive;

/**
 * @author <a href="mailto:ken@kenfinnigan.me">Ken Finnigan</a>
 */
public class PortalAdditionalDeployments {

    @Inject
    Instance<ServiceLoader> loader;

    Collection<Archive<?>> deployments = new ArrayList<Archive<?>>();

    public void deployPortal(@Observes AfterStart afterStartEvent) throws DeploymentException {
        Collection<PortletContainerDeploymentProvider> providers = loader.get().all(PortletContainerDeploymentProvider.class);

        for (PortletContainerDeploymentProvider provider : providers) {
            Archive<?> tmp = provider.build();
            deployments.add(tmp);
            afterStartEvent.getDeployableContainer().deploy(tmp);
            tmp = null;
        }
    }

    public void undeployPortal(@Observes BeforeStop beforeStopEvent) throws DeploymentException {
        for (Archive<?> archive : deployments) {
            beforeStopEvent.getDeployableContainer().undeploy(archive);
        }
    }
}
