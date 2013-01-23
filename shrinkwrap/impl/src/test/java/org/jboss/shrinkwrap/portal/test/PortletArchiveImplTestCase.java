package org.jboss.shrinkwrap.portal.test;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.InitParamType;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletType;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.SupportsType;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.jboss.shrinkwrap.portal.api.PortletMode;
import org.jboss.shrinkwrap.spi.ArchiveFormatAssociable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;

import javax.portlet.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(JUnit4.class)
public class PortletArchiveImplTestCase {

    private static final ArchivePath PORTLET_XML_PATH = ArchivePaths.create("WEB-INF/portlet.xml");

    private static final String GENERIC_PORTLET_CLASS = "javax.portlet.GenericPortlet";
    private static final String GENERIC_PORTLET_NAME = "GenericPortlet";

    private static final String GENERIC_FACES_PORTLET_CLASS = "javax.portlet.faces.GenericFacesPortlet";
    private static final String DEFAULT_VIEW_PARAM_NAME = "javax.portlet.faces.defaultViewId.view";
    private static final String DEFAULT_EDIT_PARAM_NAME = "javax.portlet.faces.defaultViewId.edit";
    private static final String DEFAULT_HELP_PARAM_NAME = "javax.portlet.faces.defaultViewId.help";

    private PortletArchive archive;

    @Before
    public void createPortletArchive() {
        archive = ShrinkWrap.create(PortletArchive.class);
    }

    @Test
    public void testDefaultArchiveFormatIsSet() throws Exception {
        Assert.assertEquals("Unexpected default archive format", ArchiveFormat.ZIP, ((ArchiveFormatAssociable)archive).getArchiveFormat());
    }

    @Test
    public void testSimplePortlet() throws Exception {
        archive.createSimplePortlet(GenericPortlet.class);

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", GENERIC_PORTLET_CLASS, genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", GENERIC_PORTLET_NAME, genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", GENERIC_PORTLET_NAME, genericPortlet.getOrCreatePortletInfo().getTitle());
    }

    @Test
    public void testSimplePortletWithCustom() throws Exception {
        archive.createSimplePortlet(DummyPortlet.class);

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", "org.jboss.shrinkwrap.portal.test.PortletArchiveImplTestCase$DummyPortlet", genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", "DummyPortlet", genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", "DummyPortlet", genericPortlet.getOrCreatePortletInfo().getTitle());
    }

    @Test
    public void testSimplePortletWithNameAndTitle() throws Exception {
        String name = "MyPortlet";
        String title = "My Portlet Title";

        archive.createSimplePortlet(GenericPortlet.class, name, title);

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", GENERIC_PORTLET_CLASS, genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", name, genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", title, genericPortlet.getOrCreatePortletInfo().getTitle());
    }

    @Test
    public void testSimplePortletWithNameTitleAndMode() throws Exception {
        String name = "MyPortlet";
        String title = "My Portlet Title";

        archive.createSimplePortlet(GenericPortlet.class, name, title, "text/html", PortletMode.VIEW);

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", GENERIC_PORTLET_CLASS, genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", name, genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", title, genericPortlet.getOrCreatePortletInfo().getTitle());

        SupportsType supports = ((SupportsType)genericPortlet.getAllSupports().get(0));
        Assert.assertEquals("Portlet mime type set", "text/html", supports.getMimeType());
        Assert.assertEquals("Should be only one Portlet Mode", 1, supports.getAllPortletMode().size());
        Assert.assertEquals("Portlet mode set", PortletMode.VIEW.toString(), supports.getAllPortletMode().get(0));
    }

    @Test
    public void testSimplePortletWithNameTitleAndModes() throws Exception {
        String name = "MyPortlet";
        String title = "My Portlet Title";

        archive.createSimplePortlet(GenericPortlet.class, name, title, "text/html", PortletMode.VIEW, PortletMode.EDIT);

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", GENERIC_PORTLET_CLASS, genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", name, genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", title, genericPortlet.getOrCreatePortletInfo().getTitle());

        SupportsType supports = ((SupportsType)genericPortlet.getAllSupports().get(0));
        Assert.assertEquals("Portlet mime type set", "text/html", supports.getMimeType());
        Assert.assertEquals("Should be two Portlet Modes", 2, supports.getAllPortletMode().size());
        Assert.assertEquals("Portlet mode set", PortletMode.VIEW.toString(), supports.getAllPortletMode().get(0));
        Assert.assertEquals("Portlet mode set", PortletMode.EDIT.toString(), supports.getAllPortletMode().get(1));
    }

