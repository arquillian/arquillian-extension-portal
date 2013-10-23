## Pluto Implementation for Arquillian Portal Extension

### What is this?

It enables the Pluto Portlet Container to be used in Jetty for testing portlets.

Depending on how you want to use the **Pluto Implementation** will determine which artifacts you need to import in your pom.

* Using the **Pluto Implementation** with a different container to Jetty only requires **arquillian-portal-impl-pluto-container**.
* Using the **Pluto Implementation** with Jetty requires **arquillian-portal-impl-pluto-jetty-bom**.
* Using the **Pluto Implementation** with JSF2 portlets requires **arquillian-portal-impl-pluto-jsf** and then either **arquillian-portal-impl-pluto-container** or **arquillian-portal-impl-pluto-jetty-bom** depending on which container your tests will be deployed to.