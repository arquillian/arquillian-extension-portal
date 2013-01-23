package org.jboss.shrinkwrap.portal.impl;

import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.shrinkwrap.portal.impl.deployment.PortletArchiveGenerator;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class ShrinkwrapPortalExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(DeploymentScenarioGenerator.class, PortletArchiveGenerator.class);
    }
}