    @Test
    public void testSimplePortletAgainstXmlFile() throws Exception {
        archive.createSimplePortlet(JSPHelloUserPortlet.class, "JSPHelloUserPortlet", "JSP Hello User Portlet", "text/html", PortletMode.values());

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        String generatedOutput = portletDescriptor.exportAsString();
        String expectedOutput = getResourceContents("src/test/resources/genericPortlet.xml");
        assertIdenticalXml(expectedOutput, generatedOutput);
    }

    @Test
    public void testFacesPortlet() throws Exception {
        archive.createFacesPortlet("JSFPortlet");

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", GENERIC_FACES_PORTLET_CLASS, genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", "JSFPortlet", genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", "JSFPortlet", genericPortlet.getOrCreatePortletInfo().getTitle());

        List<InitParamType> allInitParams = genericPortlet.getAllInitParam();
        Assert.assertEquals("Should be one Init Param", 1, allInitParams.size());
        InitParamType param = allInitParams.get(0);
        Assert.assertEquals("Param name set", DEFAULT_VIEW_PARAM_NAME, param.getName());
        Assert.assertEquals("Param value set", "/index.xhtml", param.getValue());

        SupportsType supports = ((SupportsType)genericPortlet.getAllSupports().get(0));
        Assert.assertEquals("Portlet mime type set", "text/html", supports.getMimeType());
        Assert.assertEquals("Should be only one Portlet Mode", 1, supports.getAllPortletMode().size());
        Assert.assertEquals("Portlet mode set", PortletMode.VIEW.toString(), supports.getAllPortletMode().get(0));
    }

    @Test
    public void testFacesPortletWithViewId() throws Exception {
        archive.createFacesPortlet("JSFPortlet", "JSF Portlet", "/home.xhtml");

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", GENERIC_FACES_PORTLET_CLASS, genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", "JSFPortlet", genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", "JSF Portlet", genericPortlet.getOrCreatePortletInfo().getTitle());

        List<InitParamType> allInitParams = genericPortlet.getAllInitParam();
        Assert.assertEquals("Should be one Init Param", 1, allInitParams.size());
        InitParamType param = allInitParams.get(0);
        Assert.assertEquals("Param name set", DEFAULT_VIEW_PARAM_NAME, param.getName());
        Assert.assertEquals("Param value set", "/home.xhtml", param.getValue());

        SupportsType supports = ((SupportsType)genericPortlet.getAllSupports().get(0));
        Assert.assertEquals("Portlet mime type set", "text/html", supports.getMimeType());
        Assert.assertEquals("Should be only one Portlet Mode", 1, supports.getAllPortletMode().size());
        Assert.assertEquals("Portlet mode set", PortletMode.VIEW.toString(), supports.getAllPortletMode().get(0));
    }

    @Test
    public void testFacesPortletWithViewEditIds() throws Exception {
        archive.createFacesPortlet("JSFPortlet", "JSF Portlet", "/home.xhtml", "/edit.xhtml");

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", GENERIC_FACES_PORTLET_CLASS, genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", "JSFPortlet", genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", "JSF Portlet", genericPortlet.getOrCreatePortletInfo().getTitle());

        List<InitParamType> allInitParams = genericPortlet.getAllInitParam();
        Assert.assertEquals("Should be two Init Param", 2, allInitParams.size());
        InitParamType param = allInitParams.get(0);
        Assert.assertEquals("Param name set", DEFAULT_VIEW_PARAM_NAME, param.getName());
        Assert.assertEquals("Param value set", "/home.xhtml", param.getValue());
        param = allInitParams.get(1);
        Assert.assertEquals("Param name set", DEFAULT_EDIT_PARAM_NAME, param.getName());
        Assert.assertEquals("Param value set", "/edit.xhtml", param.getValue());

        SupportsType supports = ((SupportsType)genericPortlet.getAllSupports().get(0));
        Assert.assertEquals("Portlet mime type set", "text/html", supports.getMimeType());
        Assert.assertEquals("Should be two Portlet Modes", 2, supports.getAllPortletMode().size());
        Assert.assertEquals("Portlet mode set", PortletMode.VIEW.toString(), supports.getAllPortletMode().get(0));
        Assert.assertEquals("Portlet mode set", PortletMode.EDIT.toString(), supports.getAllPortletMode().get(1));
    }

