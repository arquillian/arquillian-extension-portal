## GateIn Extension for Arquillian

### What is this?

The **GateIn Extension** is an implementation of the [Arquillian Extension for Portals](https://github.com/arquillian/arquillian-extension-portal)
that enables the GateIn Portlet Container to be used on different containers for testing portlets.

Using **GateIn Extension** in your project for testing requires that a version of pc-embed be added as a dependency so that this extension can access it.
For that, we need the following dependency:

    <dependency>
        <groupId>org.gatein.pc</groupId>
        <artifactId>pc-embed</artifactId>
        <version>2.4.0.Final</version>
    </dependency>
