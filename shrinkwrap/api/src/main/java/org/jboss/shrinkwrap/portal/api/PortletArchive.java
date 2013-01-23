package org.jboss.shrinkwrap.portal.api;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.container.WebContainer;
import org.jboss.shrinkwrap.portal.api.container.PortletContainer;

/**
 * Traditional WAR (Java Web Archive) structure. Used in construction of portlet applications.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public interface PortletArchive extends Archive<PortletArchive>, LibraryContainer<PortletArchive>, WebContainer<PortletArchive>,
        PortletContainer<PortletArchive>, ResourceContainer<PortletArchive>, ServiceProviderContainer<PortletArchive> {

    PortletArchive createSimplePortlet(Class<?> portletClass);

    PortletArchive createSimplePortlet(Class<?> portletClass, String name, String title);

    PortletArchive createSimplePortlet(Class<?> portletClass, String name, String title, String mimeType, PortletMode... modes);

    PortletArchive createFacesPortlet(String name);

    PortletArchive createFacesPortlet(String name, String title);

    PortletArchive createFacesPortlet(String name, String title, String viewModeViewId);

    PortletArchive createFacesPortlet(String name, String title, String viewModeViewId, String editModeViewId);

    PortletArchive createFacesPortlet(String name, String title, String viewModeViewId, String editModeViewId, String helpModeViewId);
}