    @Test
    public void testFacesPortletWithViewEditHelpIds() throws Exception {
        archive.createFacesPortlet("JSFPortlet", "JSF Portlet", "/home.xhtml", "/edit.xhtml", "/help.xhtml");

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        List<PortletType<PortletDescriptor>> portlets = portletDescriptor.getAllPortlet();

        Assert.assertEquals("Only one portlet defined", 1, portlets.size());

        PortletType genericPortlet = portlets.get(0);

        Assert.assertEquals("Portlet class set", GENERIC_FACES_PORTLET_CLASS, genericPortlet.getPortletClass());
        Assert.assertEquals("Portlet name set", "JSFPortlet", genericPortlet.getPortletName());
        Assert.assertEquals("Portlet title set", "JSF Portlet", genericPortlet.getOrCreatePortletInfo().getTitle());

        List<InitParamType> allInitParams = genericPortlet.getAllInitParam();
        Assert.assertEquals("Should be two Init Param", 3, allInitParams.size());
        InitParamType param = allInitParams.get(0);
        Assert.assertEquals("Param name set", DEFAULT_VIEW_PARAM_NAME, param.getName());
        Assert.assertEquals("Param value set", "/home.xhtml", param.getValue());
        param = allInitParams.get(1);
        Assert.assertEquals("Param name set", DEFAULT_EDIT_PARAM_NAME, param.getName());
        Assert.assertEquals("Param value set", "/edit.xhtml", param.getValue());
        param = allInitParams.get(2);
        Assert.assertEquals("Param name set", DEFAULT_HELP_PARAM_NAME, param.getName());
        Assert.assertEquals("Param value set", "/help.xhtml", param.getValue());

        SupportsType supports = ((SupportsType)genericPortlet.getAllSupports().get(0));
        Assert.assertEquals("Portlet mime type set", "text/html", supports.getMimeType());
        Assert.assertEquals("Should be three Portlet Modes", 3, supports.getAllPortletMode().size());
        Assert.assertEquals("Portlet mode set", PortletMode.VIEW.toString(), supports.getAllPortletMode().get(0));
        Assert.assertEquals("Portlet mode set", PortletMode.EDIT.toString(), supports.getAllPortletMode().get(1));
        Assert.assertEquals("Portlet mode set", PortletMode.HELP.toString(), supports.getAllPortletMode().get(2));
    }

    @Test
    public void testFacesPortletAgainstXmlFile() throws Exception {
        archive.createFacesPortlet("richfaces-simple", "RichFaces 4 Simple Portlet");

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        String generatedOutput = portletDescriptor.exportAsString();
        String expectedOutput = getResourceContents("src/test/resources/genericFacesPortlet.xml");
        assertIdenticalXml(expectedOutput, generatedOutput);
    }

    @Test
    public void testFacesPortletAgainstXmlFileWithViewId() throws Exception {
        archive.createFacesPortlet("richfaces-simple", "RichFaces 4 Simple Portlet", "index.xhtml");

        PortletDescriptor portletDescriptor = getDescriptor(archive);
        String generatedOutput = portletDescriptor.exportAsString();
        String expectedOutput = getResourceContents("src/test/resources/genericFacesPortlet.xml");
        assertIdenticalXml(expectedOutput, generatedOutput);
    }

    private PortletDescriptor getDescriptor(PortletArchive archive) {
        Node portletNode = archive.get(PORTLET_XML_PATH);
        return Descriptors.importAs(PortletDescriptor.class).fromStream(portletNode.getAsset().openStream());
    }

    private String getResourceContents(String resourceName) throws Exception {
        final BufferedReader reader = new BufferedReader(new FileReader(resourceName));
        final StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        return builder.toString();
    }

    private void assertIdenticalXml(String expected, String actual) {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setNormalizeWhitespace(true);

        try {
            Diff diff = new Diff(expected, actual);
            Assert.assertTrue("Pieces of xml are similar " + diff, diff.similar());
            Assert.assertTrue("but are they identical? " + diff, diff.identical());
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class DummyPortlet implements Portlet {
        @Override
        public void init(PortletConfig config) throws PortletException {
        }
        @Override
        public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        }
        @Override
        public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        }
        @Override
        public void destroy() {
        }
    }
}
