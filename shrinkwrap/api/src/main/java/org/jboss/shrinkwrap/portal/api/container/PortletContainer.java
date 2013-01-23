package org.jboss.shrinkwrap.portal.api.container;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.container.WebContainer;

import java.io.File;
import java.net.URL;

/**
 * Defines the contract for a component capable of storing portlet-related resources.
 * <br/>
 * The actual path to the Portlet resources within the Archive is up to the implementations/specifications.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public interface PortletContainer<T extends Archive<T>> extends WebContainer<T> {
    /**
     * Adds the resource as portlet.xml to the container, returning the container itself. <br/>
     * The {@link ClassLoader} used to obtain the resource is up to the implementation.
     *
     * @param resourceName
     *            resource to add
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resourceName is null
     * @see #setPortletXML(org.jboss.shrinkwrap.api.asset.Asset)
     */
    T setPortletXML(String resourceName) throws IllegalArgumentException;

    /**
     * Adds the {@link java.io.File} as portlet.xml to the container, returning the container itself.
     *
     * @param resource
     *            {@link java.io.File} resource to add
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resource is null
     * @see #setPortletXML(org.jboss.shrinkwrap.api.asset.Asset)
     */
    T setPortletXML(File resource) throws IllegalArgumentException;

    /**
     * Adds the {@link java.net.URL} as portlet.xml to the container, returning the container itself.
     *
     * @param resource
     *            {@link java.net.URL} resource to add
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resource is null
     * @see #setPortletXML(org.jboss.shrinkwrap.api.asset.Asset)
     */
    T setPortletXML(URL resource) throws IllegalArgumentException;

    /**
     * Adds the {@link org.jboss.shrinkwrap.api.asset.Asset} as portlet.xml to the container, returning the container itself.
     *
     * @param resource
     *            {@link org.jboss.shrinkwrap.api.asset.Asset} resource to add
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resource is null
     * @see # addAsWebResource(Asset, ArchivePath)
     */
    T setPortletXML(Asset resource) throws IllegalArgumentException;

    /**
     * Adds the resource inside the package as portlet.xml to the container, returning the container itself. <br/>
     * <br/>
     * The {@link ClassLoader} used to obtain the resource is up to the implementation.
     *
     * @param resourcePackage
     *            The package of the resources
     * @param resourceName
     *            The name of the resources inside resourcePackage
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resourcePackage is null
     * @throws IllegalArgumentException
     *             if resourceName is null
     */
    T setPortletXML(Package resourcePackage, String resourceName) throws IllegalArgumentException;
}
