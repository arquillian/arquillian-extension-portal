## Arquillian Portal Extension

### What is this?

**Arquillian Portal Extension** was created to help you write tests for portlets.

Currently it supports:

* Injection of portal specific @ArquillianResource URL with @PortalURL
* @PortalURL supports following values:
    * null or "" - URL to page with all deployed portlets on a single page
    * "MyPortlet" - URL to page with only "MyPortlet" loaded
    * {"MyPortlet", "YourPortlet"} - URL to page with listed portlets on a single page.
* Ability for portlet containers to add extra deployments into the runtime container prior to deployment of the test archive
* @PortalTest marker annotation on test class to allow special processing by container specific implementations

On it's own this extension doesn't do much, so you will need a portlet container specific implementation of this extension
to use it. Implementations for GateIn and Pluto portlet containers can be found at
[JBoss Portlet Bridge](http://github.com/jbossportletbridge).

### Code example
--- 

    @RunWith(Arquillian.class)
    @PortalTest
    public class PortletTest {
        @Deployment
        public static Archive<?> createDeployment() {
            return ShrinkWrap
                .create(WebArchive.class)
                .addAsLibraries(
                    DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
                        .artifacts("org.jboss.portletbridge:portletbridge-api").resolveAsFiles())
                .addAsLibraries(
                    DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
                        .artifacts("org.jboss.portletbridge:portletbridge-impl").resolveAsFiles())
                .addAsWebInfResource("WEB-INF/web.xml", "web.xml").addAsWebInfResource("WEB-INF/faces-config.xml")
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

