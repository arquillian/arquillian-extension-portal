package org.jboss.shrinkwrap.portal.impl.deployment;

import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.test.impl.client.deployment.AnnotationDeploymentScenarioGenerator;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.portal.api.PortletArchive;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletArchiveGenerator extends AnnotationDeploymentScenarioGenerator {
    @Override
    public List<DeploymentDescription> generate(TestClass testClass) {
        List<DeploymentDescription> deployments = super.generate(testClass);
        List<DeploymentDescription> updatedDeployments = new ArrayList<DeploymentDescription>();

        for (DeploymentDescription currentDeploymentDescription : deployments) {
            if (currentDeploymentDescription.getArchive() instanceof PortletArchive) {
                DeploymentDescription newDescription = new DeploymentDescription(currentDeploymentDescription.getName(),
                        currentDeploymentDescription.getArchive().as(WebArchive.class));
                newDescription.shouldBeTestable(currentDeploymentDescription.testable())
                                .shouldBeManaged(currentDeploymentDescription.managed())
                                .setOrder(currentDeploymentDescription.getOrder())
                                .setTarget(currentDeploymentDescription.getTarget())
                                .setProtocol(currentDeploymentDescription.getProtocol())
                                .setExpectedException(currentDeploymentDescription.getExpectedException());

                updatedDeployments.add(newDescription);
            } else {
                updatedDeployments.add(currentDeploymentDescription);
            }
        }

        return updatedDeployments;
    }
}
