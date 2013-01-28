package org.jboss.shrinkwrap.portal.impl;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.jboss.shrinkwrap.portal.api.PortletMode;
import org.jboss.shrinkwrap.portal.impl.container.PortletContainerBase;

import javax.portlet.Portlet;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletArchiveImpl extends PortletContainerBase<PortletArchive> implements PortletArchive {

    /**
     * Path to the web inside of the Archive.
     */
    private static final ArchivePath PATH_WEB = ArchivePaths.root();

    /**
     * Path to the WEB-INF inside of the Archive.
     */
    private static final ArchivePath PATH_WEB_INF = ArchivePaths.create("WEB-INF");

    /**
     * Path to the resources inside of the Archive.
     */
    private static final ArchivePath PATH_RESOURCE = ArchivePaths.create(PATH_WEB_INF, "classes");

    /**
     * Path to the libraries inside of the Archive.
     */
    private static final ArchivePath PATH_LIBRARY = ArchivePaths.create(PATH_WEB_INF, "lib");

    /**
     * Path to the classes inside of the Archive.
     */
    private static final ArchivePath PATH_CLASSES = ArchivePaths.create(PATH_WEB_INF, "classes");

    /**
     * Path to the manifests inside of the Archive.
     */
    private static final ArchivePath PATH_MANIFEST = ArchivePaths.create("META-INF");

    /**
     * Path to web archive service providers.
     */
    private static final ArchivePath PATH_SERVICE_PROVIDERS = ArchivePaths.create(PATH_CLASSES, "META-INF/services");

    private static final ArchivePath PATH_PORTLET_DESCRIPTOR = ArchivePaths.create(PATH_WEB_INF, "portlet.xml");

    private static final String GENERIC_FACES_PORTLET_CLASS = "javax.portlet.faces.GenericFacesPortlet";
    private static final String DEFAULT_VIEW_ID_PARAM_NAME = "javax.portlet.faces.defaultViewId.view";
    private static final String DEFAULT_EDIT_ID_PARAM_NAME = "javax.portlet.faces.defaultViewId.edit";
    private static final String DEFAULT_HELP_ID_PARAM_NAME = "javax.portlet.faces.defaultViewId.help";

    /**
     * Create a new PortletArchive with any type storage engine as backing.
     *
     * @param delegate
     *            The storage backing.
     */
    public PortletArchiveImpl(final Archive<?> delegate) {
        super(PortletArchive.class, delegate);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getManifestPath()
     */
    @Override
    protected ArchivePath getManifestPath() {
        return PATH_MANIFEST;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getClassesPath()
     */
    @Override
    protected ArchivePath getClassesPath() {
        return PATH_CLASSES;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getResourcePath()
     */
    @Override
    protected ArchivePath getResourcePath() {
        return PATH_RESOURCE;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getLibraryPath()
     */
    @Override
    protected ArchivePath getLibraryPath() {
        return PATH_LIBRARY;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.WebContainerBase#getWebPath()
     */
    @Override
    protected ArchivePath getWebPath() {
        return PATH_WEB;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.WebContainerBase#getWebInfPath()
     */
    @Override
    protected ArchivePath getWebInfPath() {
        return PATH_WEB_INF;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.WebContainerBase#getWebInfPath()
     */
    @Override
    protected ArchivePath getServiceProvidersPath() {
        return PATH_SERVICE_PROVIDERS;
    }

    @Override
    public PortletArchive createSimplePortlet(Class<? extends Portlet> portletClass) {
        String name = portletClass.getSimpleName();

        PortletDescriptor desc = getOrCreatePortletDescriptor()
                .createPortlet()
                    .portletName(name)
                    .portletClass(portletClass.getName())
                    .createSupports()
                        .mimeType("text/html")
                        .portletMode(PortletMode.VIEW.toString())
                        .portletMode(PortletMode.EDIT.toString())
                        .portletMode(PortletMode.HELP.toString())
                        .up()
                    .getOrCreatePortletInfo()
                        .title(name)
                        .up()
                    .up();

        this.setPortletXML(new StringAsset(desc.exportAsString()));
        return covarientReturn();
    }

    @Override
    public PortletArchive createSimplePortlet(Class<? extends Portlet> portletClass, String name, String title) {
        PortletDescriptor desc = getOrCreatePortletDescriptor()
                .createPortlet()
                    .portletName(name)
                    .portletClass(portletClass.getName())
                    .createSupports()
                        .mimeType("text/html")
                        .portletMode(PortletMode.VIEW.toString())
                        .portletMode(PortletMode.EDIT.toString())
                        .portletMode(PortletMode.HELP.toString())
                        .up()
                    .getOrCreatePortletInfo()
                        .title(title)
                        .up()
                    .up();

        this.setPortletXML(new StringAsset(desc.exportAsString()));
        return covarientReturn();
    }

    @Override
    public PortletArchive createSimplePortlet(Class<? extends Portlet> portletClass, String name, String title, String mimeType, PortletMode... modes) {
        PortletDescriptor desc = getOrCreatePortletDescriptor()
                .createPortlet()
                    .portletName(name)
                    .portletClass(portletClass.getName())
                    .createSupports()
                        .mimeType(mimeType)
                        .portletMode(PortletMode.valuesAsString(modes))
                        .up()
                    .getOrCreatePortletInfo()
                        .title(title)
                        .up()
                    .up();

        this.setPortletXML(new StringAsset(desc.exportAsString()));
        return covarientReturn();
    }

    @Override
    public PortletArchive createFacesPortlet(String name) {
        PortletDescriptor desc = getOrCreatePortletDescriptor()
                .createPortlet()
                    .portletName(name)
                    .portletClass(GENERIC_FACES_PORTLET_CLASS)
                    .createInitParam()
                        .name(DEFAULT_VIEW_ID_PARAM_NAME)
                        .value("/index.xhtml")
                        .up()
                    .createSupports()
                        .mimeType("text/html")
                        .portletMode(PortletMode.VIEW.toString())
                        .up()
                    .getOrCreatePortletInfo()
                        .title(name)
                        .up()
                    .up();

        this.setPortletXML(new StringAsset(desc.exportAsString()));
        return covarientReturn();
    }

    @Override
    public PortletArchive createFacesPortlet(String name, String title) {
        PortletDescriptor desc = getOrCreatePortletDescriptor()
                .createPortlet()
                .portletName(name)
                .portletClass(GENERIC_FACES_PORTLET_CLASS)
                .createInitParam()
                .name(DEFAULT_VIEW_ID_PARAM_NAME)
                .value("/index.xhtml")
                .up()
                .createSupports()
                .mimeType("text/html")
                .portletMode(PortletMode.VIEW.toString())
                .up()
                .getOrCreatePortletInfo()
                .title(title)
                .up()
                .up();

        this.setPortletXML(new StringAsset(desc.exportAsString()));
        return covarientReturn();
    }

    @Override
    public PortletArchive createFacesPortlet(String name, String title, String viewModeViewId) {
        viewModeViewId = checkForLeadingSlash(viewModeViewId);

        PortletDescriptor desc = getOrCreatePortletDescriptor()
                .createPortlet()
                    .portletName(name)
                    .portletClass(GENERIC_FACES_PORTLET_CLASS)
                    .createInitParam()
                        .name(DEFAULT_VIEW_ID_PARAM_NAME)
                        .value(viewModeViewId)
                        .up()
                    .createSupports()
                        .mimeType("text/html")
                        .portletMode(PortletMode.VIEW.toString())
                        .up()
                    .getOrCreatePortletInfo()
                        .title(title)
                        .up()
                    .up();

        this.setPortletXML(new StringAsset(desc.exportAsString()));
        return covarientReturn();
    }

    @Override
    public PortletArchive createFacesPortlet(String name, String title, String viewModeViewId, String editModeViewId) {
        viewModeViewId = checkForLeadingSlash(viewModeViewId);
        editModeViewId = checkForLeadingSlash(editModeViewId);

        PortletDescriptor desc = getOrCreatePortletDescriptor()
                .createPortlet()
                    .portletName(name)
                    .portletClass(GENERIC_FACES_PORTLET_CLASS)
                    .createInitParam()
                        .name(DEFAULT_VIEW_ID_PARAM_NAME)
                        .value(viewModeViewId)
                        .up()
                    .createInitParam()
                        .name(DEFAULT_EDIT_ID_PARAM_NAME)
                        .value(editModeViewId)
                        .up()
                    .createSupports()
                        .mimeType("text/html")
                        .portletMode(PortletMode.VIEW.toString())
                        .portletMode(PortletMode.EDIT.toString())
                        .up()
                    .getOrCreatePortletInfo()
                        .title(title)
                        .up()
                    .up();

        this.setPortletXML(new StringAsset(desc.exportAsString()));
        return covarientReturn();
    }

    @Override
    public PortletArchive createFacesPortlet(String name, String title, String viewModeViewId, String editModeViewId, String helpModeViewId) {
        viewModeViewId = checkForLeadingSlash(viewModeViewId);
        editModeViewId = checkForLeadingSlash(editModeViewId);
        helpModeViewId = checkForLeadingSlash(helpModeViewId);

        PortletDescriptor desc = getOrCreatePortletDescriptor()
                .createPortlet()
                    .portletName(name)
                    .portletClass(GENERIC_FACES_PORTLET_CLASS)
                    .createInitParam()
                        .name(DEFAULT_VIEW_ID_PARAM_NAME)
                        .value(viewModeViewId)
                        .up()
                    .createInitParam()
                        .name(DEFAULT_EDIT_ID_PARAM_NAME)
                        .value(editModeViewId)
                        .up()
                    .createInitParam()
                        .name(DEFAULT_HELP_ID_PARAM_NAME)
                        .value(helpModeViewId)
                        .up()
                    .createSupports()
                        .mimeType("text/html")
                        .portletMode(PortletMode.VIEW.toString())
                        .portletMode(PortletMode.EDIT.toString())
                        .portletMode(PortletMode.HELP.toString())
                        .up()
                    .getOrCreatePortletInfo()
                        .title(title)
                        .up()
                    .up();

        this.setPortletXML(new StringAsset(desc.exportAsString()));
        return covarientReturn();
    }

    private String checkForLeadingSlash(String viewId) {
        return viewId.indexOf("/") != 0 ? "/" + viewId : viewId;
    }

    private PortletDescriptor getOrCreatePortletDescriptor() {
        PortletDescriptor descriptor;

        Node portletXmlNode = this.get(PATH_PORTLET_DESCRIPTOR);
        if (null != portletXmlNode) {
            descriptor = Descriptors.importAs(PortletDescriptor.class).fromStream(portletXmlNode.getAsset().openStream());
            this.delete(PATH_PORTLET_DESCRIPTOR);
        } else {
            descriptor = Descriptors.create(PortletDescriptor.class)
                                .addDefaultNamespaces()
                                .version("2.0");
        }
        return descriptor;
    }

}
