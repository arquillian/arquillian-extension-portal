package org.jboss.arquillian.portal.warp.jsf;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.warp.spi.WarpDeploymentEnrichmentExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class WarpPortletJSFExtension implements LoadableExtension, WarpDeploymentEnrichmentExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(WarpDeploymentEnrichmentExtension.class, this.getClass());
    }

    @Override
    public void enrichWebArchive(WebArchive webArchive) {
        // Do Nothing
    }

    @Override
    public JavaArchive getEnrichmentLibrary() {
        return ShrinkWrap.create(JavaArchive.class, "arquillian-portal-warp-jsf.jar")
                .addAsManifestResource("META-INF/portal-extensions/faces-config.xml", "faces-config.xml")
                .addPackage("org.jboss.arquillian.portal.warp.jsf")
                .addAsServiceProvider(RemoteLoadableExtension.class, WarpPortletJSFRemoteExtension.class);
    }
}
