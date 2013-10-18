## Arquillian Portal Extension

### What is this?

**Arquillian Portal Extension** was created to help you write tests for portlets.

Currently it supports:

* Injection of portal specific `@ArquillianResource` URL with `@PortalURL`
* `@PortalURL` supports following values:
    * null or "" - URL to page with all deployed portlets on a single page
    * "MyPortlet" - URL to page with only "MyPortlet" loaded
    * {"MyPortlet", "YourPortlet"} - URL to page with listed portlets on a single page.
* Ability for portlet containers to add extra deployments into the runtime container prior to deployment of the test archive
* `@PortalTest` marker annotation on test class to allow special processing by container specific implementations

As part of the extension we provide implementations for the following portlet containers:
* GateIn

### Code example
---

    @RunWith(Arquillian.class)
    @PortalTest
    public class PortletTest {
        @Deployment
        public static WebArchive createDeployment() {
            return ShrinkWrap
                .create(WebArchive.class)
                .addAsLibraries(
                    Maven.resolver().loadPomFromFile("pom.xml")
                        .resolve("org.jboss.portletbridge:portletbridge-impl")
                        .withTransitivity
                        .asFile())
                .addAsWebInfResource("WEB-INF/web.xml", "web.xml")
                .addAsWebInfResource("WEB-INF/faces-config.xml")
                .addAsWebInfResource("WEB-INF/portlet.xml", "portlet.xml");
                .addClass(Bean.class)
                .addAsWebResource("output.xhtml", "home.xhtml")
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
        }

        protected static final By OUTPUT_FIELD = By.id("output");

        @ArquillianResource
        @PortalURL
        URL portalURL;

        @Drone
        WebDriver browser;

        @Test
        @RunAsClient
        public void renderFacesPortlet() throws Exception {
            browser.get(portalURL.toString());
            assertNotNull("Check that page contains output element", browser.findElement(OUTPUT_FIELD));
            assertTrue("Portlet should return: " + Bean.HELLO_JSF_PORTLET,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, Bean.HELLO_JSF_PORTLET).apply(driver));
        }
    }
---

### Shrinkwrap PortletArchive

We can now simplify the process of creating an Archive for portlet testing with `PortletArchive`.

The `PortletArchive` allows us to create definitions for portlets that extend `GenericPortlet` or `GenericFacesPortlet` by pre-populating `portlet.xml` for us.

To create a `PortletArchive` with a `portlet.xml` definition of a portlet called `MyPortlet` we do:

    ShrinkWrap.create(PortletArchive.class)
              .createSimplePortlet(MyPortlet.class);

Using `PortletArchive`, the above code sample for the deployment method now becomes:

    @Deployment
    public static PortletArchive createDeployment() {
        return ShrinkWrap
                .create(PortletArchive.class)
                .createFacesPortlet("SimpleTest", "Simple Test Portlet", "home.xhtml")
                .addAsLibraries(
                    Maven.resolver().loadPomFromFile("pom.xml")
                        .resolve("org.jboss.portletbridge:portletbridge-impl")
                        .withTransitivity
                        .asFile())
                .addAsWebInfResource("WEB-INF/web.xml", "web.xml").addAsWebInfResource("WEB-INF/faces-config.xml")
                .addClass(Bean.class)
                .addAsWebResource("output.xhtml", "home.xhtml")
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }
