## Pluto Extension for Arquillian

### What is this?

The **Pluto Extension** is an implementation of the [Arquillian Extension for Portals](https://github.com/arquillian/arquillian-extension-portal)
that enables the Pluto Portlet Container to be used on Jetty for testing portlets.

Depending on how you want to use the **Pluto Extension** will determine which artifacts you need to import in your pom.

* Using the **Pluto Extension** with a different container to Jetty only requires **pluto-container**.
* Using the **Pluto Extension** with Jetty requires **pluto-jetty-bom**.
* Using the **Pluto Extension** with JSF2 portlets requires **pluto-extension-jsf** and then either **pluto-container** or **pluto-jetty-bom** depending on which container your tests will be deployed to.