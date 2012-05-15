## Arquillian Portal Extension

### What is this?

**Arquillian Portal Extension** was created to help you write tests for portlets.

Currently it supports:

* Injection of portal specific @ArquillianResource URL with @PortalURL
* Ability for portal containers to add extra deployments into the container prior to deployment of the test archive

On it's own this extension doesn't do much, so you will need a portal container specific implementation of this extension
to use it. Implementations for GateIn and Pluto portal containers can be found at
[JBoss Portlet Bridge](http://github.com/jbossportletbridge).

### Code example
---

    @RunWith(Arquillian.class)
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

        @ArquillianResource
        @PortalURL
        URL portalURL;

        @Test
        @RunAsClient
        public void renderFacesPortlet() throws Exception {
            WebClient client = new WebClient();
            client.setAjaxController(new NicelyResynchronizingAjaxController());
            HtmlPage body = webClient.getPage(portalURL.toExternalForm());
            HtmlElement element = body.getElementById("output");
            assertNotNull("Check what page contains output element", element);
            Assert.assertThat("Verify that the portlet was deployed and returns the expected result", element.asText(),
                containsString(Bean.HELLO_JSF_PORTLET));
        }
    }

