## GateIn Implementation for Arquillian Portal Extension

### What is this?

It enables the GateIn Portlet Container to be used in different containers for testing portlets.

Using the **GateIn Implementation** in your project for testing requires that a version of pc-embed be added as a dependency so that this extension can access it.
For that, we need the following dependency:

    <dependency>
        <groupId>org.gatein.pc</groupId>
        <artifactId>pc-embed</artifactId>
        <version>2.4.3.Final</version>
    </dependency>

And then we need to add a dependency for the **GateIn Implementation**:

    <dependency>
        <groupId>org.jboss.arquillian.extension</groupId>
        <artifactId>arquillian-portal-impl-gatein</artifactId>
        <version>1.1.0.Final-SNAPSHOT</version>
    </dependency>
