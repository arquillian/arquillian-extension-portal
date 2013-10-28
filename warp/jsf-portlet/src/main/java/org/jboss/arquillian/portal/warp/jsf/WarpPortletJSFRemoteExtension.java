package org.jboss.arquillian.portal.warp.jsf;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class WarpPortletJSFRemoteExtension implements RemoteLoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(PhaseLifecycleObserver.class);
    }
}
