package org.jboss.shrinkwrap.portal.impl.container;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.container.WebContainerBase;
import org.jboss.shrinkwrap.portal.api.container.PortletContainer;

import java.io.File;
import java.net.URL;

/**
 * PortletContainerBase
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class PortletContainerBase <T extends Archive<T>> extends WebContainerBase<T> implements PortletContainer<T> {

    protected PortletContainerBase(Class<T> actualType, Archive<?> archive) {
        super(actualType, archive);
    }

    /**
     * @see org.jboss.shrinkwrap.portal.api.container.PortletContainer#setPortletXML(String)
     */
    @Override
    public T setPortletXML(String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName should be specified");
        return setPortletXML(new ClassLoaderAsset(resourceName));
    }

    /**
     * @see org.jboss.shrinkwrap.portal.api.container.PortletContainer#setPortletXML(java.io.File)
     */
    @Override
    public T setPortletXML(File resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        return setPortletXML(new FileAsset(resource));
    }

    /**
     * @see org.jboss.shrinkwrap.portal.api.container.PortletContainer#setPortletXML(java.net.URL)
     */
    @Override
    public T setPortletXML(URL resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        return setPortletXML(new UrlAsset(resource));
    }

    /**
     * @see org.jboss.shrinkwrap.portal.api.container.PortletContainer#setPortletXML(org.jboss.shrinkwrap.api.asset.Asset)
     */
    @Override
    public T setPortletXML(Asset resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        return addAsWebInfResource(resource, "portlet.xml");
    }

    /**
     * @see org.jboss.shrinkwrap.portal.api.container.PortletContainer#setPortletXML(Package, String)
     */
    @Override
    public T setPortletXML(Package resourcePackage, String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        return setPortletXML(new ClassLoaderAsset(classloaderResourceName));
    }
}
