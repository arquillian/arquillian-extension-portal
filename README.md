# ![Obsolete](https://dummyimage.com/700x100/fff/f00&text=This%20Repository%20Is%20Obsolete!)

We don't maintain this code base anymore. If you are interested in picking it up from where we left please reach out to us through [Arquillian forum](http://discuss.arquillian.org/).

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
* [GateIn](/portal/impl/impl-gatein/README.md)
* [Pluto](/portal/impl/impl-pluto/README.md)

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

### Warp Portlets

We can now take advantage of Warp to test the internals of our portlets!

For testing non JSF portlets, such as those extending `GenericPortlet`, we can use `@BeforePortletPhase` and `@AfterPortletPhase` on our Warp inspection methods. The possible values for these annotations are `ACTION`, `EVENT`, `RENDER`, and `RESOURCE`, signifying which type of portlet request we want the method to run before or after.

An example usage:

    @RunWith(Arquillian.class)
    @PortalTest
    @WarpTest
    public class PortletWarpTest {

        @Deployment
        public static PortletArchive getDeployment() {
        ...
        }

        @ArquillianResource
        @PortalURL
        URL portalURL;

        @Drone
        WebDriver browser;

        @Test
        @RunAsClient
        public void portletAction() throws Exception {

            browser.navigate().to(portalURL);

            Warp
                .initiate(new Activity() {
                    public void perform() {
                        submitButton.click();
                    }
                })
                .group()
                    .observe(request().index(1))
                    .inspect(new Inspection() {
                        private static final long serialVersionUID = 1L;

                        @ArquillianResource
                        ActionResponse actionResponse;

                        @BeforePortletPhase(Phase.ACTION)
                        public void beforeActionRequest() {
                            String[] values = actionResponse.getRenderParameterMap().get("data");
                            assertNull("Render parameter for data should not be set.", values);
                        }

                        @AfterPortletPhase(Phase.ACTION)
                        public void afterActionRequest() {
                            String[] values = actionResponse.getRenderParameterMap().get("data");
                            assertTrue("Render parameter for data should be set.", values.length == 1);
                            assertEquals("Render parameter set to incorrect value.", BasicPortlet.ACTION, values[0]);
                        }
                    })
                .group()
                    .observe(request().index(2))
                    .inspect(new Inspection() {
                        private static final long serialVersionUID = 1L;

                        @ArquillianResource
                        RenderRequest renderRequest;

                        @BeforePortletPhase(Phase.RENDER)
                        public void beforeRenderRequest() {
                            String value = renderRequest.getParameter("data");
                            assertEquals("Render parameter set to incorrect value.", BasicPortlet.ACTION, value);
                        }
                    })
                .execute();
        }
    }

The above example also shows how we can inject the request and response objects into the `Inspection` to perform our assertions. All portlet lifecycle specific request and response objects, such as `ActionRequest`, are available for injection, in addition to the generic `PortletRequest` and `PortletResponse` objects.

An important point about the above example is the use of Warp groups and differentiating them by the first and second request. Most portlet containers implement the pattern of performing a redirect between an `ActionRequest` or `EventRequest` and the subsequent `RenderRequest`, so we need to bear this in mind when writing our Warp inspections. We can easily distinguish between which request we want to act on by using `observe()`, such as the following for an `ActionRequest`:

    .group()
        .observe(request().index(1))
        .inspect(new Inspection() {...})

This will inform Warp that we want the inspection to run against the first request that it monitors.

We're also able to test our JSF portlets by enhancing the Warp JSF extension to handle portlets. JSF portlets can still use `@BeforePortletPhase` and `@AfterPortletPhase` on inspection methods, but it's important to realize that these annotations won't provide access to JSF internals as they are acting purely in the context of a portlet request. To fully interact with the JSF lifecycle we need to use the Warp JSF annotations along with `@PortletPhase` to signify in which portlet lifecycle phase we want to interact with JSF.

An example of inspecting a JSF portlet:

    @RunWith(Arquillian.class)
    @PortalTest
    @WarpTest
    public class JSFPortletWarpTest {

        @Deployment
        public static PortletArchive getDeployment() {
        ...
        }

        @ArquillianResource
        @PortalURL
        URL portalURL;

        @Drone
        WebDriver browser;

        @Test
        @RunAsClient
        public void portletAction() throws Exception {

            browser.navigate().to(portalURL);

            Warp
                .initiate(new Activity() {
                    public void perform() {
                        inputField.clear();
                        inputField.sendKeys("newValue");
                        submitButton.click();
                    }
                })
                .group()
                    .observe(request().index(1))
                    .inspect(new Inspection() {
                        private static final long serialVersionUID = 1L;


                        @ManagedProperty("#{bean}")
                        Bean bean;

                        @PortletPhase(ACTION) @BeforePhase(Phase.UPDATE_MODEL_VALUES)
                        public void testBeanValueBeforeUpdate() {
                            assertEquals("Bean value should not be updated yet.", "originalValue", bean.getText());
                        }

                        @PortletPhase(ACTION) @AfterPhase(Phase.UPDATE_MODEL_VALUES)
                        public void testBeanValueAfterUpdate() {
                            assertEquals("Bean value should now be updated.", "newValue", bean.getText());
                        }
                    })
                .group()
                    .observe(request().index(2))
                    .inspect(new Inspection() {
                        private static final long serialVersionUID = 1L;

                        @ManagedProperty("#{bean}")
                        Bean bean;

                        @PortletPhase(RENDER) @BeforePhase(Phase.RENDER_RESPONSE)
                        public void testBeanValueBeforeRenderResponse() {
                            assertEquals("Bean value should still contain new value.", "newValue", bean.getText());
                        }
                    })
                .execute();
        }
    }

The above example will execute two inspection methods, before and after, around the `UPDATE_MODEL_VALUES` JSF lifecycle phase when part of an `ActionRequest` during the first group, and a separate group that will execute an inspection method before the JSF `RENDER_RESPONSE` lifecycle phase as part of a `RenderRequest`.

Notice that we can also use the same `@ManagedProperty` from Warp JSF to inject our JSF beans into the `Inspection`.
